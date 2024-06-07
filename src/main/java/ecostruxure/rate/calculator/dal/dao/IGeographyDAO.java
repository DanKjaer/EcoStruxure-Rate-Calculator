package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;

import java.util.ArrayList;
import java.util.List;

public interface IGeographyDAO {
    ArrayList<Geography> all() throws Exception;

    List<Geography> allExceptCountries() throws Exception;

    List<Geography> allOnlyCountries() throws Exception;

    Geography get(int id) throws Exception;

    /**
     * Adds a new geography to the data source.
     *
     * @param geography The geography to add.
     * @param countries The countries to associate with the geography.
     * @return The newly created geography, same data as the input geography. But with the id set.
     * @throws Exception If the geography could not be created.
     */
    Geography create(Geography geography, List<Country> countries) throws Exception;

    /**
     * Checks if a geography with the specified countries already exists.
     *
     * @param countries The list of countries to check.
     * @return true if a geography with the specified countries exists, false otherwise.
     * @throws Exception if an error occurred during the check.
     */
    boolean exists(List<Country> countries) throws Exception;

    Geography get(List<Country> countries) throws Exception;
}
