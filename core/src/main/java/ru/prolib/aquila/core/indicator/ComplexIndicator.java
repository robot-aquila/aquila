package ru.prolib.aquila.core.indicator;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Заготовка индикатора.
 * <p>
 * Предназначен для использования в комплексе с функцией расчета типа
 * {@link ComplexFunction}.
 * <p>
 * @param <T> - тип значений источника
 * @param <R> - тип результирующих значений
 */
public class ComplexIndicator<T, R> extends CommonIndicator<R>
	implements EventListener
{
	protected final ComplexFunction<T, R> fn;
	protected final Series<T> sourceSeries;
	protected final EditableSeries<R> ownSeries;
	protected final String id;

	/**
	 * Конструктор.
	 * <p>
	 * @param id символьный идентификатор (может быть null)
	 * @param function функция расчета значения
	 * @param sourceSeries исходный ряд данных
	 * @param ownSeries результирующий ряд данных
	 * @param dispatcher диспетчер событий
	 */
	public ComplexIndicator(String id, ComplexFunction<T, R> function,
			Series<T> sourceSeries,
			EditableSeries<R> ownSeries,
			IndicatorEventDispatcher dispatcher)
	{
		super(dispatcher);
		this.id = id;
		this.fn = function;
		this.sourceSeries = sourceSeries;
		this.ownSeries = ownSeries;
	}
	
	/**
	 * Получить функцию расчета.
	 * <p>
	 * @return функция
	 */
	public ComplexFunction<T, R> getFunction() {
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
	 * Получить результирующий (собственный) ряд.
	 * <p>
	 * @return ряд данных
	 */
	public EditableSeries<R> getOwnSeries() {
		return ownSeries;
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
		return ownSeries.get();
	}

	@Override
	public R get(int index) throws ValueException {
		return ownSeries.get(index);
	}

	@Override
	public int getLength() {
		return ownSeries.getLength();
	}

	@Override
	protected void onStart() throws StarterException {
		ownSeries.clear();
		synchronized ( sourceSeries ) {
			int count = sourceSeries.getLength();
			for ( int i = 0; i < count; i ++ ) {
				try {
					ownSeries.add(fn.calculate(sourceSeries, ownSeries, i));
				} catch ( ValueException e ) {
					ownSeries.clear();
					throw new StarterException("Recalculation failed", e);
				}
			}
			dispatcher.startRelayFor(ownSeries);
			sourceSeries.OnAdded().addSyncListener(this);
			sourceSeries.OnUpdated().addSyncListener(this);
		}
	}

	@Override
	protected void onStop() throws StarterException {
		synchronized ( sourceSeries ) {
			sourceSeries.OnAdded().removeListener(this);
			sourceSeries.OnUpdated().removeListener(this);
		}
		dispatcher.stopRelay();
	}

	/**
	 * Сравнить атрибуты объектов.
	 * <p>
	 * @param other экземпляр для сравнения
	 * @return true атрибуты равны, false не равны
	 */
	protected synchronized boolean fieldsEquals(ComplexIndicator<T, R> other) {
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(other.fn, fn)
			.append(other.id, id)
			.append(other.ownSeries, ownSeries)
			.append(other.sourceSeries, sourceSeries)
			.isEquals();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized void onEvent(Event event) {
		try {
			if ( event.isType(sourceSeries.OnAdded()) ) {
				ownSeries.add(fn.calculate(sourceSeries, ownSeries,
					((ValueEvent) event).getValueIndex()));
			} else if ( event.isType(sourceSeries.OnUpdated()) ) {
				ownSeries.set(fn.calculate(sourceSeries, ownSeries,
					((ValueEvent) event).getValueIndex()));
			}
		} catch ( ValueException e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ComplexIndicator.class ) {
			return false;
		}
		return fieldsEquals((ComplexIndicator<T, R>) other);
	}

}
