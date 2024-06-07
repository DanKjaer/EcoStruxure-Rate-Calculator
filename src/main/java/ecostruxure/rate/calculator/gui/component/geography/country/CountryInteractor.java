package ecostruxure.rate.calculator.gui.component.geography.country;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.bll.service.CountryService;
import ecostruxure.rate.calculator.gui.component.geography.GeographyModel;

import java.util.ArrayList;
import java.util.List;

public class CountryInteractor {
    private final GeographyModel model;
    private CountryService countryService;
    private List<CountryItemModel> countryItemModels;

    public CountryInteractor(GeographyModel model, Runnable onFetchError) {
        this.model = model;

        try {
            this.countryService = new CountryService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean getCountries() {
        try {
            List<Country> countries = countryService.all();

            countryItemModels = convertToCountriesModel(countries);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CountryItemModel> convertToCountriesModel(List<Country> countries) {
        List<CountryItemModel> countryItemModels = new ArrayList<>();

        for (Country country : countries) {
            CountryItemModel countryItemModel = new CountryItemModel();

            countryItemModel.codeProperty().set(country.code());
            countryItemModel.nameProperty().set(country.name());
            countryItemModel.latitudeProperty().set(country.latitude());
            countryItemModel.longitudeProperty().set(country.longitude());

            countryItemModels.add(countryItemModel);
        }

        return countryItemModels;
    }

    public void updateStats() {
        model.countries().setAll(countryItemModels);
        model.numCountries().set(String.valueOf(model.countries().size()));
        model.all().addAll(model.countries());
    }
}
