package ecostruxure.rate.calculator.dal.transaction;

import ecostruxure.rate.calculator.dal.transaction.TransactionCallback;

public interface TransactionManager {
    <T> T executeTransaction(TransactionCallback<T> callback) throws Exception;
}
