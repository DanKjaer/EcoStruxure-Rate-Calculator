package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.RateService;
import ecostruxure.rate.calculator.be.enums.RateType;
import ecostruxure.rate.calculator.be.data.Rates;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExcelService {
    private static final int GENERAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private TeamService teamService;
    private ProfileService profileService;
    private GeographyService geographyService;
    private RateService rateService;


    public ExcelService() {
        try {
            teamService = new TeamService();
            profileService = new ProfileService();
            geographyService = new GeographyService();
            rateService = new RateService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
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

    public ByteArrayOutputStream exportTeam(UUID teamId) throws Exception {
        Map<String, Object> teamData = teamService.getTeamAndProfiles(teamId);
        Team getTeam = (Team) teamData.get("team");
        List<TeamProfile> teamProfiles = (List<TeamProfile>) teamData.get("profiles");

        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheetTeam = workbook.createSheet("Team");

        this.setWorkbookProperties(workbook, "Team Export", "EcoStruxure Rate Calculator", "Team data of " + getTeam.getName(), "EcoStruxure Rate Calculator", "Schneider Electric");

        Rates hourlyRates = rateService.calculateRates(getTeam, RateType.HOURLY, teamProfiles);
        Rates dayRates = rateService.calculateRates(getTeam, RateType.DAY, teamProfiles);
        Rates annualRates = rateService.calculateRates(getTeam, RateType.ANNUAL, teamProfiles);

        String[] header1 = {"Team Name", "Markup", "Gross Margin", "Profiles", "Archived", "Currency"};
        String[] data1 = {getTeam.getName(),
                getTeam.getMarkup() != null ? getTeam.getMarkup().toString() : "",
                getTeam.getGrossMargin() != null ? getTeam.getGrossMargin().toString() : "",
                String.valueOf(teamProfiles.size()), getTeam.isArchived() ? "Yes" : "No", "EURO"};
        createHeaderAndDataRows(sheetTeam, 0, header1, data1, workbook);

        String[] header2 = {"Raw hourly rate", "Markup hourly rate", "GM hourly rate"};
        String[] data2 = {hourlyRates.rawRate().toString(), hourlyRates.markupRate().toString(), hourlyRates.grossMarginRate().toString()};
        createHeaderAndDataRows(sheetTeam, 3, header2, data2, workbook);

        String[] header3 = {"Raw day rate", "Markup day rate", "GM day rate"};
        String[] data3 = {dayRates.rawRate().toString(), dayRates.markupRate().toString(), dayRates.grossMarginRate().toString()};
        createHeaderAndDataRows(sheetTeam, 6, header3, data3, workbook);

        String[] header4 = {"Raw annual rate", "Markup annual rate", "GM annual rate"};
        String[] data4 = {annualRates.rawRate().toString(), annualRates.markupRate().toString(), annualRates.grossMarginRate().toString()};
        createHeaderAndDataRows(sheetTeam, 9, header4, data4, workbook);

        Sheet sheetProfiles = workbook.createSheet("Profiles");
        if (teamProfiles.size() >= 1) {
            String[] profileHeader = {"Name", "Utilization hours", "Hours", "Utilization rate", "Hourly rate", "Day rate"};
            Row headerRow = sheetProfiles.createRow(0);
            for (int i = 0; i < profileHeader.length; i++) {
                headerRow.createCell(i).setCellValue(profileHeader[i]);
            }
            int rowIndex = 1;
            for (TeamProfile teamProfile : teamProfiles) {
                String[] profileData = {teamProfile.getName(),
                        teamProfile.getHourAllocation().toString(),
                        teamProfile.getAllocatedHoursOnTeam().toString(),
                        teamProfile.getCostAllocation().toString(),
                        teamProfile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0 ? "0" :
                                teamProfile.getAnnualCost().divide(teamProfile.getAnnualHours(),
                                        GENERAL_SCALE,
                                        ROUNDING_MODE).toString(),
                        teamProfile.getDayRateOnTeam().toString()};
                createDataRows(sheetProfiles, rowIndex++, profileData, workbook);
            }
        } else {
            String[] profileHeader = {"No profiles assigned"};
            Row headerRow = sheetProfiles.createRow(0);
            for (int i = 0; i < profileHeader.length; i++) {
                headerRow.createCell(i).setCellValue(profileHeader[i]);
            }
        }

        try (ByteArrayOutputStream fileOut = new ByteArrayOutputStream()) {
            workbook.write(fileOut);
            workbook.close();
            return fileOut;
        } catch (IOException e) {
            throw new Exception("Error occured, couldn't write to file..\n" + e.getMessage());
        }
    }

    public ByteArrayOutputStream exportTeams(List<Team> teams) throws Exception {
        Workbook workbook = WorkbookFactory.create(true);
        try {
            for (Team team : teams) {
                //Team getTeam = teamService.get(team.getTeamId());
                List<TeamProfile> teamProfiles = teamService.getTeamProfilesV2(team.getTeamId());
                //List<ProfileTeamItemModel> profileTeamItemModelList = convertToTeamProfileModels(team.getTeamId());

                String sheetName = team.getName();
                if (sheetName.length() > 31) sheetName = sheetName.substring(0, 31);

                Sheet sheetTeam = workbook.createSheet(sheetName);
                this.setWorkbookProperties(workbook,
                        "Team Export",
                        "EcoStruxure Rate Calculator",
                        "Multiple team data",
                        "EcoStruxure Rate Calculator",
                        "Schneider Electric");

                Rates hourlyRates = rateService.calculateRates(team, RateType.HOURLY, teamProfiles);
                Rates dayRates = rateService.calculateRates(team, RateType.DAY, teamProfiles);
                Rates annualRates = rateService.calculateRates(team, RateType.ANNUAL, teamProfiles);

                String[] header1 = {"Team Name", "Markup", "Gross Margin", "Profiles", "Archived", "Currency"};
                String[] data1 = {team.getName(),
                        team.getMarkup() != null ? team.getMarkup().toString() : "",
                        team.getGrossMargin() != null ? team.getGrossMargin().toString() : "",
                        String.valueOf(teamProfiles.size()), team.isArchived() ? "Yes" : "No", "EURO"};
                createHeaderAndDataRows(sheetTeam, 0, header1, data1, workbook);

                String[] header2 = {"Raw hourly rate", "Markup hourly rate", "GM hourly rate"};
                String[] data2 = {hourlyRates.rawRate() != null ? hourlyRates.rawRate().toString() : "",
                        hourlyRates.markupRate() != null ? hourlyRates.markupRate().toString() : "",
                        hourlyRates.grossMarginRate() != null ? hourlyRates.grossMarginRate().toString() : ""};
                createHeaderAndDataRows(sheetTeam, 3, header2, data2, workbook);

                String[] header3 = {"Raw day rate", "Markup day rate", "GM day rate"};
                String[] data3 = {dayRates.rawRate() != null ? dayRates.rawRate().toString() : "",
                        dayRates.markupRate() != null ? dayRates.markupRate().toString() : "",
                        dayRates.grossMarginRate() != null ? dayRates.grossMarginRate().toString() : ""};
                createHeaderAndDataRows(sheetTeam, 6, header3, data3, workbook);

                String[] header4 = {"Raw annual rate", "Markup annual rate", "GM annual rate"};
                String[] data4 = {annualRates.rawRate() != null ? annualRates.rawRate().toString() : "",
                        annualRates.markupRate() != null ? annualRates.markupRate().toString() : "",
                        annualRates.grossMarginRate() != null ? annualRates.grossMarginRate().toString() : ""};
                createHeaderAndDataRows(sheetTeam, 9, header4, data4, workbook);

                int profileHeaderIndex = 13;
                if (teamProfiles.size() >= 1) {
                    String[] profileHeader = {"Name", "Utilization hours", "Hours", "Utilization rate", "Hourly rate", "Day rate"};
                    Row headerRow = sheetTeam.createRow(profileHeaderIndex);
                    for (int i = 0; i < profileHeader.length; i++)
                        headerRow.createCell(i).setCellValue(profileHeader[i]);

                    int rowIndex = profileHeaderIndex + 1;
                    TeamProfile tp = new TeamProfile();
                    for (TeamProfile teamProfile : teamProfiles) {
                        String[] profileData = {teamProfile.getName(),
                                teamProfile.getHourAllocation().toString(),
                                teamProfile.getAllocatedHoursOnTeam().toString(),
                                teamProfile.getCostAllocation().toString(),
                                teamProfile.getAnnualCost().divide(teamProfile.getAnnualHours(), GENERAL_SCALE, ROUNDING_MODE).toString(),
                                teamProfile.getDayRateOnTeam().toString()};
                        createDataRows(sheetTeam, rowIndex++, profileData, workbook);
                    }
                } else {
                    String[] profileHeader = {"No profiles assigned"};
                    Row headerRow = sheetTeam.createRow(profileHeaderIndex);
                    for (int i = 0; i < profileHeader.length; i++)
                        headerRow.createCell(i).setCellValue(profileHeader[i]);
                }
            }

            try (ByteArrayOutputStream fileOut = new ByteArrayOutputStream()) {
                workbook.write(fileOut);
                workbook.close();
                return fileOut;
            }
        } catch (Exception e) {
            throw new Exception("Error occured, couldn't write to file..\n" + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new Exception("Error occured, couldn't write to file..\n" + e.getMessage());
            }
        }
    }

    /**
     * Ikke slettet endnu da vi måske vil lave funktionen, måske skal det bare slettes
     */

//    public void exportTable(TableView<?> tableView) {
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
//        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Sheet1");
//        HSSFRow firstRow = hssfSheet.createRow(0);
//
//        for (int i = 0; i < tableView.getColumns().size(); i++)
//            firstRow.createCell(i).setCellValue(tableView.getColumns().get(i).getText());
//
//        for (int row = 0; row < tableView.getItems().size(); row++) {
//
//            HSSFRow hssfRow = hssfSheet.createRow(row + 1);
//
//            for (int col = 0; col < tableView.getColumns().size(); col++) {
//                if (tableView.getColumns().get(col).getCellObservableValue(row) == null) continue;
//
//                Object celValue = tableView.getColumns().get(col).getCellObservableValue(row).getValue();
//                try {
//                    if (celValue != null && Double.parseDouble(celValue.toString()) != 0.0) {
//                        hssfRow.createCell(col).setCellValue(Double.parseDouble(celValue.toString()));
//                    }
//                } catch (NumberFormatException e) {
//                    hssfRow.createCell(col).setCellValue(celValue.toString());
//                }
//            }
//        }
//
//        try {
//            hssfWorkbook.write(new FileOutputStream("WorkBook.xls"));
//            hssfWorkbook.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
