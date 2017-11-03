package ru.prolib.aquila.utils.experimental.chart.swing.settings;

import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import java.util.List;

/**
 * Created by TiM on 02.11.2017.
 */
public class ChartSettings<TCategory> {
    private Double minValue = null;
    private Double maxValue = null;
    private final List<BarChartLayer<TCategory>> layers;

    public ChartSettings(List<BarChartLayer<TCategory>> layers) {
        this.layers = layers;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public List<BarChartLayer<TCategory>> getLayers() {
        return layers;
    }
}
