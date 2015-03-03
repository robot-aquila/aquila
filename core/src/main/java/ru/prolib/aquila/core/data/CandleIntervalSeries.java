package ru.prolib.aquila.core.data;

import org.joda.time.Interval;

import ru.prolib.aquila.core.EventSystem;

/**
 * Ряд временных интервалов.
 * <p>
 * Используется для конвертации данных свечей ряда в обособленный ряд.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleTimeSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleIntervalSeries extends CandleProxy<Interval>
	implements IntervalSeries
{

	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param valueId идентификатор
	 * @param candles ряд свечей
	 */
	public CandleIntervalSeries(EventSystem es, String valueId,
			Series<Candle> candles) 
	{
		super(es, valueId, candles, new GCandleInterval());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == CandleIntervalSeries.class
			? fieldsEquals(other) : false;
	}

}
