package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.*;

/**
 * Индикатор Moving Average.
 */
public class MA extends ComplexIndicator<Double, Double> {
	
	/**
	 * Конструктор (полный).
	 * <p>
	 * @param id идентификатор (может быть null)
	 * @param function функция расчета
	 * @param sourceSeries исходный ряд данных
	 * @param ownSeries собственный ряд данных
	 * @param dispatcher диспетчер событий
	 */
	public MA(String id, MAFunction function, Series<Double> sourceSeries,
			EditableSeries<Double> ownSeries, IndicatorEventDispatcher dispatcher)
	{
		super(id, function, sourceSeries, ownSeries, dispatcher);
	}

	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param id идентификатор (может быть null)
	 * @param function функция расчета
	 * @param sourceSeries исходный ряд данных
	 */
	public MA(EventSystem es, String id, MAFunction function,
			Series<Double> sourceSeries)
	{
		this(id, function, sourceSeries, new SeriesImpl<Double>(es),
			new IndicatorEventDispatcher(es));
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param function функция расчета
	 * @param sourceSeries исходный ряд данных
	 */
	public MA(EventSystem es, MAFunction function, Series<Double> sourceSeries) {
		this(es, null, function, sourceSeries);
	}
	
	@Override
	public synchronized String getId() {
		return id == null ? ((MAFunction) fn).getDefaultId() : id;
	}
	
	/**
	 * Установить период скользящей средней.
	 * <p>
	 * Изменить период можно только для индикатора в нерабочем состоянии.
	 * То есть, сначала нужно остановить индикатор методом {@link #stop()}.
	 * <p>
	 * @param period период
	 * @throws IllegalStateException индикатор в работе
	 */
	public synchronized void setPeriod(int period) {
		if ( started() ) {
			throw new IllegalStateException("Indicator " + getId()
					+ " already started");
		}
		((MAFunction) fn).setPeriod(period);
	}
	
	/**
	 * Получить период скользящей средней.
	 * <p>
	 * @return период скользящей средней
	 */
	public synchronized int getPeriod() {
		return ((MAFunction) fn).getPeriod();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MA.class ) {
			return false;
		}
		return fieldsEquals((MA) other);
	}

}
