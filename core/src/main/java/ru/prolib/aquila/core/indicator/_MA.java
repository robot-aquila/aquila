package ru.prolib.aquila.core.indicator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Абстрактная заготовка под индикатор типа MA.
 * <p>
 * Используется как основа индикаторов с отдельным источником данных и периодом.
 * <p>
 * 2013-03-12<br>
 * $Id: _MA.java 571 2013-03-12 00:53:34Z whirlwind $
 */
@Deprecated
abstract public class _MA implements Series<Double> {
	private static final Logger logger;
	protected final SeriesImpl<Double> series;
	protected final Series<Double> source;
	protected final int period;
	
	static {
		logger = LoggerFactory.getLogger(_MA.class);
	}

	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param id идентификатор
	 * @param source источник данных
	 * @param period период индикатора
	 * @param storageLimit лимит размера хранимых значений 
	 * @throws ValueException исключение перерасчета значений индикатора
	 */
	public _MA(EventSystem es, String id, Series<Double> source, int period,
			int storageLimit) throws ValueException
	{
		super();
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period cannot be less than 2");
		}
		this.source = source;
		this.period = period;
		series = new SeriesImpl<Double>(es, makeId(id), storageLimit);
		init();
	}

	/**
	 * Получить лимит хранилища данных.
	 * <p>
	 * @return лимит
	 */
	public int getStorageLimit() {
		return series.getStorageLimit();
	}

	/**
	 * Получить исходный ряд.
	 * <p>
	 * @return ряд
	 */
	public Series<Double> getSource() {
		return source;
	}

	/**
	 * Получить период скользящей средней.
	 * <p>
	 * @return период
	 */
	public int getPeriod() {
		return period;
	}

	@Override
	public String getId() {
		return series.getId();
	}

	@Override
	public synchronized Double get() throws ValueException {
		return series.get();
	}

	@Override
	public synchronized Double get(int index) throws ValueException {
		return series.get(index);
	}

	@Override
	public synchronized int getLength() {
		return series.getLength();
	}

	@Override
	public EventType OnAdded() {
		return series.OnAdded();
	}

	@Override
	public EventType OnUpdated() {
		return series.OnUpdated();
	}
	
	/**
	 * Инициализация ряда.
	 * <p>
	 * @throws ValueException ошибка перерасчета ряда для существующих значений
	 * ряда-источника
	 */
	private void init() throws ValueException {
		synchronized ( source ) {
			for ( int i = 0; i < source.getLength(); i ++ ) {
				series.add(calculate(i));
			}
			source.OnAdded().addSyncListener(new EventListener() {
				@SuppressWarnings("unchecked")
				@Override public void onEvent(Event event) {
					onSourceValueAdded((ValueEvent<Double>) event);
				}
			});
			source.OnUpdated().addSyncListener(new EventListener() {
				@SuppressWarnings("unchecked")
				@Override public void onEvent(Event event) {
					onSourceValueUpdated((ValueEvent<Double>) event);
				}
			});
		}
	}
	
	private synchronized void onSourceValueAdded(ValueEvent<Double> event) {
		try {
			series.add(calculate(event.getValueIndex()));
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	private synchronized void onSourceValueUpdated(ValueEvent<Double> event) {
		synchronized ( series ) {
			int index = event.getValueIndex();
			int last = source.getLength() - 1;
			for ( ; index <= last; index ++ ) {
				try {
					series.set(calculate(index));
				} catch ( ValueException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		}
	}

	/**
	 * Сформировать идентификатор.
	 * <p>
	 * Данный метод формирует идентификатор если он не задан или модифицирует
	 * указанный идентификатор в зависимости от специфики ряда. 
	 * <p>
	 * @param id идентификатор переданный в конструктор (может быть null)
	 * @return идентификатор
	 */
	abstract protected String makeId(String id);
	
	/**
	 * Расчитать значение.
	 * <p>
	 * @param index индекс исходного элемента ряда (всегда гарантировано &gt;=0)
	 * @return новое значение
	 * @throws ValueException - TODO:
	 */
	abstract protected Double calculate(int index) throws ValueException;
	
	/**
	 * Сравнить базовые атрибуты.
	 * <p>
	 * @param other экземпляр для сравнения
	 * @return результат сравнения
	 */
	protected boolean fieldsEquals(_MA other) {
		return new EqualsBuilder()
			.append(other.period, period)
			.append(other.series, series)
			.append(other.source, source)
			.isEquals();
	}
	
}
