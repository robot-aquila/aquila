package ru.prolib.aquila.core.data;

import java.util.Date;

/**
 * Реализация ряда временных меток.
 * <p>
 * 2013-03-11<br>
 * $Id: TimeSeriesImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class TimeSeriesImpl extends SeriesImpl<Date>
		implements EditableTimeSeries
{
	
	public TimeSeriesImpl() {
		super();
	}
	
	public TimeSeriesImpl(String valueId) {
		super(valueId);
	}
	
	public TimeSeriesImpl(String valueId, int lengthLimit) {
		super(valueId, lengthLimit);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == TimeSeriesImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}

}
