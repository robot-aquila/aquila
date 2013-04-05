package ru.prolib.aquila.core.data;

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
	 * @param valueId идентификатор
	 * @param candles ряд свечей
	 * @param getter геттер значения атрибута свечи
	 */
	public CandleDataSeries(String valueId, Series<Candle> candles,
			G<Double> getter)
	{
		super(valueId, candles, getter);
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
