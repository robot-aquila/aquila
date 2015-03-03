package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventSystem;

/**
 * Фабрика значений.
 * <p>
 * Данная реализация позволяет использовать общее ограничение размера хранилища
 * для создаваемых экземпляров. 
 * <p>
 * 2012-04-09<br>
 * $Id: SeriesFactoryImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class SeriesFactoryImpl implements SeriesFactory {
	private final int limit;
	private final EventSystem es;
	
	/**
	 * Конструктор по умолчанию.
	 * <p>
	 * Ограничение длины истории для порождаемых объектов не используется.
	 * <p>
	 * @param es фасад системы событий
	 */
	public SeriesFactoryImpl(EventSystem es) {
		this(es, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад системы событий
	 * @param lengthLimit ограничение длины хранимой истории
	 */
	public SeriesFactoryImpl(EventSystem es, int lengthLimit) {
		super();
		this.es = es;
		this.limit = lengthLimit;
	}
	
	/**
	 * Получить используемое ограничение длины хранимой истории.
	 * <p>
	 * @return лимит истории
	 */
	public int getLengthLimit() {
		return limit;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private EditableSeries createValue(String id) {
		return new SeriesImpl(es, id, limit);
	}
	
	@Override
	public EditableDataSeries createDouble(String id) {
		return new DataSeriesImpl(es, id, limit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EditableSeries<Integer> createInteger(String id) {
		return createValue(id);
	}

	@Override
	public EditableIntervalSeries createInterval(String id) {
		return new IntervalSeriesImpl(es, id, limit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EditableSeries<Boolean> createBoolean(String id) {
		return createValue(id);
	}

	@Override
	public EditableCandleSeries createCandle(Timeframe tf, String id) {
		return new CandleSeriesImpl(es, tf, id, limit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EditableSeries<Long> createLong(String id) {
		return createValue(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EditableSeries<String> createString(String id) {
		return createValue(id);
	}

	@Override
	public EditableSeries<Boolean> createBoolean() {
		return createBoolean(Series.DEFAULT_ID);
	}

	@Override
	public EditableCandleSeries createCandle(Timeframe tf) {
		return createCandle(tf, Series.DEFAULT_ID);
	}

	@Override
	public EditableIntervalSeries createInterval() {
		return createInterval(Series.DEFAULT_ID);
	}

	@Override
	public EditableDataSeries createDouble() {
		return createDouble(Series.DEFAULT_ID);
	}

	@Override
	public EditableSeries<Integer> createInteger() {
		return createInteger(Series.DEFAULT_ID);
	}

	@Override
	public EditableSeries<Long> createLong() {
		return createLong(Series.DEFAULT_ID);
	}

	@Override
	public EditableSeries<String> createString() {
		return createString(Series.DEFAULT_ID);
	}

}
