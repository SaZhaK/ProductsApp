var saveBtn = document.getElementById('save_btn');
saveBtn.onclick = function() {
    var table = document.getElementById("table");
    var rows = table.rows;

    var dates = [];
    var datesIdx = 0;

    var trades = [];
    var tradeIdx = 0;

    for (let i = 0; i < rows.length; i++) {
      var cells = rows[i].cells;

      var trade = {};
      var product = {};
      var amounts = [];
      var amountIdx = 0;

      for (let j = 0; j < cells.length; j++) {
        var input = cells[j].children[0];
        if (input != null) {
            var value = input.value;

            if (i == 0) {
                dates[datesIdx++] = value;
            } else if (j == 0) {
                product.id = value;
            } else if (j == 1) {
                product.name = value;
            } else {
                amounts[amountIdx++] = value;
            }
        }
      }

      trade.product = product;
      trade.amounts = amounts;

      trades[tradeIdx++] = trade;
    }

    var result = {};
    result.dates = dates;
    result.trades = trades;

    var jsonResult = JSON.stringify(result);
    console.log(jsonResult);

    var response = fetch('/save', {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: jsonResult
    })
}
