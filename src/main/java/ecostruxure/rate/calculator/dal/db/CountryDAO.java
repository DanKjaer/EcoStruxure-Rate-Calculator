package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.dao.ICountryDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO implements ICountryDAO {
    private final DBConnector dbConnector;

    public CountryDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public Country get(String code) throws Exception {
        Country country = null;

        String query = """
                       WITH SingleCountryGeography AS (
                           SELECT gc.geography
                           FROM dbo.Geography_countries AS gc
                           JOIN dbo.Geography AS g ON gc.geography = g.id
                           WHERE g.country = 1
                       )
                       SELECT c.code, c.latitude, c.longitude, g.name AS country_name
                       FROM dbo.Countries AS c
                       LEFT JOIN dbo.Geography_countries AS gc ON c.code = gc.code
                       LEFT JOIN dbo.Geography AS g ON gc.geography = g.id
                       WHERE c.code = 'AD' AND gc.geography IN (SELECT geography FROM SingleCountryGeography);
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    BigDecimal latitude = rs.getBigDecimal("latitude");
                    BigDecimal longitude = rs.getBigDecimal("longitude");

                    country = new Country(code, name, latitude, longitude);
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not get Country from Database.\n" + e.getMessage());
        }

        if (country == null) throw new Exception("Country with code " + code + " not found.");

        return country;
    }

    @Override
    public List<Country> all() throws Exception {
        List<Country> countries = new ArrayList<>();

        String query = """
                       WITH SingleCountryGeography AS (
                           SELECT gc.geography
                           FROM dbo.Geography_countries AS gc
                           JOIN dbo.Geography AS g ON gc.geography = g.id
                           WHERE g.country = 1
                       )
                       SELECT c.code, c.latitude, c.longitude, g.name AS name
                       FROM dbo.Countries AS c
                       LEFT JOIN dbo.Geography_countries AS gc ON c.code = gc.code
                       LEFT JOIN dbo.Geography AS g ON gc.geography = g.id
                       WHERE gc.geography IS NULL OR gc.geography IN (SELECT geography FROM SingleCountryGeography)
                       ORDER BY c.code;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("code");
                String name = rs.getString("name");
                BigDecimal latitude = rs.getBigDecimal("latitude");
                BigDecimal longitude = rs.getBigDecimal("longitude");

                countries.add(new Country(code, name, latitude, longitude));
            }

            return countries;
        } catch (Exception e) {
            throw new Exception("Could not get all Countries from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Country> allByGeography(Geography geography) throws Exception {
        String query = """
                       WITH RelevantCountries AS (
                           SELECT gc.code
                           FROM dbo.Geography_countries AS gc
                           WHERE gc.geography = ?
                       )
                       SELECT c.code, c.latitude, c.longitude, g.name AS country_name
                       FROM dbo.Countries AS c
                       JOIN RelevantCountries rc ON c.code = rc.code
                       JOIN dbo.Geography_countries AS gc ON c.code = gc.code
                       JOIN dbo.Geography AS g ON gc.geography = g.id AND g.country = 1
                       ORDER BY c.code;
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, geography.id());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Country> countries = new ArrayList<>();
                while (rs.next()) {
                    String code = rs.getString("code");
                    String name = rs.getString("country_name");
                    BigDecimal latitude = rs.getBigDecimal("latitude");
                    BigDecimal longitude = rs.getBigDecimal("longitude");

                    countries.add(new Country(code, name, latitude, longitude));
                }

                return countries;
            }
        }
    }
}
