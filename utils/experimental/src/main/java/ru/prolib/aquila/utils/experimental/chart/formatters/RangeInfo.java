package ru.prolib.aquila.utils.experimental.chart.formatters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by TiM on 20.06.2017.
 */
public class RangeInfo {
    private final double minValue, maxValue, stepValue, firstValue, lastValue;

    public RangeInfo(double minValue, double maxValue, double stepValue, double firstValue, double lastValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepValue = stepValue;
        this.firstValue = firstValue;
        this.lastValue = lastValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStepValue() {
        return stepValue;
    }

    public double getFirstValue() {
        return firstValue;
    }

    public double getLastValue() {
        return lastValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("minValue", minValue)
                .append("maxValue", maxValue)
                .append("stepValue", stepValue)
                .append("firstValue", firstValue)
                .append("lastValue", lastValue)
                .toString();
    }
}
