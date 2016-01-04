package ru.prolib.aquila.core.data;

import java.time.Instant;
import org.threeten.extra.Interval;

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
	public Series<Double> getOpenSeries();

	/**
	 * Получить ряд данных, соответствующий максимальной цене свечи.
	 * <p>
	 * @return ряд
	 */
	public Series<Double> getHighSeries();
	
	/**
	 * Получить ряд данных, соответствующий минимальной цене свечи.
	 * <p>
	 * @return ряд
	 */
	public Series<Double> getLowSeries();
	
	/**
	 * Получить ряд данных, соответствующий цене закрытия свечи.
	 * <p>
	 * @return ряд
	 */
	public Series<Double> getCloseSeries();
	
	/**
	 * Получить ряд данных, соответствующий объему торгов свечи.
	 * <p>
	 * @return ряд
	 */
	public Series<Double> getVolumeSeries();
	
	/**
	 * Получить ряд данных, соответствующий интервалу свечи.
	 * <p>
	 * @return ряд
	 */
	public Series<Interval> getIntervalSeries();
	
	/**
	 * Получить точку актуальности.
	 * <p>
	 * @return время точки актуальности
	 */
	public Instant getPOA();
	
	/**
	 * Получить таймфрейм свечей.
	 * <p>
	 * @return таймфрейм
	 */
	public TimeFrame getTimeFrame();
	
	/**
	 * Найти первую внутридневную свечу.
	 * <p>
	 * Ищет самую раннюю свечу текущего дня начиная с конца последовательности.
	 * <p>
	 * @return свеча или null, если последовательность пуста
	 */
	public Candle findFirstIntradayCandle();

}
