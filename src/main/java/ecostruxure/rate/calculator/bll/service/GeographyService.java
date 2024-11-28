package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.IGeographyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeographyService {

    @Autowired
    private IGeographyRepository geographyRepository;

    public GeographyService() throws IOException {
    }

    public Iterable<Geography> all() throws Exception {
        return geographyRepository.findAll();
    }

    public Geography getById(int id) throws Exception {
        return geographyRepository.findById(id).orElseThrow(() -> new Exception("Geography not found."));
    }

//    public List<Geography> getGeographies() throws Exception {
//        return geographyDAO.getGeographies();
//    }
//
//    public List<Geography> allExceptCountries() throws Exception {
//        return geographyDAO.allExceptCountries();
//    }
//
//    public Geography get(int id) throws Exception {
//        return geographyDAO.get(id);
//    }
//
//    public Geography getByCountryId(int countryId) throws Exception {
//        List<Geography> geographies = geographyDAO.all();
//        return geographies.stream()
//                .filter(geography -> geography.id() == countryId)
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * Creates a new geography.<br>
//     * Removes all duplicate and empty countries from countries list, requires at least two countries.<br>
//     *
//     * @param geography The geography to create.<br>
//     *                  Must not be null. Must have a name. Must not be predefined. Must not have an id.
//     * @param countries The countries to associate with the geography.<br>
//     *                  Must not be null, empty, must at least contain two countries.
//     * @return The newly created geography, same data as the input geography. But with the id set.
//     * @throws Exception If the geography could not be created.<br>
//     *                   If the countries could not be associated with the geography.<br>
//     *                   If the one of the countries do not exist in the data source.
//     * @throws IllegalArgumentException If the geography is null, has no name, is predefined, has an id, or countries is null, empty, contains less than two countries.
//     */
//    public Geography create(Geography geography, List<Country> countries) throws Exception {
//        Objects.requireNonNull(geography, "Geography must not be null.");
//        Objects.requireNonNull(geography.name(), "Geography must have a name.");
//        Objects.requireNonNull(countries, "Countries must not be null.");
//        if (geography.name().isEmpty()) throw new IllegalArgumentException("Geography must have a name.");
//        if (geography.predefined()) throw new IllegalArgumentException("Geography cannot be predefined.");
//        if (geography.id() > 0) throw new IllegalArgumentException("Geography cannot have an id.");
//
//        var countriesWithoutEmpty = removeEmpty(countries);
//        var countriesWithoutDuplicates = removeDuplicates(countriesWithoutEmpty);
//        if (countriesWithoutDuplicates.size() < 2) throw new IllegalArgumentException("Countries must at least contain two countries.");
//
//        return geographyDAO.create(geography, countriesWithoutDuplicates);
//    }
//
//    private List<Country> removeEmpty(List<Country> countries) {
//        return countries.stream()
//                        .filter(country -> country.code() != null &&
//                                !country.code().isEmpty())
//                        .collect(Collectors.toList());
//    }
//
//    private List<Country> removeDuplicates(List<Country> countries) {
//        return countries.stream()
//                        .distinct()
//                        .collect(Collectors.toList());
//    }
//
//    public boolean exists(List<Country> countries) throws Exception {
//        if (countries.size() == 1) return true;
//        return geographyDAO.exists(countries);
//    }
//
//    public Geography get(List<Country> countries) throws Exception {
//        return geographyDAO.get(countries);
//    }
}
