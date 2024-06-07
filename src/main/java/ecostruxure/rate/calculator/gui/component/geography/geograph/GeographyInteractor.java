package ecostruxure.rate.calculator.gui.component.geography.geograph;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.bll.service.CountryService;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.gui.component.geography.GeographyModel;
import ecostruxure.rate.calculator.gui.component.geography.country.CountryInteractor;

import java.util.ArrayList;
import java.util.List;

public class GeographyInteractor {
    private final GeographyModel model;
    private GeographyService geographyService;
    private CountryService countryService;
    private CountryInteractor countryInteractor;
    private List<GeographyItemModel> geographyItemModels;

    public GeographyInteractor(CountryInteractor countryInteractor, GeographyModel model, Runnable onFetchError) {
        this.countryInteractor = countryInteractor;
        this.model = model;

        try {
            this.geographyService = new GeographyService();
            this.countryService = new CountryService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean getGeographies() {
        try {
            List<Geography> geographies = geographyService.allExceptCountries();

            geographyItemModels = convertToGeographiesModel(geographies);;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<GeographyItemModel> convertToGeographiesModel(List<Geography> geographies) throws Exception {
        List<GeographyItemModel> geographyItemModels = new ArrayList<>();

        for (Geography geography : geographies) {
            GeographyItemModel geographyItemModel = new GeographyItemModel();

            List<Country> countries = countryService.allByGeography(geography);

            geographyItemModel.idProperty().set(String.valueOf(geography.id()));
            geographyItemModel.nameProperty().set(geography.name());
            geographyItemModel.predefinedProperty().set(geography.predefined());

            geographyItemModel.countryList().clear();
            geographyItemModel.countryList().addAll(countries);

            geographyItemModels.add(geographyItemModel);
        }

        return geographyItemModels;
    }

    public void updateStats() {
        model.geographies().setAll(geographyItemModels);
        model.numGeographies().set(String.valueOf(model.geographies().size()));
        model.all().addAll(model.geographies());
    }
}
