package ecostruxure.rate.calculator.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import ecostruxure.rate.calculator.util.AppConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private final PGSimpleDataSource dataSource;

    /**
     * Constructs a DBConnector using default configuration parameters from file provided by AppConfig.CONFIG_FILE
     *
     * @throws IOException if there is an error reading the configuration file.
     */
    public DBConnector() throws IOException {
        this(AppConfig.CONFIG_FILE);
    }

    /**
     * Constructs a DBConnector using configuration parameters supplied by a specified file.<br>
     * The file should supply the configuration parameters as follows:<br>
     * <ul>
     *     <li>db.server= (The database server name or IP address)</li>
     *     <li>db.database= (The database name)</li>
     *     <li>db.username= (The username for database access)</li>
     *     <li>db.password= (The password for database access)</li>
     *     <li>db.port= (The database server port number)</li>
     *     <li>db.use_integrated_security= (Boolean indicating whether to use integrated security, true or false)</li>
     * </ul>
     *
     * @param propertiesFilePath path to the properties file containing database configuration settings.
     * @throws IOException if there is an error reading the provided file.
     */
    public DBConnector(String propertiesFilePath) throws IOException {
        var properties = new Properties();
        properties.load(new FileInputStream(propertiesFilePath));

        dataSource = new PGSimpleDataSource();
        dataSource.setServerName(properties.getProperty(AppConfig.DB_SERVER));
        dataSource.setDatabaseName(properties.getProperty(AppConfig.DB_DATABASE));

        dataSource.setUser(properties.getProperty(AppConfig.DB_USERNAME));
        dataSource.setPassword(properties.getProperty(AppConfig.DB_PASSWORD));

        dataSource.setPortNumber(Integer.parseInt(properties.getProperty(AppConfig.DB_PORT)));
    }

    /**
     * Creates and returns a new connection to the database based on the configuration paramters supplied
     * during the instantiation of the DBConnector object.
     *
     * @return A new Connection object to the database.
     * @throws SQLException if a database access error occurs or the connection parameters are incorrect.
     */
    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Used for testing purposes, to see if one can connect to database.
     *
     * @param args unused
     * @throws RuntimeException if connecting to the database fails or reading the configuration file fails.
     */
    public static void main(String[] args){
        DBConnector dbConnector = null;
        try {
            dbConnector = new DBConnector();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read properties file... " + e);
        }

        try (Connection conn = dbConnector.connection()) {
            System.out.println("Established connection to database: " + !conn.isClosed());
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database... " + e);
        }
    }
}
