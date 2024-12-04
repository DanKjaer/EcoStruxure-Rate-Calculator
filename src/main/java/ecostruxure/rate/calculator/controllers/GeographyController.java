package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.bll.geography.GeographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geography")
public class GeographyController {
    private final GeographyService geographyService;

    @Autowired
    public GeographyController(GeographyService geographyService) throws Exception {
        this.geographyService = geographyService;
    }

    @GetMapping()
    public Iterable<Geography> getGeographies() throws Exception {
        return geographyService.all();
    }
}
