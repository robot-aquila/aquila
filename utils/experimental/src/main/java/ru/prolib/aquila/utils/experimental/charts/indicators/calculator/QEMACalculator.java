package ru.prolib.aquila.utils.experimental.charts.indicators.calculator;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ta.QEMA;

/**
 * Created by TiM on 04.05.2017.
 */
public class QEMACalculator implements Calculator<Double, Double> {

    private final static String ID_PREFIX = "QEMA";
    private final static String NAME_PREFIX = "QEMA ";
    private final int period;

    public QEMACalculator(int period) {
        this.period = period;
    }

    @Override
    public String getId() {
        return ID_PREFIX+period;
    }

    @Override
    public String getName() {
        return NAME_PREFIX+period;
    }

    @Override
    public Series<Double> calculate(Series<Double> data) {
        return new QEMA(getId(), data, period);
    }
}
