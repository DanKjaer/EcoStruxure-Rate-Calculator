package ecostruxure.rate.calculator.bll.service;

import ecostruxure.rate.calculator.be.Currency;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;


class CurrencyServiceTest {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
    }

    @AfterEach
    void tearDown() {

    }

    void writeToFile(String content) throws IOException {
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
    }

    @Test
    void parseCSV_NormalCase() throws Exception {
        String csvContent = "eur,1.07,1.28\nusd,1.09,1.23";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().currencyCode()).isEqualTo("eur");
        assertThat(result.getFirst().eurConversionRate()).isEqualTo(new BigDecimal("1"));
        assertThat(result.getFirst().usdConversionRate()).isEqualTo(new BigDecimal("1.28"));
    }

    @Test
    void parseCSV_EmptyFile() throws Exception {
        writeToFile("");
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).isEmpty();
    }

    @Test
    void parseCSV_InvalidFormat() throws Exception {
        String csvContent = "eur,1.07\nnok,1.09,1.23,extra";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().currencyCode()).isEqualTo("nok");
        assertThat(result.getFirst().eurConversionRate()).isEqualTo(new BigDecimal("1.09"));
        assertThat(result.getFirst().usdConversionRate()).isEqualTo(new BigDecimal("1.23"));
    }

    @Test
    void parseCSV_InvalidDataTypes() throws Exception {
        String csvContent = "nok,abc,1.28\njpy,1.09,xyz";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).isEmpty();
    }

    @Test
    void parseCSV_MissingFields() throws Exception {
        String csvContent = "eur,,1.28\nusd,1.09,\n";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).isEmpty();
    }

    @Test
    void parseCSV_ExtraFields() throws Exception {
        String csvContent = "eur,1.07,1.28,extra\nusd,1.09,1.23,another,field";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).hasSize(2);
        assertThat(result.get(0).currencyCode()).isEqualTo("eur");
        assertThat(result.get(1).currencyCode()).isEqualTo("usd");
        assertThat(result.get(0).usdConversionRate()).isEqualTo(new BigDecimal("1.28"));
    }

    @Test
    void parseCSV_WhitespaceIssues() throws Exception {
        String csvContent = "eur, 1.07 , 1.28\n usd , 1.09 , 1.23 ";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result.getFirst().eurConversionRate()).isEqualTo(new BigDecimal("1"));
    }

    @Test
    void parseCSV_CorruptedLines() throws Exception {
        String csvContent = "eur 1.07 1.28\nusd:1.09,1.23";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).isEmpty();
    }

    @Test
    void parseCSV_DuplicateEntries() throws Exception {
        String csvContent = "eur,1.07,1.28\neur,1.10,1.30";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().currencyCode()).isEqualTo("eur");
        assertThat(result.getFirst().eurConversionRate()).isEqualTo(new BigDecimal("1"));
        assertThat(result.getFirst().usdConversionRate()).isEqualTo(new BigDecimal("1.30"));
    }

    @Test
    void parseCSV_NonStandardLineBreaks() throws Exception {
        String csvContent = "eur,1.07,1.28\r\nusd,1.09,1.23\naud,1.45,1.67";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).hasSize(3);
    }

    @Test
    void parseCSV_IgnoreExtraFieldsAndHandleSkips() throws Exception {
        String csvContent = "eur,1.07\nusd,1.09,1.23,extra\njpy, ,2.50";
        writeToFile(csvContent);
        List<Currency> result = CurrencyService.parseCSV(tempFile.getAbsolutePath());
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().currencyCode()).isEqualTo("usd");
        assertThat(result.getFirst().eurConversionRate()).isEqualTo(new BigDecimal("1.09"));
        assertThat(result.getFirst().usdConversionRate()).isEqualTo(new BigDecimal("1"));
    }

    @Test
    void parseCSV_FullFile() throws Exception {
        String csvContent = "currency,eur_rate,usd_rate,extra_field\n" +
                "EUR,1.87,1.11,ignored\n" +
                "USD,1.10,3.23,something extra\n" +
                "GBP,0.85,1.22,\n" +
                "JPY,113.5,0.0089,note\n" +
                "CAD,1.31,0.97\n" +
                "AUD,not a number,0.69\n" +
                "CHF,,1.09\n" +
                "NOK,10.50,1.15,extra\n" +
                "DKK,7.44,0.16\n" +
                "SEK,10.33,\n" +
                "NZD,1.52,0.66\n" +
                "HKD,7.77,2.27,invalid,extra\n" +
                "MXN,20.27,0.05";

        Path tempFile = Files.createTempFile("test_currencies", ".csv");
        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write(csvContent);
        }

        List<Currency> result = CurrencyService.parseCSV(tempFile.toString());

        assertThat(result).hasSize(10); // 13 rows, 3 invalid (AUD, CHF, SEK) = 10 valid

        // Special case checks
        Currency eur = result.stream().filter(c -> c.currencyCode().equalsIgnoreCase("EUR")).findFirst().orElse(null);
        Currency usd = result.stream().filter(c -> c.currencyCode().equalsIgnoreCase("USD")).findFirst().orElse(null);

        assertThat(eur).isNotNull();
        assertThat(eur.eurConversionRate()).isEqualTo(BigDecimal.ONE);
        assertThat(eur.usdConversionRate()).isEqualTo(new BigDecimal("1.11"));

        assertThat(usd).isNotNull();
        assertThat(usd.eurConversionRate()).isEqualTo(new BigDecimal("1.10"));
        assertThat(usd.usdConversionRate()).isEqualTo(BigDecimal.ONE);

        // Check some other valid currencies
        Currency gbp = result.stream().filter(c -> c.currencyCode().equalsIgnoreCase("GBP")).findFirst().orElse(null);
        Currency jpy = result.stream().filter(c -> c.currencyCode().equalsIgnoreCase("JPY")).findFirst().orElse(null);

        assertThat(gbp).isNotNull();
        assertThat(gbp.eurConversionRate()).isEqualTo(new BigDecimal("0.85"));
        assertThat(gbp.usdConversionRate()).isEqualTo(new BigDecimal("1.22"));

        assertThat(jpy).isNotNull();
        assertThat(jpy.eurConversionRate()).isEqualTo(new BigDecimal("113.5"));
        assertThat(jpy.usdConversionRate()).isEqualTo(new BigDecimal("0.0089"));
    }
}