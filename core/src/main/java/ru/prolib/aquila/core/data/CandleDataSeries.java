package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventSystem;


/**
 * Ряд данных для конвертации атрибута свечи в обособленный ряд.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleDataSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleDataSeries extends CandleProxy<Double>
		implements DataSeries
{

	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param valueId идентификатор
	 * @param candles ряд свечей
	 * @param getter геттер значения атрибута свечи
	 */
	public CandleDataSeries(EventSystem es, String valueId,
			Series<Candle> candles, GCandlePart<Double> getter)
	{
		super(es, valueId, candles, getter);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == CandleDataSeries.class
			? fieldsEquals(other) : false;
	}

}
