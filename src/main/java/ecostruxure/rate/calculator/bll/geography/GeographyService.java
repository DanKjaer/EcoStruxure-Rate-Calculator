package ecostruxure.rate.calculator.bll.geography;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.dal.IGeographyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeographyService {

    @Autowired
    private IGeographyRepository geographyRepository;

    public GeographyService() throws IOException {
    }

    public Iterable<Geography> all() throws Exception {
        return geographyRepository.findAll();
    }

    public Geography getById(int id) throws Exception {
        return geographyRepository.findById(id).orElseThrow(() -> new Exception("Geography not found."));
    }
}