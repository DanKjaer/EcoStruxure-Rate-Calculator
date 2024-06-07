package ecostruxure.rate.calculator.bll;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Team;
import ecostruxure.rate.calculator.be.enums.AdjustmentType;
import ecostruxure.rate.calculator.be.enums.RateType;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

class RateServiceTest {
    private RateService rateService;
    private ProfileService profileService;


    @BeforeEach
    void setUp() throws Exception {
        profileService = new ProfileService();
        rateService = new RateService();
    }

    @Test
    void it_calculateRate_HourlyRaw() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedHourlyRate = profileService.hourlyRate(profiles);

        var result = rateService.calculateRate(team, RateType.HOURLY, AdjustmentType.RAW);

        assertThat(result).isEqualTo(expectedHourlyRate);
    }

    @Test
    void it_calculateRate_DayRaw() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedDayRate = profileService.dayRate(profiles);

        var result = rateService.calculateRate(team, RateType.DAY, AdjustmentType.RAW);

        assertThat(result).isEqualTo(expectedDayRate);
    }

    @Test
    void it_calculateRate_AnnualRaw() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedAnnualRate = profileService.annualCost(profiles);

        var result = rateService.calculateRate(team, RateType.ANNUAL, AdjustmentType.RAW);

        assertThat(result).isEqualTo(expectedAnnualRate);
    }

    @Test
    void it_calculateRate_HourlyMarkup() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedHourlyRate = profileService.hourlyRate(profiles);
        BigDecimal expectedAdjustedRate = rateService.applyMarkup(expectedHourlyRate, team.markup());

        var result = rateService.calculateRate(team, RateType.HOURLY, AdjustmentType.MARKUP);

        assertThat(result).isEqualTo(expectedAdjustedRate);
    }

    @Test
    void it_calculateRate_DayMarkup() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedDayRate = profileService.dayRate(profiles);
        BigDecimal expectedAdjustedRate = rateService.applyMarkup(expectedDayRate, team.markup());

        var result = rateService.calculateRate(team, RateType.DAY, AdjustmentType.MARKUP);

        assertThat(result).isEqualTo(expectedAdjustedRate);
    }

    @Test
    void it_calculateRate_AnnualMarkup() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedAnnualRate = profileService.annualCost(profiles);
        BigDecimal expectedAdjustedRate = rateService.applyMarkup(expectedAnnualRate, team.markup());

        var result = rateService.calculateRate(team, RateType.ANNUAL, AdjustmentType.MARKUP);

        assertThat(result).isEqualTo(expectedAdjustedRate);
    }

    @Test
    void it_calculateRate_HourlyGrossMargin() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedHourlyRate = profileService.hourlyRate(profiles);
        BigDecimal expectedAdjustedRate = rateService.applyGrossMargin(rateService.applyMarkup(expectedHourlyRate, team.markup()), team.grossMargin());

        var result = rateService.calculateRate(team, RateType.HOURLY, AdjustmentType.GROSS_MARGIN);

        assertThat(result).isEqualTo(expectedAdjustedRate);
    }

    @Test
    void it_calculateRate_DayGrossMargin() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedDayRate = profileService.dayRate(profiles);
        BigDecimal expectedAdjustedRate = rateService.applyGrossMargin(rateService.applyMarkup(expectedDayRate, team.markup()), team.grossMargin());

        var result = rateService.calculateRate(team, RateType.DAY, AdjustmentType.GROSS_MARGIN);

        assertThat(result).isEqualTo(expectedAdjustedRate);
    }

    @Test
    void it_calculateRate_AnnualGrossMargin() throws Exception {
        Team team = new Team(11, "Innovation and R&D Center", new BigDecimal("20.00"), new BigDecimal("35.00"), false);
        List<Profile> profiles = profileService.allByTeam(team);
        BigDecimal expectedAnnualRate = profileService.annualCost(profiles);
        BigDecimal expectedAdjustedRate = rateService.applyGrossMargin(rateService.applyMarkup(expectedAnnualRate, team.markup()), team.grossMargin());

        var result = rateService.calculateRate(team, RateType.ANNUAL, AdjustmentType.GROSS_MARGIN);

        assertThat(result).isEqualTo(expectedAdjustedRate);
    }

    @Test
    void applyMarkup() {
        assertThat(rateService.applyMarkup(new BigDecimal("100.00"), new BigDecimal("20.00"))).isEqualTo(new BigDecimal("120.00"));
        assertThat(rateService.applyMarkup(new BigDecimal("100.00"), BigDecimal.ZERO)).isEqualTo(new BigDecimal("100.00"));
        assertThat(rateService.applyMarkup(new BigDecimal("100.00"), new BigDecimal("-20.00"))).isEqualTo(new BigDecimal("80.00"));
        assertThat(rateService.applyMarkup(new BigDecimal("50.00"), new BigDecimal("0.01"))).isEqualTo(new BigDecimal("50.01"));
        assertThat(rateService.applyMarkup(new BigDecimal("50.00"), new BigDecimal("100.00"))).isEqualTo(new BigDecimal("100.00"));
        assertThat(rateService.applyMarkup(new BigDecimal("50.00"), new BigDecimal("-100.00"))).isEqualTo(new BigDecimal("0.00"));
        assertThat(rateService.applyMarkup(new BigDecimal("100.00"), new BigDecimal("33.33"))).isEqualTo(new BigDecimal("133.33"));
        assertThat(rateService.applyMarkup(new BigDecimal("0.00"), new BigDecimal("50.00"))).isEqualTo(new BigDecimal("0.00"));

    }

    @Test
    void applyGrossMargin() {
        assertThat(rateService.applyGrossMargin(new BigDecimal("120.00"), new BigDecimal("20.00"))).isEqualTo(new BigDecimal("150.00"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("100.00"), BigDecimal.ZERO)).isEqualTo(new BigDecimal("100.00"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("100.00"), new BigDecimal("-20.00"))).isEqualTo(new BigDecimal("83.33"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("50.00"), new BigDecimal("0.01"))).isEqualTo(new BigDecimal("50.01"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("50.00"), new BigDecimal("100.00"))).isEqualTo(new BigDecimal("100.00"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("50.00"), new BigDecimal("-100.00"))).isEqualTo(new BigDecimal("25.00"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("100.00"), new BigDecimal("33.33"))).isEqualTo(new BigDecimal("149.99"));
        assertThat(rateService.applyGrossMargin(new BigDecimal("0.00"), new BigDecimal("50.00"))).isEqualTo(new BigDecimal("0.00"));
    }
}