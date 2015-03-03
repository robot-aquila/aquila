package ru.prolib.aquila.core.data;

import org.joda.time.Interval;

import ru.prolib.aquila.core.EventSystem;

/**
 * Реализация ряда временных интервалов.
 * <p>
 * 2013-03-11<br>
 * $Id: TimeSeriesImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class IntervalSeriesImpl extends SeriesImpl<Interval>
		implements EditableIntervalSeries
{
	
	public IntervalSeriesImpl(EventSystem es) {
		super(es);
	}
	
	public IntervalSeriesImpl(EventSystem es, String valueId) {
		super(es, valueId);
	}
	
	public IntervalSeriesImpl(EventSystem es, String valueId, int lengthLimit) {
		super(es, valueId, lengthLimit);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == IntervalSeriesImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}

}
