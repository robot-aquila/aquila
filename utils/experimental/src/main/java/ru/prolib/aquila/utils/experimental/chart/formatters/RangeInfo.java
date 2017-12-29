package ru.prolib.aquila.utils.experimental.chart.formatters;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Created by TiM on 20.06.2017.
 */
@Deprecated
public class RangeInfo {
    private final double minValue, maxValue, stepValue, firstValue, lastValue;

    public RangeInfo(CDecimal minValue, CDecimal maxValue, CDecimal stepValue, CDecimal firstValue, CDecimal lastValue) {
        this.minValue = minValue.toBigDecimal().doubleValue();
        this.maxValue = maxValue.toBigDecimal().doubleValue();
        this.stepValue = stepValue.toBigDecimal().doubleValue();
        this.firstValue = firstValue.toBigDecimal().doubleValue();
        this.lastValue = lastValue.toBigDecimal().doubleValue();
    }

    public RangeInfo(double minValue, double maxValue, double stepValue, double firstValue, double lastValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepValue = stepValue;
        this.firstValue = firstValue;
        this.lastValue = lastValue;
    }

    public CDecimal getMinValue() {
        return minValue;
    }

    public CDecimal getMaxValue() {
        return maxValue;
    }

    public CDecimal getStepValue() {
        return stepValue;
    }

    public CDecimal getFirstValue() {
        return firstValue;
    }

    public CDecimal getLastValue() {
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
