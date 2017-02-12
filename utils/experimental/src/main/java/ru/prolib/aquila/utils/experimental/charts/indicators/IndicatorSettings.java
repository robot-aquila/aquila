package ru.prolib.aquila.utils.experimental.charts.indicators;

import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;

/**
 * Created by TiM on 12.02.2017.
 */
public class IndicatorSettings {
    private final Calculator calculator;
    private final String styleClass;

    public IndicatorSettings(Calculator calculator, String styleClass) {
        this.calculator = calculator;
        this.styleClass = styleClass;
    }

    public Calculator getCalculator() {
        return calculator;
    }

    public String getStyleClass() {
        return styleClass;
    }
}
