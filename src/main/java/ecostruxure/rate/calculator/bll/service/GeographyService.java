package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.db.GeographyDAO;
import ecostruxure.rate.calculator.dal.dao.IGeographyDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GeographyService {
    private final IGeographyDAO geographyDAO;

    public GeographyService() throws IOException {
        this.geographyDAO = new GeographyDAO();
    }

    public ArrayList<Geography> all() throws Exception {
        return geographyDAO.all();
    }

    public List<Geography> allExceptCountries() throws Exception {
        return geographyDAO.allExceptCountries();
    }

    public Geography get(int id) throws Exception {
        return geographyDAO.get(id);
    }

    /**
     * Creates a new geography.<br>
     * Removes all duplicate and empty countries from countries list, requires at least two countries.<br>
     *
     * @param geography The geography to create.<br>
     *                  Must not be null. Must have a name. Must not be predefined. Must not have an id.
     * @param countries The countries to associate with the geography.<br>
     *                  Must not be null, empty, must at least contain two countries.
     * @return The newly created geography, same data as the input geography. But with the id set.
     * @throws Exception If the geography could not be created.<br>
     *                   If the countries could not be associated with the geography.<br>
     *                   If the one of the countries do not exist in the data source.
     * @throws IllegalArgumentException If the geography is null, has no name, is predefined, has an id, or countries is null, empty, contains less than two countries.
     */
    public Geography create(Geography geography, List<Country> countries) throws Exception {
        Objects.requireNonNull(geography, "Geography must not be null.");
        Objects.requireNonNull(geography.name(), "Geography must have a name.");
        Objects.requireNonNull(countries, "Countries must not be null.");
        if (geography.name().isEmpty()) throw new IllegalArgumentException("Geography must have a name.");
        if (geography.predefined()) throw new IllegalArgumentException("Geography cannot be predefined.");
        if (geography.id() > 0) throw new IllegalArgumentException("Geography cannot have an id.");

        var countriesWithoutEmpty = removeEmpty(countries);
        var countriesWithoutDuplicates = removeDuplicates(countriesWithoutEmpty);
        if (countriesWithoutDuplicates.size() < 2) throw new IllegalArgumentException("Countries must at least contain two countries.");

        return geographyDAO.create(geography, countriesWithoutDuplicates);
    }

    private List<Country> removeEmpty(List<Country> countries) {
        return countries.stream()
                        .filter(country -> country.code() != null &&
                                !country.code().isEmpty())
                        .collect(Collectors.toList());
    }

    private List<Country> removeDuplicates(List<Country> countries) {
        return countries.stream()
                        .distinct()
                        .collect(Collectors.toList());
    }

    public boolean exists(List<Country> countries) throws Exception {
        if (countries.size() == 1) return true;
        return geographyDAO.exists(countries);
    }

    public Geography get(List<Country> countries) throws Exception {
        return geographyDAO.get(countries);
    }
}
