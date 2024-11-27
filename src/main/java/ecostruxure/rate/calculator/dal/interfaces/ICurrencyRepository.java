package ecostruxure.rate.calculator.dal.interfaces;

import ecostruxure.rate.calculator.be.Currency;
import org.springframework.data.repository.CrudRepository;

public interface ICurrencyRepository extends CrudRepository<Currency, String> {
}
