package ecostruxure.rate.calculator.bll.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.bll.MissingCurrenciesException;
import ecostruxure.rate.calculator.dal.interfaces.ICurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
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
        return currencyRepository.findById(code).orElse(null);
    }

    public void importCurrencies(Currency[] currencies) throws Exception {
        currencyRepository.saveAll(Arrays.asList(currencies));
    }
}
