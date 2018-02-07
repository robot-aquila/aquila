package ru.prolib.aquila.utils.experimental.chart.formatters;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Created by TiM on 20.06.2017.
 */
@Deprecated
public class RangeCalculatorImpl implements RangeCalculator {

    @Override
    public RangeInfo autoRange(double minValue, double maxValue, double length, double minStep) {
        return autoRange(minValue, maxValue, length, minStep, null);
    }

    @Override
    public RangeInfo autoRange(double minValue, double maxValue, double length, double minStep, Integer precision) {
        length = Math.abs(length);
        double firstVal = 0d, lastVal = 0d, stepValue = 0d;
        int stepCount;

        double minValueStep = 0;
        if(precision!=null){
            minValueStep = Math.pow(10d, -precision);
        }
        double range = maxValue - minValue;
        if(range > 0){
            stepCount = new Double(length/minStep).intValue();
            stepValue = -1;
            while (stepValue < minValueStep && stepCount > 0){
                stepValue = range/stepCount;
                double mul = Math.pow(10, Math.floor(Math.log10(stepValue)));
                double v = stepValue / mul;
                if(v < 2d){
                    v = 2d;
                } else if(v < 5d){
                    v = 5d;
                } else {
                    v = 10d;
                }
                stepValue = v * mul;
                if(stepValue < minValueStep){
                    stepCount--;
                }
            }
            firstVal = Math.ceil(minValue/stepValue)*stepValue;
            lastVal = Math.floor(maxValue/stepValue)*stepValue;
        } else {
            stepCount = 0;
        }
        if(stepCount == 0){
            stepValue = minValueStep;
            firstVal = Math.ceil(minValue/stepValue)*stepValue - stepValue;
            lastVal = Math.floor(maxValue/stepValue)*stepValue + stepValue;
            minValue = firstVal;
            maxValue = lastVal;
        }

        return new RangeInfo(minValue, maxValue, stepValue, firstVal, lastVal);
    }
    
}
