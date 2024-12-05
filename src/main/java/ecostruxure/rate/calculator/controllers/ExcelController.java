package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.bll.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService) throws Exception {
        this.excelService = excelService;
    }

    @GetMapping()
    public ResponseEntity<byte[]> getExcel() throws Exception {
        return excelService.exportToExcel();
    }
}
