package ecostruxure.rate.calculator.dal.dao;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.db.CountryDAO;
import ecostruxure.rate.calculator.dal.db.GeographyDAO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GeographyDAOTest {
    private IGeographyDAO geographyDAO;
    private ICountryDAO countryDAO;

    public GeographyDAOTest() {
        try {
            geographyDAO = new GeographyDAO();
            countryDAO = new CountryDAO();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void it_CreateSuccess() {
        var countriesExpected = List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI"));
        Geography geography = null;
        try {
            geography = geographyDAO.create(new Geography("Nordic"), countriesExpected);
        } catch (Exception e) {
            fail(e);
        }
        assertThat(geography).isNotNull();
        assertThat(geography.id()).isGreaterThan(0);
        assertThat(geography.name()).isEqualTo("Nordic");
        assertThat(geography.predefined()).isFalse();

        List<Country> countriesActual = null;
        try {
            countriesActual = countryDAO.allByGeography(geography);
        } catch (Exception e) {
            fail(e);
        }

        assertThat(countriesActual).containsExactlyElementsIn(countriesExpected);
    }

    @Test
    void it_exists() throws Exception {
        boolean denmarkExists = geographyDAO.exists(List.of(new Country("DK")));
        assertThat(denmarkExists).isTrue();

        boolean AU_NZExists = geographyDAO.exists(List.of(new Country("AU"), new Country("NZ")));
        assertThat(AU_NZExists).isTrue();
    }

    @Test
    void it_doesntExists() throws Exception {
        boolean falseExists = geographyDAO.exists(List.of(new Country("XX")));
        assertThat(falseExists).isFalse();

        boolean usNordicExists = geographyDAO.exists(List.of(new Country("DK"), new Country("SE"), new Country("NO"), new Country("FI"), new Country("US")));
        assertThat(usNordicExists).isFalse();
    }
}