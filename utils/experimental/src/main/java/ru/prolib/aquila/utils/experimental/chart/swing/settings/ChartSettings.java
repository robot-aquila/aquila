package ru.prolib.aquila.utils.experimental.chart.swing.settings;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import java.util.List;

/**
 * Created by TiM on 02.11.2017.
 */
public class ChartSettings {
    private CDecimal minValue = null;
    private CDecimal maxValue = null;
    private final List<BarChartLayer> layers;

    public ChartSettings(List<BarChartLayer> layers) {
        this.layers = layers;
    }

    public CDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(CDecimal minValue) {
        this.minValue = minValue;
    }

    public CDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(CDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public List<BarChartLayer> getLayers() {
        return layers;
    }
}
