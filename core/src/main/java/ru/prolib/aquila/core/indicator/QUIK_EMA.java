package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.EventSystem;
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
 * @deprecated используй {@link MA} в комплексе с функцией расчета MA.  
 * <p>
 * 2013-03-11<br>
 * $Id: QUIK_EMA.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class QUIK_EMA extends _MA {
	
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
	public QUIK_EMA(EventSystem es, String id, DataSeries source, int period,
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
	public QUIK_EMA(EventSystem es, String id, DataSeries source, int period)
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
	public QUIK_EMA(EventSystem es, DataSeries source, int period)
			throws ValueException
	{
		this(es, null, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
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
