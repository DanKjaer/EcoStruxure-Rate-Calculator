package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.dao.IGeographyDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GeographyDAO implements IGeographyDAO {
    private final DBConnector dbConnector;

    public GeographyDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public ArrayList<Geography> all() throws Exception {
        ArrayList<Geography> geographies = new ArrayList<>();

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Geography ORDER BY name");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean predefined = rs.getBoolean("predefined");

                geographies.add(new Geography(id, name, predefined));
            }

            return geographies;
        } catch (Exception e) {
            throw new Exception("Could not get all Geographies from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Geography> allExceptCountries() throws Exception {
        List<Geography> geographies = new ArrayList<>();

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Geography WHERE country = 0 ORDER BY name");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean predefined = rs.getBoolean("predefined");

                geographies.add(new Geography(id, name, predefined));
            }

            return geographies;
        } catch (Exception e) {
            throw new Exception("Could not get all Geographies from Database.\n" + e.getMessage());
        }
    }

    @Override
    public List<Geography> allOnlyCountries() throws Exception {
        List<Geography> geographies = new ArrayList<>();

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Geography WHERE country = 1 ORDER BY name");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean predefined = rs.getBoolean("predefined");

                geographies.add(new Geography(id, name, predefined));
            }

            return geographies;
        } catch (Exception e) {
            throw new Exception("Could not get all Geographies from Database.\n" + e.getMessage());
        }
    }

    @Override
    public Geography get(int id) throws Exception {
        Geography geography = null;

        String query = """
                       SELECT * FROM Geography WHERE id = ?
                       """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    boolean predefined = rs.getBoolean("predefined");

                    geography = new Geography(id, name, predefined);
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not get Geography from Database. Geography ID: " + id + "\n" + e.getMessage());
        }

        if (geography == null) throw new Exception("Geography with ID " + id + " not found.");

        return geography;
    }

    @Override
    public Geography create(Geography geography, List<Country> countries) throws Exception {
        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try {
                Geography addedGeography = add(geography);

                for (Country country : countries) {
                    addGeographyCountryRelation(addedGeography, country);
                }

                conn.commit();
                return addedGeography;
            } catch (SQLException e) {
                conn.rollback();
                throw new Exception("Could not create Geography in Database.\n" + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean exists(List<Country> countries) throws Exception {
        return get(countries) != null;
    }

    @Override
    public Geography get(List<Country> countries) throws Exception {
        Set<String> countryCodes = countries.stream()
                                            .map(Country::code)
                                            .collect(Collectors.toSet());

        String placeholders = String.join(",", Collections.nCopies(countryCodes.size(), "?"));

        String query = "SELECT g.id, g.name, g.predefined " +
                       "FROM Geography_countries gc " +
                       "JOIN Geography g ON g.id = gc.geography " +
                       "GROUP BY g.id, g.name, g.predefined " +
                       "HAVING COUNT(DISTINCT gc.code) = ? " +
                       "AND SUM(CASE WHEN gc.code IN (" + placeholders + ") THEN 1 ELSE 0 END) = COUNT(DISTINCT gc.code)";

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int index = 1;
            stmt.setInt(index, countryCodes.size());
            for (String code : countryCodes) {
                index++;
                stmt.setString(index, code);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Geography(rs.getInt("id"), rs.getString("name"), rs.getBoolean("predefined"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new Exception("Error retrieving geography with specified countries", e);
        }
    }

    private Geography add(Geography geography) throws SQLException {
        String query = """
                     INSERT INTO Geography (name, predefined)
                     VALUES (?, 0)
                     """; // 0 betyder den ikke er predefined

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, geography.name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    geography.id(rs.getInt(1));
                    return geography;
                } else {
                    throw new SQLException("No generated keys returned");
                }
            }
        }
    }

    private void addGeographyCountryRelation(Geography geography, Country country) throws SQLException {
        String query = """
                     INSERT INTO Geography_countries (geography, code)
                     VALUES (?, ?)
                     """;

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, geography.id());
            stmt.setString(2, country.code());
            stmt.executeUpdate();
        }
    }
}
