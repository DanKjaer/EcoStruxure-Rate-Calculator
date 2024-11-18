package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Currency;

import java.util.List;

public interface ICurrencyDAO {
    /**
     * Gets a currency by its code.
     * @param code string
     * @return {@link Currency}
     * @throws Exception
     */
    Currency get(String code) throws Exception;

    /**
     * Gets all currencies.
     * @return {@link List<Currency>}<{@link Currency}>
     * @throws Exception
     */
    List<Currency> all() throws Exception;

    /**
     * Adds a list of currencies to the database.
     * @param currencies {@link List<Currency>}<{@link Currency}>
     * @throws Exception
     */
    void addCurrencies(List<Currency> currencies) throws Exception;

    /**
     * Removes all currencies from database.
     * @throws Exception
     */
    void removeAllCurrencies() throws Exception;
}
