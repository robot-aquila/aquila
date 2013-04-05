package ru.prolib.aquila.core.data;

/**
 * Интерфейс ряда свечей.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public interface CandleSeries extends Series<Candle> {
	
	/**
	 * Получить ряд данных, соответствующий цене открытия свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getOpen();

	/**
	 * Получить ряд данных, соответствующий максимальной цене свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getHigh();
	
	/**
	 * Получить ряд данных, соответствующий минимальной цене свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getLow();
	
	/**
	 * Получить ряд данных, соответствующий цене закрытия свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getClose();
	
	/**
	 * Получить ряд данных, соответствующий объему торгов свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getVolume();
	
	/**
	 * Получить ряд данных, соответствующий времени открытия свечи.
	 * <p>
	 * @return ряд
	 */
	public TimeSeries getTime();

}
