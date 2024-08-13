package ecostruxure.rate.calculator.bll;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProfileServiceTest {
    private ProfileService service;
    private Profile profile;

    @BeforeEach
    void setUp() throws Exception {
        service = new ProfileService();
        profile = new Profile();

        profile.annualSalary(new BigDecimal("120000.1234"));
        profile.effectiveness(new BigDecimal("0.15")); // 15%
        profile.effectiveWorkHours(new BigDecimal("2080"));
        profile.hoursPerDay(new BigDecimal("8"));
    }

    @Test
    void hourlyRateCalculatesCorrectly() {
        BigDecimal expectedHourlyRate = new BigDecimal("23.08").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualHourlyRate = service.hourlyRate(profile);
        assertThat(actualHourlyRate).isEqualTo(expectedHourlyRate);
    }

    @Test
    void dayRateCalculatesCorrectly() {
        BigDecimal expectedDayRate = new BigDecimal("184.64").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualDayRate = service.dayRate(profile);
        assertThat(actualDayRate).isEqualTo(expectedDayRate);
    }

    @Test
    void hourlyRateHandlesLargeValues() {
        profile.annualSalary(new BigDecimal("1000000000.0000"));
        profile.effectiveness(new BigDecimal("0.5"));
        profile.effectiveWorkHours(new BigDecimal("1"));
        BigDecimal expectedHourlyRate = new BigDecimal("2000000000.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualHourlyRate = service.hourlyRate(profile);
        assertThat(actualHourlyRate).isEqualTo(expectedHourlyRate);
    }

    @Test
    void hourlyAndDayRateHandlesNull() {
        assertThrows(NullPointerException.class, () -> service.hourlyRate((Profile) null));
        assertThrows(NullPointerException.class, () -> service.hourlyRate((List<Profile>) null));
        assertThrows(NullPointerException.class, () -> service.dayRate((Profile) null));
        assertThrows(NullPointerException.class, () -> service.dayRate((List<Profile>) null));
    }

    @Test
    void hourlyRateSmallNumbers() {
        profile.annualSalary(new BigDecimal("1.0001"));
        profile.effectiveWorkHours(new BigDecimal("1000"));
        BigDecimal expectedHourlyRate = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualHourlyRate = service.hourlyRate(profile);
        assertThat(actualHourlyRate).isEqualTo(expectedHourlyRate);
    }

    @Test
    void hourlyRateMaxSalary() {
        profile.annualSalary(new BigDecimal("999999999999999.9999"));
        profile.effectiveness(new BigDecimal("0.20")); // 20%
        profile.effectiveWorkHours(new BigDecimal("2000.00"));
        BigDecimal expectedHourlyRate = new BigDecimal("100000000005.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualHourlyRate = service.hourlyRate(profile);
        assertThat(actualHourlyRate).isEqualTo(expectedHourlyRate);
    }

    @Test
    void hourlyRateMinSalary() {
        profile.annualSalary(new BigDecimal("0.0001"));
        profile.effectiveness(new BigDecimal("0.20")); // 20%
        profile.effectiveWorkHours(new BigDecimal("2000.00"));
        BigDecimal expectedHourlyRate = new BigDecimal("5.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualHourlyRate = service.hourlyRate(profile);
        assertThat(actualHourlyRate).isEqualTo(expectedHourlyRate);
    }
}