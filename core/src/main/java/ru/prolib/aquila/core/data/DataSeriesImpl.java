package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventSystem;

/**
 * Реализация ряда данных.
 * <p>
 * 2013-03-11<br>
 * $Id: DataSeriesImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class DataSeriesImpl extends SeriesImpl<Double>
		implements EditableDataSeries
{
	
	/**
	 * Создать ряд данных.
	 * <p>
	 * В качестве идентификатора значения используется {@link Series#DEFAULT_ID}.
	 * Реальное ограничение длины не используется.
	 * <p>
	 * @param es фасад системы событий
	 */
	public DataSeriesImpl(EventSystem es) {
		super(es);
	}
	
	/**
	 * Создать ряд данных.
	 * <p>
	 * Реальное ограничение длины не используется.
	 * <p>
	 * @param es фасад системы событий
	 * @param valueId идентификатор ряда
	 */
	public DataSeriesImpl(EventSystem es, String valueId) {
		super(es, valueId);
	}
	
	/**
	 * Создать ряд данных.
	 * <p>
	 * @param es фасад системы событий
	 * @param valueId идентификатор ряда
	 * @param lengthLimit реальное ограничение длины. Если меньше нуля, то
	 * используется {@link #STORAGE_NOT_LIMITED}
	 */
	public DataSeriesImpl(EventSystem es, String valueId, int lengthLimit) {
		super(es, valueId, lengthLimit);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == DataSeriesImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}

}
