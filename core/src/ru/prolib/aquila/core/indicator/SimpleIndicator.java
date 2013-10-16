package ru.prolib.aquila.core.indicator;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Заготовка индикатора.
 * <p>
 * Предназначен для использования в комплексе с функцией расчета типа
 * {@link SimpleFunction}.
 * <p>
 * @param <T> - тип значений источника
 * @param <R> - тип результирующих значений
 */
public class SimpleIndicator<T, R> extends CommonIndicator<R>
	implements EventListener
{
	protected final SimpleFunction<T, R> fn;
	protected final Series<T> sourceSeries;
	protected final String id;

	/**
	 * Конструктор.
	 * <p>
	 * @param id идентификатор (может быть null)
	 * @param function функция расчета
	 * @param sourceSeries исходный ряд данных
	 * @param dispatcher диспетчер событий
	 */
	public SimpleIndicator(String id, SimpleFunction<T, R> function,
			Series<T> sourceSeries, IndicatorEventDispatcher dispatcher)
	{
		super(dispatcher);
		this.id = id;
		this.fn = function;
		this.sourceSeries = sourceSeries;
	}
	
	/**
	 * Получить функцию расчета.
	 * <p>
	 * @return функция
	 */
	public SimpleFunction<T, R> getFunction() {
		return fn;
	}
	
	/**
	 * Получить ряд исходных данных.
	 * <p>
	 * @return исходный ряд
	 */
	public Series<T> getSourceSeries() {
		return sourceSeries;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public IndicatorEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public String getId() {
		return id == null ? fn.getDefaultId() : id;
	}

	@Override
	public R get() throws ValueException {
		return fn.calculate(sourceSeries, sourceSeries.getLength() - 1);
	}

	@Override
	public R get(int index) throws ValueException {
		return fn.calculate(sourceSeries, index);
	}

	@Override
	public int getLength() {
		return sourceSeries.getLength();
	}

	@Override
	protected void onStart() throws StarterException {
		sourceSeries.OnAdded().addListener(this);
		sourceSeries.OnUpdated().addListener(this);
	}

	@Override
	protected void onStop() throws StarterException {
		sourceSeries.OnAdded().removeListener(this);
		sourceSeries.OnUpdated().removeListener(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized void onEvent(Event event) {
		int index;
		try {
			if ( event.isType(sourceSeries.OnAdded()) ) {
				index = ((ValueEvent) event).getValueIndex(); 
				dispatcher.fireAdded(fn.calculate(sourceSeries, index), index);
			} else if ( event.isType(sourceSeries.OnUpdated()) ) {
				index = ((ValueEvent) event).getValueIndex();
				dispatcher.fireUpdated(fn.calculate(sourceSeries, index), index);
			}
		} catch ( ValueException e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	/**
	 * Сравнить атрибуты объектов.
	 * <p>
	 * @param other экземпляр для сравнения
	 * @return true атрибуты равны, false не равны
	 */
	protected synchronized boolean fieldsEquals(SimpleIndicator<T, R> other) {
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(other.fn, fn)
			.append(other.id, id)
			.append(other.sourceSeries, sourceSeries)
			.isEquals();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SimpleIndicator.class ) {
			return false;
		}
		return fieldsEquals((SimpleIndicator<T, R>) other);
	}

}
