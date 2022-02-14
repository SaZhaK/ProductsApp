package application.controller;

import application.dto.TradeDto;
import application.entity.Product;
import application.entity.Trade;
import application.repository.TradeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class MainController {

	private final TradeRepository tradeRepository;

	@GetMapping
	public String getMain(Model model) {
		List<Trade> tradesFromDb = tradeRepository.findAll();

		List<LocalDate> dates = tradesFromDb.stream()
				.map(Trade::getDate)
				.distinct()
				.collect(Collectors.toList());
		dates.add(null);

		List<TradeDto> trades = new ArrayList<>();

		Map<Product, List<Trade>> tradesByProduct = new HashMap<>();
		for (Trade trade : tradesFromDb) {
			Product product = trade.getProduct();
			List<Trade> tradeList = tradesByProduct.getOrDefault(product, new ArrayList<>());
			tradeList.add(trade);
			tradesByProduct.put(product, tradeList);
		}

		for (Product product : tradesByProduct.keySet()) {
			List<Trade> tradeList = tradesByProduct.get(product);
			tradeList.sort(Comparator.comparing(Trade::getDate));

			List<Integer> amounts = tradeList.stream().map(Trade::getAmount).collect(Collectors.toList());
			amounts.add(null);
			TradeDto tradeDto = new TradeDto(product, amounts);
			trades.add(tradeDto);
		}

		List<Integer> emptyList = new ArrayList<>();
		for (int i = 0; i < dates.size(); i++) {
			emptyList.add(null);
		}

		TradeDto emptyTradeDto = new TradeDto(new Product(0L, ""), emptyList);
		trades.add(emptyTradeDto);

		model.addAttribute("dates", dates);
		model.addAttribute("trades", trades);

		return "main_page";
	}
}
