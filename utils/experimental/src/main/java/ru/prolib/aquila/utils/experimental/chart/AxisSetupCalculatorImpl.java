package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class AxisSetupCalculatorImpl {

    public ValueAxisSetup getValueAxisSetup(CDecimal minValue,
    		CDecimal maxValue,
    		CDecimal valueTickSize,
    		int lengthPx,
    		int minGridStepPx)
    {
    	if ( lengthPx < minGridStepPx ) {
    		return new ValueAxisSetup(minValue, maxValue, lengthPx, 0, lengthPx);
    	}
    	int gridStepCount = lengthPx / minGridStepPx;
    	int x = lengthPx % minGridStepPx;
    	if ( x == 0 ) {
    		return new ValueAxisSetup(minValue, maxValue, lengthPx, gridStepCount, minGridStepPx);
    	}
    	x = lengthPx % gridStepCount;
    	if ( x == 0 ) {
    		minGridStepPx = lengthPx / gridStepCount;
    		return new ValueAxisSetup(minValue, maxValue, lengthPx, gridStepCount, minGridStepPx);
    	}
    	for ( int i = gridStepCount - 1; i >= 1; i -- ) {
    		x = lengthPx % i;
    		if ( x == 0 ) {
    			minGridStepPx = lengthPx / i;
    			return new ValueAxisSetup(minValue, maxValue, lengthPx, i, minGridStepPx);
    		}
    	}
    	throw new RuntimeException("Unexpected case");
    }

}
