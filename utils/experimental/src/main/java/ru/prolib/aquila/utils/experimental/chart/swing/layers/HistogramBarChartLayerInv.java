package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import ru.prolib.aquila.core.data.Series;

/**
 * Created by TiM on 13.09.2017.
 */
public class HistogramBarChartLayerInv<TCategory> extends HistogramBarChartLayer {

    public HistogramBarChartLayerInv(Series data) {
        super(data);
    }

    @Override
    protected double getMaxValue(Number value) {
        return 0;
    }

    @Override
    protected double getMinValue(Number value) {
        return -value.doubleValue();
    }

    protected int getSign(){
        return -1;
    }
}
