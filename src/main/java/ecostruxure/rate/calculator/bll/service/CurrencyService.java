package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.dal.ICurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CurrencyService {

    @Autowired
    private ICurrencyRepository currencyRepository;

    public CurrencyService() throws Exception {
    }

    public Iterable<Currency> all() throws Exception {
        return currencyRepository.findAll();
    }

    public Currency get(String code) throws Exception {
        return currencyRepository.findById(code).orElseThrow(() -> new Exception(code + " not found"));
    }

    public void importCurrencies(Currency[] currencies) throws Exception {
        currencyRepository.saveAll(Arrays.asList(currencies));
    }
}
