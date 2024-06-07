package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.sql.Connection;

public class SqlTransactionContext implements TransactionContext {
    private final Connection connection;

    public SqlTransactionContext(Connection connection) {
        this.connection = connection;
    }

    public Connection connection() {
        return connection;
    }

    @Override
    public void begin() throws Exception {
        connection.setAutoCommit(false);
    }

    @Override
    public void commit() throws Exception {
        connection.commit();
    }

    @Override
    public void rollback() throws Exception {
        connection.rollback();
    }
}
