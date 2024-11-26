package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.bll.service.CurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController() throws Exception {
        this.currencyService = new CurrencyService();
    }

    @GetMapping
    public List<Currency> getAll() throws Exception {
        return this.currencyService.all();
    }

    @PutMapping("/import")
    public void importCurrenciesFromCSV() throws Exception {
        // TODO - implement this method
    }
}
