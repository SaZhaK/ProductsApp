package application.controller;

import application.dto.ResultDto;
import application.dto.TradeDto;
import application.entity.Product;
import application.entity.Trade;
import application.repository.ProductRepository;
import application.repository.TradeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/save")
@AllArgsConstructor
public class SaveController {

	private final ProductRepository productRepository;
	private final TradeRepository tradeRepository;

	@PostMapping
	public String save(@RequestBody ResultDto resultDto) {
		List<LocalDate> dates = resultDto.getDates();
		List<TradeDto> trades = resultDto.getTrades().stream()
				.filter(trade -> trade.getProduct() != null)
				.collect(Collectors.toList());

		for (TradeDto resultTrade : trades) {
			Product product = resultTrade.getProduct();
			List<Integer> amounts = resultTrade.getAmounts();

			for (int i = 0; i < amounts.size(); i++) {
				LocalDate date = dates.get(i);
				Integer amount = amounts.get(i);

				if (product.getId() == 0 && product.getName().equals("")) continue;
				if (date == null) continue;
				if (amount == null) continue;

				if (product.getId() != 0) {
					List<Trade> tradesFromDb = tradeRepository.findAll();
					List<Trade> found = tradesFromDb.stream()
							.filter(trade -> trade.getProduct().getId().equals(product.getId()) && trade.getDate().equals(date))
							.collect(Collectors.toList());

					if (!found.isEmpty()) {
						Trade trade = new Trade(found.get(0).getId(), product, amount, date);
						tradeRepository.save(trade);
					} else {
						Long newTradeId = (long) tradesFromDb.size() + 1;
						Trade trade = new Trade(newTradeId, product, amount, date);
						tradeRepository.save(trade);
					}
				} else {
					Long newTradeId = (long) tradeRepository.findAll().size() + 1;
					Long newProductId = (long) productRepository.findAll().size() + 1;
					Product newProduct = new Product(newProductId, product.getName());
					productRepository.save(newProduct);

					Trade trade = new Trade(newTradeId, newProduct, amount, date);
					tradeRepository.save(trade);

					product.setId(newProductId);
				}

			}
		}

		return "redirect:/";
	}
}
