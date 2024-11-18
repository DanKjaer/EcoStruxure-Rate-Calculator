package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geography")
public class GeographyController {
    private final GeographyService geographyService;

    public GeographyController() throws Exception {
        this.geographyService = new GeographyService();
    }

    @GetMapping()
    public List<Geography> getGeographies() throws Exception {
        return geographyService.getGeographies();
    }

    @GetMapping("/{countryId}")
    public Geography getCountryById(@PathVariable int countryId) throws Exception {
        return geographyService.get(countryId);
    }
}
