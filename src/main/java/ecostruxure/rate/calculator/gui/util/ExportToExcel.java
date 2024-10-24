package ecostruxure.rate.calculator.gui.util;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.TeamProfile;
import ecostruxure.rate.calculator.bll.RateService;
import ecostruxure.rate.calculator.be.enums.RateType;
import ecostruxure.rate.calculator.be.data.Rates;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.bll.service.TeamProfileManagementService;
import ecostruxure.rate.calculator.bll.service.TeamService;
import ecostruxure.rate.calculator.gui.component.profile.ProfileTeamItemModel;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExportToExcel {
    private TeamService teamService;
    private ProfileService profileService;
    private GeographyService geographyService;
    private RateService rateService;
    private TeamProfileManagementService teamProfileManagementService;

    public ExportToExcel() {
        try {
            teamProfileManagementService = new TeamProfileManagementService();
            teamService = new TeamService();
            profileService = new ProfileService();
            geographyService = new GeographyService();
            rateService = new RateService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveFile(String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files (.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel Files (.xls)", "*.xls"),
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(null);

        return file;
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

    private List<ProfileTeamItemModel> convertToTeamProfileModels(UUID teamId) throws Exception {
        List<TeamProfile> teamProfiles = teamProfileManagementService.getTeamProfiles(teamId);;
        List<ProfileTeamItemModel> teamProfileModels = new ArrayList<>();

        for (TeamProfile teamProfile : teamProfiles) {
            ProfileTeamItemModel teamProfileModel = new ProfileTeamItemModel();

            teamProfileModel.setName(teamProfile.getName());

            teamProfileModel.profileIdProperty().set(teamProfile.getProfileId());
            teamProfileModel.teamIdProperty().set(teamProfile.getTeamId());
            BigDecimal dayRate = teamProfile.getDayRateOnTeam();
            teamProfileModel.setDayRate(dayRate);
            teamProfileModel.costAllocationProperty().set(teamProfile.getCostAllocation());
            teamProfileModel.hourAllocationProperty().set(teamProfile.getHourAllocation());
            BigDecimal allocatedCost = teamProfile.getAllocatedCostOnTeam();
            BigDecimal allocatedHours = teamProfile.getAllocatedHoursOnTeam();
            teamProfileModel.allocatedCostOnTeamProperty().set(allocatedCost);
            teamProfileModel.allocatedHoursOnTeamProperty().set(allocatedHours);

            teamProfileModels.add(teamProfileModel);
        }

        return teamProfileModels;
    }

    public void exportTeam(UUID teamId, File file) throws Exception {
        Team getTeam = teamService.get(teamId);
        List<Profile> profiles = teamService.getTeamProfiles(teamId);
        List<ProfileTeamItemModel> profileTeamItemModelList = convertToTeamProfileModels(teamId);

        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheetTeam = workbook.createSheet("Team");

        this.setWorkbookProperties(workbook,
                "Team Export",
                "EcoStruxure Rate Calculator",
                "Team data of " + getTeam.getName(),
                "EcoStruxure Rate Calculator",
                "Schneider Electric");

        Rates hourlyRates = rateService.calculateRates(getTeam, RateType.HOURLY, profileTeamItemModelList);
        Rates dayRates = rateService.calculateRates(getTeam, RateType.DAY, profileTeamItemModelList);
        Rates annualRates = rateService.calculateRates(getTeam, RateType.ANNUAL, profileTeamItemModelList);

        String[] header1 = {"Team Name", "Markup", "Gross Margin", "Profiles", "Archived", "Currency"};
        String[] data1 = {getTeam.getName(), getTeam.getMarkup() != null ? getTeam.getMarkup().toString()
                : "", getTeam.getGrossMargin() != null ? getTeam.getGrossMargin().toString()
                : "", String.valueOf(profiles.size()), getTeam.isArchived() ? "Yes" : "No", "EURO"};
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

        if (profiles.size() >= 1) {
            String[] profileHeader = {"Name", "Utilization hours", "Hours", "Utilization rate", "Hourly rate", "Day rate"};
            Row headerRow = sheetProfiles.createRow(0);
            for (int i = 0; i < profileHeader.length; i++) {
                headerRow.createCell(i).setCellValue(profileHeader[i]);
            }
            int rowIndex = 1;
            TeamProfile tp = new TeamProfile();
            for (Profile p : profiles) {
                String[] profileData = {p.getName(), tp.getHourAllocation().toString(),
                        profileService.totalHoursPercentage(p, tp.getHourAllocation()).toString(),
                        tp.getCostAllocation().toString(), profileService.hourlyRate(p, tp.getCostAllocation()).toString(),
                        profileService.dayRate(p, tp.getCostAllocation()).toString()};
                createDataRows(sheetProfiles, rowIndex++, profileData, workbook);
            }
        } else {
            String[] profileHeader = {"No profiles assigned"};
            Row headerRow = sheetProfiles.createRow(0);
            for (int i = 0; i < profileHeader.length; i++) {
                headerRow.createCell(i).setCellValue(profileHeader[i]);
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            throw new Exception("Error occured, couldn't write to file..\n" + e.getMessage());
        }
    }

    public void exportTeams(List<Team> teams, File file) throws Exception {
        Workbook workbook = WorkbookFactory.create(true);
        try {
            for (Team t : teams) {
                Team getTeam = teamService.get(t.getTeamId());
                List<Profile> profiles = teamService.getTeamProfiles(t.getTeamId());
                List<ProfileTeamItemModel> profileTeamItemModelList = convertToTeamProfileModels(t.getTeamId());

                String sheetName = t.getName();
                if (sheetName.length() > 31)
                    sheetName = sheetName.substring(0, 31);

                Sheet sheetTeam = workbook.createSheet(sheetName);
                this.setWorkbookProperties(workbook,
                        "Team Export",
                        "EcoStruxure Rate Calculator",
                        "Multiple team data",
                        "EcoStruxure Rate Calculator",
                        "Schneider Electric");

                Rates hourlyRates = rateService.calculateRates(getTeam, RateType.HOURLY, profileTeamItemModelList);
                Rates dayRates = rateService.calculateRates(getTeam, RateType.DAY, profileTeamItemModelList);
                Rates annualRates = rateService.calculateRates(getTeam, RateType.ANNUAL, profileTeamItemModelList);

                String[] header1 = {"Team Name", "Markup", "Gross Margin", "Profiles", "Archived", "Currency"};
                String[] data1 = {getTeam.getName(), getTeam.getMarkup() != null ? getTeam.getMarkup().toString() : "",
                        getTeam.getGrossMargin() != null ? getTeam.getGrossMargin().toString()
                                : "", String.valueOf(profiles.size()), getTeam.isArchived() ? "Yes" : "No", "EURO"};
                createHeaderAndDataRows(sheetTeam, 0, header1, data1, workbook);

                String[] header2 = {"Raw hourly rate", "Markup hourly rate", "GM hourly rate"};
                String[] data2 = {hourlyRates.rawRate() != null ? hourlyRates.rawRate().toString() : "", hourlyRates.markupRate() != null ? hourlyRates.markupRate().toString() : "", hourlyRates.grossMarginRate() != null ? hourlyRates.grossMarginRate().toString() : ""};
                createHeaderAndDataRows(sheetTeam, 3, header2, data2, workbook);

                String[] header3 = {"Raw day rate", "Markup day rate", "GM day rate"};
                String[] data3 = {dayRates.rawRate() != null ? dayRates.rawRate().toString() : "", dayRates.markupRate() != null ? dayRates.markupRate().toString() : "", dayRates.grossMarginRate() != null ? dayRates.grossMarginRate().toString() : ""};
                createHeaderAndDataRows(sheetTeam, 6, header3, data3, workbook);

                String[] header4 = {"Raw annual rate", "Markup annual rate", "GM annual rate"};
                String[] data4 = {annualRates.rawRate() != null ? annualRates.rawRate().toString() : "", annualRates.markupRate() != null ? annualRates.markupRate().toString() : "", annualRates.grossMarginRate() != null ? annualRates.grossMarginRate().toString() : ""};
                createHeaderAndDataRows(sheetTeam, 9, header4, data4, workbook);

                int profileHeaderIndex = 13;
                if (profiles.size() >= 1) {
                    String[] profileHeader = {"Name", "Utilization hours", "Hours", "Utilization rate", "Hourly rate", "Day rate"};
                    Row headerRow = sheetTeam.createRow(profileHeaderIndex);
                    for (int i = 0; i < profileHeader.length; i++)
                        headerRow.createCell(i).setCellValue(profileHeader[i]);

                    int rowIndex = profileHeaderIndex + 1;
                    TeamProfile tp = new TeamProfile();
                    for (Profile p : profiles) {
                        String[] profileData = {
                                p.getName(),
                                tp.getHourAllocation() != null ? tp.getHourAllocation().toString() : "",
                                profileService.totalHoursPercentage(p, tp.getHourAllocation()) != null ? profileService.totalHoursPercentage(p, tp.getHourAllocation()).toString() : "",
                                tp.getCostAllocation() != null ? tp.getCostAllocation().toString() : "",
                                profileService.hourlyRate(p, tp.getCostAllocation()) != null ? profileService.hourlyRate(p, tp.getCostAllocation()).toString() : "",
                                profileService.dayRate(p, tp.getCostAllocation()) != null ? profileService.dayRate(p, tp.getCostAllocation()).toString() : ""
                        };
                        createDataRows(sheetTeam, rowIndex++, profileData, workbook);
                    }
                } else {
                    String[] profileHeader = {"No profiles assigned"};
                    Row headerRow = sheetTeam.createRow(profileHeaderIndex);
                    for (int i = 0; i < profileHeader.length; i++)
                        headerRow.createCell(i).setCellValue(profileHeader[i]);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
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

    public void exportTable(TableView<?> tableView) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Sheet1");
        HSSFRow firstRow = hssfSheet.createRow(0);

        for (int i = 0; i < tableView.getColumns().size(); i++)
            firstRow.createCell(i).setCellValue(tableView.getColumns().get(i).getText());

        for (int row = 0; row < tableView.getItems().size(); row++) {

            HSSFRow hssfRow = hssfSheet.createRow(row + 1);

            for (int col = 0; col < tableView.getColumns().size(); col++) {
                if (tableView.getColumns().get(col).getCellObservableValue(row) == null)
                    continue;

                Object celValue = tableView.getColumns().get(col).getCellObservableValue(row).getValue();
                try {
                    if (celValue != null && Double.parseDouble(celValue.toString()) != 0.0) {
                        hssfRow.createCell(col).setCellValue(Double.parseDouble(celValue.toString()));
                    }
                } catch (NumberFormatException e) {
                    hssfRow.createCell(col).setCellValue(celValue.toString());
                }
            }
        }

        try {
            hssfWorkbook.write(new FileOutputStream("WorkBook.xls"));
            hssfWorkbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
