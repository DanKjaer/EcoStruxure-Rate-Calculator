package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.dal.dao.ICurrencyDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO implements ICurrencyDAO {
    private final DBConnector dbConnector;

    public CurrencyDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }


    @Override
    public Currency get(String code) throws Exception {
        String query = "SELECT * FROM Currency WHERE currency_code = ?";

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Currency(
                        rs.getString("currency_code"),
                        rs.getBigDecimal("eur_conversion_rate"),
                        rs.getBigDecimal("usd_conversion_rate")
                );
            } else {
                throw new Exception("Currency with code " + code + " not found in the database.");
            }
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve currency from the database for code: " + code + ".\n" + e.getMessage(), e);
        }
    }

    @Override
    public List<Currency> all() throws Exception {
        List<Currency> currencies = new ArrayList<>();

        String query = "SELECT * FROM Currency ORDER BY currency_code";

        try (Connection conn = dbConnector.connection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Currency currency = new Currency();
                currency.setCurrencyCode(rs.getString("currency_code"));
                currency.setEurConversionRate(rs.getBigDecimal("eur_conversion_rate"));
                currency.setUsdConversionRate(rs.getBigDecimal("usd_conversion_rate"));
                currency.setSymbol(rs.getString("symbol"));
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve currencies from the database.\n" + e.getMessage(), e);
        }
    }

    @Override
    public void addCurrencies(List<Currency> currencies) throws Exception {
        String query = """
                       INSERT INTO Currency (currency_code, eur_conversion_rate, usd_conversion_rate)
                       VALUES (?, ?, ?);
                       """;

        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                for (Currency currency : currencies) {
                    stmt.setString(1, currency.currencyCode());
                    stmt.setBigDecimal(2, currency.eurConversionRate());
                    stmt.setBigDecimal(3, currency.usdConversionRate());
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new Exception("Could not add currencies to the database.\n" + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new Exception("Database connection error occurred", e);
        }
    }

    @Override
    public void addCurrency(Currency currency) throws Exception {
        String query = """
                INSERT INTO dbo.currency (currency_code, eur_conversion_rate, usd_conversion_rate, symbol)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection conn = dbConnector.connection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currency.currencyCode());
            stmt.setBigDecimal(2, currency.eurConversionRate());
            stmt.setBigDecimal(3, currency.usdConversionRate());
            stmt.setString(4, currency.getSymbol());
            stmt.executeUpdate();
        }
    }

    @Override
    public void removeAllCurrencies() throws Exception {
        String query = "DELETE FROM Currency";

        try (Connection conn = dbConnector.connection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new Exception("Failed to clear Currency table. Operation rolled back.", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new Exception("Database connection error occurred", e);
        }
    }
}
