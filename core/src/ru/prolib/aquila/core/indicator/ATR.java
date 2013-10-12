package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.*;

/**
 * ATR - Average True Range.
 */
public class ATR extends WilderMA {

	/**
	 * Конструктор.
	 * <p>
	 * @param id символьный идентификатор
	 * @param candles исходная последовательность свечей
	 * @param period период усреднения
	 * @param limit лимит хранилища
	 * @throws ValueException ошибка перерасчета значений индикатора
	 */
	public ATR(String id, CandleSeries candles, int period, int limit)
		throws ValueException
	{
		super(id, new TR(candles), period, limit);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id символьный идентификатор
	 * @param candles исходная последовательность свечей
	 * @param period период усреднения
	 * @throws ValueException ошибка перерасчета значений индикатора
	 */
	public ATR(String id, CandleSeries candles, int period)
		throws ValueException
	{
		this(id, candles, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param candles исходная последовательность свечей
	 * @param period период усреднения
	 * @throws ValueException ошибка перерасчета значений индикатора
	 */
	public ATR(CandleSeries candles, int period) throws ValueException {
		this(null, candles, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	@Override
	protected String makeId(String id) {
		return id == null ? "ATR(" + period + ")" : id;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ATR.class ) {
			return false;
		}
		return fieldsEquals((_MA) other);
	}

}
