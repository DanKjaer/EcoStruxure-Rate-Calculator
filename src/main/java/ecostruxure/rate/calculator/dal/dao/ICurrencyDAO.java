package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Currency;

import java.util.List;

public interface ICurrencyDAO {
    Currency get(String code) throws Exception;
    List<Currency> all() throws Exception;
    void addCurrencies(List<Currency> currencies) throws Exception;
    void removeAllCurrencies() throws Exception;
}
