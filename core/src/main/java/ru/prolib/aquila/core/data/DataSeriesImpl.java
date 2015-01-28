package ru.prolib.aquila.core.data;

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
	 */
	public DataSeriesImpl() {
		super();
	}
	
	/**
	 * Создать ряд данных.
	 * <p>
	 * Реальное ограничение длины не используется.
	 * <p>
	 * @param valueId идентификатор ряда
	 */
	public DataSeriesImpl(String valueId) {
		super(valueId);
	}
	
	/**
	 * Создать ряд данных.
	 * <p>
	 * @param valueId идентификатор ряда
	 * @param lengthLimit реальное ограничение длины. Если меньше нуля, то
	 * используется {@link #STORAGE_NOT_LIMITED}
	 */
	public DataSeriesImpl(String valueId, int lengthLimit) {
		super(valueId, lengthLimit);
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
