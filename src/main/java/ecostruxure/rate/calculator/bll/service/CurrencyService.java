package ecostruxure.rate.calculator.bll.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.bll.MissingCurrenciesException;
import ecostruxure.rate.calculator.dal.db.CurrencyDAO;
import ecostruxure.rate.calculator.dal.dao.ICurrencyDAO;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class CurrencyService {

    private final ICurrencyDAO currencyDAO;

    public CurrencyService() throws Exception {
        this.currencyDAO = new CurrencyDAO();
    }

    public List<Currency> all() throws Exception {
        return currencyDAO.all();
    }

    public Currency get(String code) throws Exception {
        return currencyDAO.get(code);
    }

    public void importCurrencies(Currency[] currencies) throws Exception {
        for (Currency currency : currencies) {
            this.currencyDAO.addCurrency(currency);
        }
    }
}
