package ru.prolib.aquila.core.data.timeframe;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import ru.prolib.aquila.core.BusinessEntities.TimeUnit;
import ru.prolib.aquila.core.data.TimeFrame;

/**
 * Daily timeframe.
 */
public class TFDays implements TimeFrame {
	private final int length;
	
	public TFDays(int length) {
		super();
		this.length = length;
	}

	@Override
	public Interval getInterval(DateTime time) {
		int segmentIndex = (time.getDayOfYear() - 1) / length;
		int firstDayOffset = segmentIndex * length; 
		DateTime firstDay = new DateTime(time.getYear(), 1, 1, 0, 0, 0, 0);
		return new Interval(firstDay.plusDays(firstDayOffset),
				firstDay.plusDays(firstDayOffset + length));
	}

	@Override
	public boolean isIntraday() {
		return false;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.DAY;
	}

	@Override
	public int getLength() {
		return length;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TFDays.class ) {
			return false;
		}
		TFDays o = (TFDays) other;
		return o.length == length;
	}

}
