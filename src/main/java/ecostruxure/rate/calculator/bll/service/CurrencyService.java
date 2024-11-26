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
    private static final String EUR = "EUR";
    private static final String USD = "USD";

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
        this.currencyDAO.removeAllCurrencies();
        for (Currency currency : currencies) {
            this.currencyDAO.addCurrency(currency);
        }
    }

    public void importCurrenciesFromCSV(String filePath) throws Exception {
        List<Currency> currencies = parseCSV(filePath);

        boolean hasEUR = currencies.stream().anyMatch(currency -> EUR.equalsIgnoreCase(currency.currencyCode()));
        boolean hasUSD = currencies.stream().anyMatch(currency -> USD.equalsIgnoreCase(currency.currencyCode()));

        if (!hasEUR || !hasUSD) {
            throw new MissingCurrenciesException("CSV must contain both EUR and USD entries.");
        }

        currencyDAO.removeAllCurrencies();
        currencyDAO.addCurrencies(currencies);
    }

    static List<Currency> parseCSV(String filePath) throws IOException, CsvException {
        Map<String, Currency> currencyMap = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                processRecord(record, currencyMap);
            }
        }
        return new ArrayList<>(currencyMap.values());
    }

    private static void processRecord(String[] record, Map<String, Currency> currencyMap) {
        if (record.length >= 3 && !record[1].trim().isEmpty() && !record[2].trim().isEmpty()) {
            String currencyCode = record[0].trim();
            try {
                BigDecimal eurConversionRate = getEurConversionRate(currencyCode, record[1].trim());
                BigDecimal usdConversionRate = getUsdConversionRate(currencyCode, record[2].trim());
                currencyMap.put(currencyCode, new Currency(currencyCode, eurConversionRate, usdConversionRate));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // we just skip line if invalid format
            }
        }
    }

    private static BigDecimal getEurConversionRate(String currencyCode, String rate) {
        if (currencyCode.equalsIgnoreCase(EUR)) return BigDecimal.ONE;
        else return new BigDecimal(rate);
    }

    private static BigDecimal getUsdConversionRate(String currencyCode, String rate) {
        if (currencyCode.equalsIgnoreCase(USD)) return BigDecimal.ONE;
        else return new BigDecimal(rate);
    }
}
