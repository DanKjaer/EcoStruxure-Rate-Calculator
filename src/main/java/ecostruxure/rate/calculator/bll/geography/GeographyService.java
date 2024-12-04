package ecostruxure.rate.calculator.bll.geography;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.IGeographyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeographyService {
    private final IGeographyRepository geographyRepository;

    @Autowired
    public GeographyService(IGeographyRepository geographyRepository){
        this.geographyRepository = geographyRepository;
    }

    public Iterable<Geography> all() throws Exception {
        return geographyRepository.findAll();
    }
}