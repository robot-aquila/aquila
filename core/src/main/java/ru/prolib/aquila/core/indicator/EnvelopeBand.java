package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.*;

/**
 * Индикатор, представляющий границу конверта.
 */
public class EnvelopeBand extends SimpleIndicator<Double, Double> {

	/**
	 * Конструктор.
	 * <p>
	 * @param id идентификатор (может быть null)
	 * @param function функция расчета
	 * @param sourceSeries исходный ряд данных
	 */
	public EnvelopeBand(String id, EnvelopeFunction function,
			Series<Double> sourceSeries, IndicatorEventDispatcher dispatcher)
	{
		super(id, function, sourceSeries, dispatcher);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param id идентификатор (может быть null)
	 * @param sourceSeries исходный ряд данных
	 * @param upper тип: true - верхняя, false - нижняя граница конвертов
	 * @param k коэффициент сдвига
	 */
	public EnvelopeBand(String id, Series<Double> sourceSeries,
			boolean upper, double k)
	{
		this(id, new EnvelopeFunction(upper, k), sourceSeries,
				new IndicatorEventDispatcher());
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param sourceSeries исходный ряд данных
	 * @param upper тип: true - верхняя, false - нижняя граница конвертов
	 * @param k коэффициент сдвига
	 */
	public EnvelopeBand(Series<Double> sourceSeries, boolean upper, double k) {
		this(null, sourceSeries, upper, k);
	}

	/**
	 * Установить коэффициент сдвига.
	 * <p>
	 * @param k коэффициент сдвига
	 * @throws IllegalStateException индикатор в работе
	 */
	public synchronized void setOffset(double k) {
		if ( started() ) {
			throw new IllegalStateException("Indicator " + getId()
					+ " already started");
		}
		((EnvelopeFunction) fn).setOffset(k);
	}
	
	/**
	 * Получить коэффициент сдвига.
	 * <p>
	 * @return коэффициент сдвига
	 */
	public synchronized double getOffset() {
		return ((EnvelopeFunction) fn).getOffset();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EnvelopeBand.class ) {
			return false;
		}
		return fieldsEquals((EnvelopeBand) other);
	}

}
