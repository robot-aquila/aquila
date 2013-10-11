package ru.prolib.aquila.core.data;

import org.joda.time.DateTime;

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
	public DataSeries getOpenSeries();

	/**
	 * Получить ряд данных, соответствующий максимальной цене свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getHighSeries();
	
	/**
	 * Получить ряд данных, соответствующий минимальной цене свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getLowSeries();
	
	/**
	 * Получить ряд данных, соответствующий цене закрытия свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getCloseSeries();
	
	/**
	 * Получить ряд данных, соответствующий объему торгов свечи.
	 * <p>
	 * @return ряд
	 */
	public DataSeries getVolumeSeries();
	
	/**
	 * Получить ряд данных, соответствующий интервалу свечи.
	 * <p>
	 * @return ряд
	 */
	public IntervalSeries getIntervalSeries();
	
	/**
	 * Получить точку актуальности.
	 * <p>
	 * @return время точки актуальности
	 */
	public DateTime getPOA();
	
	/**
	 * Получить таймфрейм свечей.
	 * <p>
	 * @return таймфрейм
	 */
	public Timeframe getTimeframe();
	
	/**
	 * Найти первую внутридневную свечу.
	 * <p>
	 * Ищет самую раннюю свечу текущего дня начиная с конца последовательности.
	 * <p>
	 * @return свеча или null, если последовательность пуста
	 */
	public Candle findFirstIntradayCandle();

}
