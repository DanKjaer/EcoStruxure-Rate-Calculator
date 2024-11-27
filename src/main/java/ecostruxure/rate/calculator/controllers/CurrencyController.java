package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.bll.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    public CurrencyController() throws Exception {
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
