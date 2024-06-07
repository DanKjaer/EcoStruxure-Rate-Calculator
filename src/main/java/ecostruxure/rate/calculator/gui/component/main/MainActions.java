package ecostruxure.rate.calculator.gui.component.main;

import java.util.function.Consumer;

public record MainActions(Runnable onShowGeography,
                          Runnable onShowProfiles,
                          Runnable onShowTeams,
                          Runnable onShowCurrency,
                          Consumer<Runnable> onChangeTheme,
                          Consumer<Runnable> onChangeLanguage,
                          Consumer<Runnable> onChangeCurrency) { }
