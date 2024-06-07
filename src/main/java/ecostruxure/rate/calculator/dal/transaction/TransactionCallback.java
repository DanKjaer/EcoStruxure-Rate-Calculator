package ecostruxure.rate.calculator.dal.transaction;

@FunctionalInterface
public interface TransactionCallback<T> {
    T doInTransaction(TransactionContext context) throws Exception;
}
