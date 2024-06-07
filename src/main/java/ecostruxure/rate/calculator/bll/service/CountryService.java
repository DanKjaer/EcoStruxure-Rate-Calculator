package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.db.CountryDAO;
import ecostruxure.rate.calculator.dal.dao.ICountryDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CountryService {
    private final ICountryDAO countryDAO;

    public CountryService() throws IOException {
        countryDAO = new CountryDAO();
    }

    public List<Country> all() throws Exception {
        return countryDAO.all();
    }

    public List<Country> allByGeography(Geography geography) throws Exception {
        return countryDAO.allByGeography(geography);
    }
}
