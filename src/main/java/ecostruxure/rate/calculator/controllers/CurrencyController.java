package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.bll.currency.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) throws Exception {
        this.currencyService = currencyService;
    }

    @GetMapping
    public Iterable<Currency> getAll() throws Exception {
        return this.currencyService.all();
    }

    @PutMapping("/import")
    public void importCurrenciesFromCSV(@RequestBody Currency[] currencies) throws Exception {
        this.currencyService.importCurrencies(currencies);
    }
}
