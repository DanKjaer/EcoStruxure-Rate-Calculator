package ecostruxure.rate.calculator.gui.component.modals.addprofile;

import ecostruxure.rate.calculator.be.*;
import ecostruxure.rate.calculator.be.enums.ResourceType;
import ecostruxure.rate.calculator.bll.service.CurrencyService;
import ecostruxure.rate.calculator.bll.service.GeographyService;
import ecostruxure.rate.calculator.bll.service.ProfileService;
import ecostruxure.rate.calculator.gui.common.CurrencyItemModel;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddProfileInteractor {
    private final AddProfileModel model;
    private ProfileService profileService;
    private CurrencyService currencyService;
    private GeographyService geographyService;

    private List<AddProfileGeographyItemModel> geographyModels;
    private List<CurrencyItemModel> currencyModels;

    public AddProfileInteractor(AddProfileModel model, Runnable onFetchError) {
        this.model = model;

        try {
            profileService = new ProfileService();
            currencyService = new CurrencyService();
            geographyService = new GeographyService();
        } catch (Exception e) {
            onFetchError.run();
        }

        model.nameIsValidProperty().bind(model.nameProperty().isNotEmpty());
        model.selectedGeographyIsValidProperty().bind(model.selectedGeographyProperty().isNotNull());
        model.selectedCurrencyIsValidProperty().bind(model.selectedCurrencyProperty().isNotNull());
        model.annualSalaryIsValidProperty().bind(model.annualSalaryProperty().isNotEmpty());
        model.annualEffectiveWorkingHoursIsValidProperty().bind(model.annualEffectiveWorkingHoursProperty().isNotEmpty());
        model.effectivenessIsValidProperty().bind(model.effectivenessProperty().isNotEmpty());
        model.hoursPerDayIsValidProperty().bind(model.hoursPerDayProperty().isNotEmpty());

        model.okToAddProperty().bind(Bindings.createBooleanBinding(
                this::isDataValid,
                model.nameProperty(),
                model.selectedGeographyProperty(),
                model.selectedCurrencyProperty(),
                model.annualSalaryProperty(),
                model.annualEffectiveWorkingHoursProperty(),
                model.effectivenessProperty(),
                model.hoursPerDayProperty())
        );
    }

    public boolean fetchData() {
        try {
            geographyModels = convertToGeographyModels(geographyService.all());
            currencyModels = convertToCurrencyModels(currencyService.all());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateModelAfterFetch() {
        model.locations().setAll(geographyModels);
        model.currencies().setAll(currencyModels);
    }

    public boolean addProfile() {
        try {
            profileService.create(createProfileFromModel());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Profile createProfileFromModel() {
        var profileData = new ProfileData();

        var currencyItemModel = model.selectedCurrencyProperty().get();
        BigDecimal eurConversionRate = new BigDecimal(currencyItemModel.eurConversionRateProperty().get());

        profileData.name((model.nameProperty().get()));
        profileData.currency(currencyItemModel.currencyCodeProperty().get());
        profileData.geography(model.selectedGeographyProperty().get().idProperty().get());
        profileData.overhead(model.selectedResourceTypeProperty().get() == ResourceType.OVERHEAD);
        profileData.archived(false);

        var profile = new Profile();
        profile.annualSalary(new BigDecimal(model.annualSalaryProperty().get()).multiply(eurConversionRate));
        profile.effectiveness(new BigDecimal(model.effectivenessProperty().get()));
        profile.effectiveWorkHours(new BigDecimal(model.annualEffectiveWorkingHoursProperty().get()));
        profile.hoursPerDay(new BigDecimal(model.hoursPerDayProperty().get()));
        profile.profileData(profileData);

        return profile;
    }

    private List<AddProfileGeographyItemModel> convertToGeographyModels(List<Geography> geographies) {
        List<AddProfileGeographyItemModel> geographyModels = new ArrayList<>();

        for (Geography geography : geographies) {
            AddProfileGeographyItemModel geographyModel = new AddProfileGeographyItemModel();
            geographyModel.idProperty().set(geography.id());
            geographyModel.nameProperty().set(geography.name());
            geographyModels.add(geographyModel);
        }

        return geographyModels;
    }

    private List<CurrencyItemModel> convertToCurrencyModels(List<Currency> currencies) {
        List<CurrencyItemModel> currencyModels = new ArrayList<>();

        for (Currency currency : currencies) {
            CurrencyItemModel currencyModel = new CurrencyItemModel();
            currencyModel.currencyCodeProperty().set(currency.currencyCode());
            currencyModel.eurConversionRateProperty().set(currency.eurConversionRate().toString());
            currencyModel.usdConversionRateProperty().set(currency.usdConversionRate().toString());
            currencyModels.add(currencyModel);
        }

        return currencyModels;
    }

    public void updateModelAfterAdd() {
        model.nameProperty().set("");
        model.selectedGeographyProperty().set(null);
        model.selectedCurrencyProperty().set(null);
        model.selectedResourceTypeProperty().set(ResourceType.OVERHEAD);
        model.annualSalaryProperty().set("");
        model.annualEffectiveWorkingHoursProperty().set("0");
        model.effectivenessProperty().set("");
        model.hoursPerDayProperty().set("8");

        model.locations().setAll(geographyModels);
        model.currencies().setAll(currencyModels);
    }

    private boolean isDataValid() {
        return !model.nameProperty().get().isEmpty() &&
                model.selectedGeographyProperty().get() != null &&
                model.selectedCurrencyProperty().get() != null &&
                !model.annualSalaryProperty().get().isEmpty() &&
                !model.annualEffectiveWorkingHoursProperty().get().isEmpty() &&
                !model.effectivenessProperty().get().isEmpty() &&
                !model.hoursPerDayProperty().get().isEmpty();
    }
}