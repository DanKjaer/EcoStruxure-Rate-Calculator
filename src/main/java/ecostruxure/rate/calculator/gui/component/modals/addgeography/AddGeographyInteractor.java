package ecostruxure.rate.calculator.gui.component.modals.addgeography;

import ecostruxure.rate.calculator.be.Country;
import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.bll.service.CountryService;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.gui.util.constants.LocalizedText;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddGeographyInteractor {
    private final AddGeographyModel model;
    private CountryService countryService;
    private GeographyService geographyService;
    private List<CountryInfo> countryItemModels;
    private boolean exists;
    private Geography geography;

    public AddGeographyInteractor(AddGeographyModel model, Runnable onFetchError) {
        this.model = model;

        try {
            this.countryService = new CountryService();
            this.geographyService = new GeographyService();
        } catch (IOException e) {
            onFetchError.run();
        }

        setupSearchFilter();

        model.selectedGeographyNameProperty().set(LocalizedText.GEOGRAPHY_ADD_AT_LEAST_TWO_COUNTRIES.get());

        model.nameIsValidProperty().bind(model.nameProperty().isNotEmpty());

        model.okToCreateProperty().bind(Bindings.createBooleanBinding(
                this::dataIsValid,
                model.nameProperty(),
                model.selectedCountries(),
                model.alreadyExistsProperty())
        );
    }

    public boolean createGeography() {
        try {
            geographyService.create(createGeographyFromModel(), createCountriesFromModel());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Geography createGeographyFromModel() {
        return new Geography(model.nameProperty().get());
    }

    private List<Country> createCountriesFromModel() {
        List<Country> countries = new ArrayList<>();
        for (CountryInfo countryInfo : model.selectedCountries()) {
            countries.add(new Country(countryInfo.codeProperty().get()));
        }
        return countries;
    }


    public boolean fetchCountries() {
        try {
            countryItemModels = convertoToCountryItemModels(countryService.all());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean geographyExists() {
        try {
            geography = geographyService.get(createCountriesFromModel());
            exists = geography != null;
            return true;
        } catch (Exception e) {
            exists = false;
            return false;
        }
    }

    public void updateModalAfterCheck() {
        model.alreadyExistsProperty().set(exists);
        if (model.selectedCountries().size() < 2) {
            model.validGeographyProperty().set(false);
            model.selectedGeographyNameProperty().set(LocalizedText.GEOGRAPHY_ADD_AT_LEAST_TWO_COUNTRIES.get());
        } else if (exists) {
            model.validGeographyProperty().set(false);
            model.selectedGeographyNameProperty().set(LocalizedText.GEOGRAPHY.get() + " " + geography.name() + " " + LocalizedText.ALREADY_EXISTS.get());
        } else {
            model.validGeographyProperty().set(true);
            model.selectedGeographyNameProperty().set(LocalizedText.GEOGRAPHY_DOES_NOT_EXIST.get());
        }
    }

    public void updateModel() {
        model.countries().setAll(countryItemModels);
    }

    public void updateModelAfterAdd() {
        model.nameProperty().set("");
        model.searchProperty().set("");
        model.selectedGeographyNameProperty().set(LocalizedText.GEOGRAPHY_ADD_AT_LEAST_TWO_COUNTRIES.get());
        model.alreadyExistsProperty().set(false);
        model.validGeographyProperty().set(false);
        model.countries().forEach(country -> country.selectedProperty().set(false));
        model.selectedCountries().clear();
    }

    private List<CountryInfo> convertoToCountryItemModels(List<Country> countries) {
        List<CountryInfo> items = new ArrayList<>();

        for (Country country : countries) {
            CountryInfo model = new CountryInfo();
            model.codeProperty().set(country.code());
            model.nameProperty().set(country.name());
            items.add(model);
        }

        return items;
    }

    private void setupSearchFilter() {
        model.searchProperty().addListener((obs, ov, nv) -> {
            model.filteredCountries().setPredicate(country -> {
                if (nv == null || nv.isEmpty()) return true;
                String lowerCaseFilter = nv.toLowerCase();
                return country.nameProperty().get().toLowerCase().contains(lowerCaseFilter) ||
                        country.codeProperty().get().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private boolean dataIsValid() {
        return !model.nameProperty().get().isEmpty() &&
                model.selectedCountries().size() >= 2 &&
                !model.alreadyExistsProperty().get();
    }
}