package ru.prolib.aquila.core.indicator;

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
 * <p>
 * 2013-03-11<br>
 * $Id: QUIK_EMA.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class QUIK_EMA extends _MA {
	
	public QUIK_EMA(String id, DataSeries source, int period, int limit) {
		super(id, source, period, limit);
	}
	
	public QUIK_EMA(String id, DataSeries source, int period) {
		this(id, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	public QUIK_EMA(DataSeries source, int period) {
		this(null, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}

	@Override
	protected Double calculate(int index) throws ValueException {
		Double value = source.get(index);
		if ( index == 0 || value == null ) {
			return value;
		}
		Double EMAp = series.get(index - 1);
		if ( EMAp == null ) {
			return value;
		}
		return (EMAp * (period - 1) + 2 * value) / (period + 1);
	}

	@Override
	protected String makeId(String id) {
		 return id == null ? "QUIK_EMA(" + period + ")" : id;
	}

}
