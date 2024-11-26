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
                        rs.getString("symbol")
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
                currency.setSymbol(rs.getString("symbol"));
                currencies.add(currency);
            }

            return currencies;
        } catch (SQLException e) {
            throw new Exception("Failed to retrieve currencies from the database.\n" + e.getMessage(), e);
        }
    }

    @Override
    public void addCurrency(Currency currency) throws Exception {
        String query = """
                INSERT INTO dbo.currency (currency_code, eur_conversion_rate, symbol)
                VALUES (?, ?, ?)
                ON CONFLICT (currency_code) DO UPDATE SET eur_conversion_rate = ?, symbol = ?;
                """;
        try (Connection conn = dbConnector.connection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currency.currencyCode());
            stmt.setBigDecimal(2, currency.eurConversionRate());
            stmt.setString(3, currency.getSymbol());
            stmt.executeUpdate();
        }
    }
}
