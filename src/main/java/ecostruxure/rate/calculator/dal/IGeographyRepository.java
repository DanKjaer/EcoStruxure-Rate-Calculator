package ecostruxure.rate.calculator.dal;

import ecostruxure.rate.calculator.be.Geography;
import org.springframework.data.repository.CrudRepository;

public interface IGeographyRepository extends CrudRepository<Geography, Integer> {
}
