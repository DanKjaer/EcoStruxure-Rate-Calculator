package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.be.dto.ProfileDTO;
import ecostruxure.rate.calculator.bll.profile.ProfileService;
import ecostruxure.rate.calculator.bll.project.ProjectService;
import ecostruxure.rate.calculator.bll.team.TeamService;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    private final TeamService teamService;
    private final ProfileService profileService;
    private final ProjectService projectService;

    @Autowired
    public ExcelService(TeamService teamService, ProfileService profileService, ProjectService projectService) {
        this.teamService = teamService;
        this.profileService = profileService;
        this.projectService = projectService;
    }

    public ResponseEntity<byte[]> exportToExcel() throws Exception {
        //data
        List<ProfileDTO> profiles = profileService.all();
        List<Team> teams = (List<Team>) teamService.all();
        List<Project> projects = (List<Project>) projectService.getProjects();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            //sheets for each data type
            Sheet profileSheet = workbook.createSheet("Profiles");
            Sheet teamSheet = workbook.createSheet("Teams");
            Sheet projectSheet = workbook.createSheet("Projects");

            addProfilesToSheet(profileSheet, profiles);
            addTeamsToSheet(teamSheet, teams);
            addProjectsToSheet(projectSheet, projects);

            workbook.write(outputStream);

            byte[] bytes = outputStream.toByteArray();
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=export.xlsx")
                    .body(bytes);
        }
    }

    private void addProfilesToSheet(Sheet sheet, List<ProfileDTO> profiles) {
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Location");
        headerRow.createCell(2).setCellValue("Annual Cost");
        headerRow.createCell(3).setCellValue("Annual Hours");
        headerRow.createCell(4).setCellValue("Effectiveness (%)");
        headerRow.createCell(5).setCellValue("Effective Work Hours");
        headerRow.createCell(6).setCellValue("Cost Allocation (%)");
        headerRow.createCell(7).setCellValue("Hour Allocation (%)");

        int rowIndex = 1;
        for (ProfileDTO profile : profiles) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(profile.getName());
            row.createCell(1).setCellValue(profile.getGeography().getName());
            row.createCell(2).setCellValue(profile.getAnnualCost().doubleValue());
            row.createCell(3).setCellValue(profile.getAnnualHours().doubleValue());
            row.createCell(4).setCellValue(profile.getEffectivenessPercentage().doubleValue());
            row.createCell(5).setCellValue(profile.getEffectiveWorkHours().doubleValue());
            row.createCell(6).setCellValue(profile.getTotalCostAllocation().doubleValue());
            row.createCell(7).setCellValue(profile.getTotalHourAllocation().doubleValue());
        }

        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addTeamsToSheet(Sheet sheet, List<Team> teams) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Markup (%)");
        headerRow.createCell(2).setCellValue("GM (%)");
        headerRow.createCell(3).setCellValue("Hourly Rate");
        headerRow.createCell(4).setCellValue("Day Rate");
        headerRow.createCell(5).setCellValue("Total Annual Cost");
        headerRow.createCell(6).setCellValue("Total Annual Hours");
        headerRow.createCell(7).setCellValue("Total Markup");
        headerRow.createCell(8).setCellValue("Total GM");

        int rowIndex = 1;
        for (Team team : teams) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(team.getName());
            row.createCell(1).setCellValue(team.getMarkupPercentage().doubleValue());
            row.createCell(2).setCellValue(team.getGrossMarginPercentage().doubleValue());
            row.createCell(3).setCellValue(team.getHourlyRate().doubleValue());
            row.createCell(4).setCellValue(team.getDayRate().doubleValue());
            row.createCell(5).setCellValue(team.getTotalAllocatedCost().doubleValue());
            row.createCell(6).setCellValue(team.getTotalAllocatedHours().doubleValue());
            row.createCell(7).setCellValue(team.getTotalCostWithMarkup().doubleValue());
            row.createCell(8).setCellValue(team.getTotalCostWithGrossMargin().doubleValue());
        }

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addProjectsToSheet(Sheet sheet, List<Project> projects) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Project Sales Number");
        headerRow.createCell(2).setCellValue("Members");
        headerRow.createCell(3).setCellValue("Day Rate");
        headerRow.createCell(4).setCellValue("GM (%)");
        headerRow.createCell(5).setCellValue("Total Price");
        headerRow.createCell(6).setCellValue("Start Date");
        headerRow.createCell(7).setCellValue("End Date");
        headerRow.createCell(8).setCellValue("Total days");
        headerRow.createCell(9).setCellValue("Country");

        int rowIndex = 1;
        for (Project project : projects) {
            Row row = sheet.createRow(rowIndex++);
            //Get string of all team names
            String teamNames = project.getProjectTeams()
                    .stream().map(ProjectTeam::getTeam)
                    .map(Team::getName)
                    .collect(Collectors.joining(", "));

            row.createCell(0).setCellValue(project.getProjectName());
            row.createCell(1).setCellValue(project.getProjectSalesNumber());
            row.createCell(2).setCellValue(teamNames);
            row.createCell(3).setCellValue(project.getProjectDayRate().doubleValue());
            row.createCell(4).setCellValue(project.getProjectGrossMargin().doubleValue());
            row.createCell(5).setCellValue(project.getProjectTotalCostAtChange().doubleValue());
            row.createCell(6).setCellValue(project.getProjectStartDate());
            row.createCell(7).setCellValue(project.getProjectEndDate());
            row.createCell(8).setCellValue(project.getProjectTotalDays());
            row.createCell(9).setCellValue(project.getProjectLocation().getName());
        }

        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        var font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font);
        style.setFillBackgroundColor(IndexedColors.GREEN.index);
        return style;
    }

    private CellStyle getDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    public void createHeaderAndDataRows(Sheet sheet, int rowNum, String[] headerValues, String[] dataValues, Workbook workbook) {
        Row headerRow = sheet.createRow(rowNum);
        for (int i = 0; i < headerValues.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerValues[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        Row dataRow = sheet.createRow(rowNum + 1);
        for (int i = 0; i < dataValues.length; i++) {
            Cell cell = dataRow.createCell(i);
            try {
                double numericValue = Double.parseDouble(dataValues[i]);
                cell.setCellValue(numericValue);
                cell.setCellType(CellType.NUMERIC);
            } catch (NumberFormatException e) {
                cell.setCellValue(dataValues[i]);
            }
            cell.setCellStyle(getDataCellStyle(workbook));
        }
    }

    private void createDataRows(Sheet sheet, int rowIndex, String[] data, Workbook workbook) {
        Row dataRow = sheet.createRow(rowIndex);
        for (int i = 0; i < data.length; i++) {
            Cell cell = dataRow.createCell(i);
            try {
                double numericValue = Double.parseDouble(data[i]);
                cell.setCellValue(numericValue);
                cell.setCellType(CellType.NUMERIC);
            } catch (NumberFormatException e) {
                cell.setCellValue(data[i]);
            }
            cell.setCellStyle(getDataCellStyle(workbook));
        }
    }

    public void setWorkbookProperties(Workbook workbook, String title, String creator, String description, String applicationName, String company) {
        if (workbook instanceof XSSFWorkbook) {
            XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
            POIXMLProperties xmlProps = xssfWorkbook.getProperties();
            POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties();
            coreProps.setTitle(title);
            coreProps.setCreator(creator);
            coreProps.setDescription(description);

            POIXMLProperties.ExtendedProperties extProps = xmlProps.getExtendedProperties();
            extProps.getUnderlyingProperties().setApplication(applicationName);
            extProps.getUnderlyingProperties().setCompany(company);
        } else if (workbook instanceof HSSFWorkbook) {
            HSSFWorkbook hssfWorkbook = (HSSFWorkbook) workbook;
            SummaryInformation summaryInfo = hssfWorkbook.getSummaryInformation();
            summaryInfo.setTitle(title);
            summaryInfo.setAuthor(creator);
            summaryInfo.setComments(description);

            DocumentSummaryInformation docSummaryInfo = hssfWorkbook.getDocumentSummaryInformation();
            docSummaryInfo.setCompany(company);
        }
    }
}
