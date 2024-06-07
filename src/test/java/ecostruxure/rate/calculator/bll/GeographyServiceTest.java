package ecostruxure.rate.calculator.bll;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.dal.db.CountryDAO;
import ecostruxure.rate.calculator.dal.dao.ICountryDAO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GeographyServiceTest {
    private final GeographyService geographyService;
    private final ICountryDAO countryDAO;

    public GeographyServiceTest() {
        try {
            geographyService = new GeographyService();
            countryDAO = new CountryDAO();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createNullCountry() {
        assertThrows(NullPointerException.class, () -> geographyService.create(
                null,
                List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI")))
        );
    }

    @Test
    void createNullName() {
        assertThrows(NullPointerException.class, () -> geographyService.create(
                new Geography(null),
                List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI")))
        );
    }

    @Test
    void createEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> geographyService.create(
                new Geography(""),
                List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI")))
        );
    }

    @Test
    void createIdGreaterThanZero() {
        assertThrows(IllegalArgumentException.class, () -> geographyService.create(
                new Geography(1, "Nordic", false),
                List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI")))
        );
    }

    @Test
    void createPredefined() {
        assertThrows(IllegalArgumentException.class, () -> geographyService.create(
                new Geography(0, "Nordic", true),
                List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI")))
        );
    }

    @Test
    void createNullCountries() {
        assertThrows(NullPointerException.class, () -> geographyService.create(
                new Geography("Nordic"),
                null)
        );
    }

    @Test
    void createEmptyCountries() {
        assertThrows(IllegalArgumentException.class, () -> geographyService.create(
                new Geography("Nordic"),
                List.of())
        );
    }

    @Test
    void createSingleCountry() {
        assertThrows(IllegalArgumentException.class, () -> geographyService.create(
                new Geography("Denmark"),
                List.of(new Country("DK")))
        );
    }

    @Test
    void createCountriesWithNulls() {
        assertThrows(NullPointerException.class, () -> geographyService.create(
                new Geography("Nordic - NULL COUNTRIES"),
                List.of(new Country("DK"), null, new Country("NO"), new Country("FI"), new Country("SE")))
        );
    }

    @Test
    void it_CreateNonExistentCountry() {
        assertThrows(Exception.class, () -> geographyService.create(
                new Geography("Nordic - NON-EXISTENT COUNTRY"),
                List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI"), new Country("XX")))
        );
    }

    @Test
    void it_CreateDuplicatedCountry() throws Exception {
        // Setup
        var geography = new Geography("Nordic - DUPLICATED COUNTRY");
        var countries = List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI"), new Country("DK"));

        // Call
        Geography result = geographyService.create(geography, countries);

        // Check
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0);
        assertThat(result.name()).isEqualTo(geography.name());
        assertThat(result.predefined()).isFalse();

        var resultCountries = countryDAO.allByGeography(result);
        var expectedCountries = List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI"));
        assertThat(resultCountries).containsExactlyElementsIn(expectedCountries);
    }

    @Test
    void it_CreateEmptyCountry() throws Exception {
        // Setup
        var geography = new Geography("Nordic - EMPTY COUNTRY");
        var countries = List.of(new Country("DK"), new Country(""), new Country("NO"), new Country("FI"), new Country("SE"));

        // Call
        Geography result = geographyService.create(geography, countries);

        // Check
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0);
        assertThat(result.name()).isEqualTo(geography.name());
        assertThat(result.predefined()).isFalse();

        var resultCountries = countryDAO.allByGeography(result);
        var expectedCountries = List.of(new Country("DK"), new Country("NO"), new Country("FI"), new Country("SE"));
        assertThat(resultCountries).containsExactlyElementsIn(expectedCountries);
    }

    @Test
    void it_CreateTwoCountries() throws Exception {
        // Setup
        var geography = new Geography("North America - TWO COUNTRIES");
        var countries = List.of(new Country("US"), new Country("CA"));

        // Call
        Geography result = geographyService.create(geography, countries);

        // Check
        assertThat(result).isNotNull();
        assertThat(result.id()).isGreaterThan(0);
        assertThat(result.name()).isEqualTo(geography.name());
        assertThat(result.predefined()).isFalse();

        var resultCountries = countryDAO.allByGeography(result);
        assertThat(resultCountries).containsExactlyElementsIn(countries);
    }

    @Test
    void it_CreateRemoveDuplicatesLessThanTwo() {
        // Setup
        var geography = new Geography("North America - REMOVE DUPLICATES LESS THAN TWO");
        var countries = List.of(new Country("US"), new Country("US"), new Country("US"));

        // Call & Check
        assertThrows(IllegalArgumentException.class, () -> geographyService.create(geography, countries));
    }
}