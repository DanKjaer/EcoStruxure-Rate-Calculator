package ecostruxure.rate.calculator.gui.component.currency;

import ecostruxure.rate.calculator.be.Currency;
import ecostruxure.rate.calculator.bll.service.CurrencyService;
import ecostruxure.rate.calculator.gui.common.CurrencyItemModel;

import java.util.ArrayList;
import java.util.List;

public class CurrencyInteractor {
    private final CurrencyModel model;

    private CurrencyService currencyService;
    private List<CurrencyItemModel> currencyModels;

    public CurrencyInteractor(CurrencyModel model, Runnable onFetchError) {
        this.model = model;

        try {
            currencyService = new CurrencyService();
        } catch (Exception e) {
            onFetchError.run();
        }
    }

    public boolean fetchCurrencies() {
        try {
            currencyModels = convertToCurrencyModels(currencyService.all());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateModel() {
        model.currencies().setAll(currencyModels);
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
}