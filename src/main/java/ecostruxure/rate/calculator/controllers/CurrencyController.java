package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.bll.service.CurrencyService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController() throws Exception {
        this.currencyService = new CurrencyService();
    }

    @PutMapping("/import")
    public void importCurrenciesFromCSV() throws Exception {
        // TODO - implement this method
    }
}
