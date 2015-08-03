package ru.prolib.aquila.core.indicator.function;

import ru.prolib.aquila.core.data.*;

@Deprecated
public class QuikEMAFunction extends MAFunction {

	public QuikEMAFunction(int period) {
		super(period);
	}
	
	public QuikEMAFunction() {
		super();
	}

	@Override
	public synchronized Double calculate(Series<Double> sourceSeries,
			Series<Double> ownSeries, int index)
					throws ValueException
	{
		Double value = sourceSeries.get(index);
		if ( index == 0 || value == null ) {
			return value;
		}
		Double EMAp = ownSeries.get(index - 1);
		if ( EMAp == null ) {
			return value;
		}
		return (EMAp * (period - 1) + 2 * value) / (period + 1);
	}

	@Override
	public synchronized String getDefaultId() {
		return "QuikEMA(" + period + ")";
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QuikEMAFunction.class ) {
			return false;
		}
		QuikEMAFunction o = (QuikEMAFunction) other;
		return period == o.period;
	}

}
