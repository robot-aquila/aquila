package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.data.*;

/**
 * Wilder Moving Average (WilderMA).
 * <p>
 * Расчет выполняется по формуле:
 * <pre>
 *	MAi = (MAp * (n - 1) + Pi) / n
 * </pre>
 * Где: MAi - значение скользящей средней в текущем периоде, MAp - значение
 * скользящей средней предыдущего периода, n - период скользящей средней,
 * Pi - цена в текущем периоде. Для первого периода берется исходное значение
 * как есть.
 * <p>
 * 2013-03-12<br>
 * $Id: WilderMA.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class WilderMA extends _MA {

	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param id символьный идентификатор
	 * @param source источник данных (исходный ряд)
	 * @param period период скользящей средней
	 * @param limit лимит хранилища
	 * @throws ValueException ошибка перерасчета значений индикатора
	 */
	public WilderMA(EventSystem es, String id, Series<Double> source, int period,
			int limit) throws ValueException
	{
		super(es, id, source, period, limit);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param id символьный идентификатор
	 * @param source источник данных (исходный ряд)
	 * @param period период скользящей средней
	 * @throws ValueException ошибка перерасчета значений индикатора
	 */
	public WilderMA(EventSystem es, String id, Series<Double> source, int period)
		throws ValueException
	{
		this(es, id, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param source источник данных (исходный ряд)
	 * @param period период скользящей средней
	 * @throws ValueException ошибка перерасчета значений индикатора
	 */
	public WilderMA(EventSystem es, Series<Double> source, int period)
			throws ValueException
	{
		this(es, null, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}

	@Override
	protected String makeId(String id) {
		return id == null ? "WilderMA(" + period + ")" : id;
	}

	@Override
	protected Double calculate(int index) throws ValueException {
		Double value = source.get(index);
		if ( index == 0 || value == null ) {
			return value;
		}
		Double MAp = series.get(index - 1);
		if ( MAp == null ) {
			return value;
		}
		return (MAp * (period - 1) + value) / period;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != WilderMA.class ) {
			return false;
		}
		return fieldsEquals((_MA) other);
	}

}
