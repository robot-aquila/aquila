package ru.prolib.aquila.core.data;

import java.util.Date;

/**
 * Ряд временных меток для конвертации времени свечи в обособленный ряд.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleTimeSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleTimeSeries extends CandleProxy<Date> implements TimeSeries {

	/**
	 * Конструктор.
	 * <p>
	 * @param valueId идентификатор
	 * @param candles ряд свечей
	 */
	public CandleTimeSeries(String valueId, Series<Candle> candles) {
		super(valueId, candles, new GCandleTime());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == CandleTimeSeries.class
			? fieldsEquals(other) : false;
	}

}
