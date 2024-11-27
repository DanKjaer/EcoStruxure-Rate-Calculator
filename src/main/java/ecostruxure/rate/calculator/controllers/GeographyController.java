package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geography")
public class GeographyController {

    @Autowired
    private GeographyService geographyService;

    public GeographyController() throws Exception {
    }

    @GetMapping()
    public Iterable<Geography> getGeographies() throws Exception {
        return geographyService.all();
    }
}
