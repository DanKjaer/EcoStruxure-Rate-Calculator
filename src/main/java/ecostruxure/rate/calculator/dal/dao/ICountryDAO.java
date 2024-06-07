package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;

import java.util.ArrayList;
import java.util.List;

public interface ICountryDAO {
    Country get(String code) throws Exception;

    List<Country> all() throws Exception;

    /**
     * Gets all countries associated with a geography.
     *
     * @param geography The geography to get the countries for.
     * @return A list of countries associated with the geography.
     * @throws Exception If the countries could not be retrieved.
     */
    List<Country> allByGeography(Geography geography) throws Exception;
}
