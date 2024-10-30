package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.bll.service.ExcelService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    ExcelService excelService;

    public ExcelController() throws Exception {
        excelService = new ExcelService();
    }

    @GetMapping("/team")
    public ResponseEntity<byte[]> getTeamExcel(@RequestParam UUID teamId) throws Exception {
        ByteArrayOutputStream out = excelService.exportTeam(teamId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "data.xlsx");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/teams")
    public ResponseEntity<byte[]> getTeamsExcel(@RequestBody List<Team> teamList) throws Exception {
        ByteArrayOutputStream out = excelService.exportTeams(teamList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "data.xlsx");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }
}
