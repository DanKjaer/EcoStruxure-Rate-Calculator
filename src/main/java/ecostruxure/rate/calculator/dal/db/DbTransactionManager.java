package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.dal.transaction.TransactionCallback;
import ecostruxure.rate.calculator.dal.transaction.TransactionManager;

import java.io.IOException;
import java.sql.Connection;

public class DbTransactionManager implements TransactionManager {
    private final DBConnector dbConnector;

    public DbTransactionManager() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public <T> T executeTransaction(TransactionCallback<T> callback) throws Exception {
        try (Connection connection = dbConnector.connection()) {
            SqlTransactionContext context = new SqlTransactionContext(connection);
            context.begin();

            try {
                T result = callback.doInTransaction(context);
                context.commit();
                return result;
            } catch (Exception e) {
                context.rollback();
                throw new Exception("Transaction failed.\n" + e.getMessage(), e);
            }
        }
    }
}
