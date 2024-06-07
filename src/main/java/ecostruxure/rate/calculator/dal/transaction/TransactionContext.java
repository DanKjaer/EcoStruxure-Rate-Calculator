package ecostruxure.rate.calculator.dal.transaction;

public interface TransactionContext {
    void begin() throws Exception;
    void commit() throws Exception;
    void rollback() throws Exception;
}
