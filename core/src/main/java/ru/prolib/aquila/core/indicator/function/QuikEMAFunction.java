package ru.prolib.aquila.core.indicator.function;

import ru.prolib.aquila.core.data.*;

/**
 * Exponential Moving Average (EMA) по формуле QUIK.
 * <p>
 * Расчет выполняется по формуле:
 * <pre>
 * 		EMAi = (EMAi - 1 * (n - 1) + 2 * Pi) / (n + 1)
 * </pre>
 * где Pi - значение цены в текущем периоде, EMAi - значение EMA текущего
 * периода, EMAi-1 - значение EMA предыдущего периода. В качестве первого
 * значения берется значение источника как есть. 
 */
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
