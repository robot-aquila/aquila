package ru.prolib.aquila.core.data.timeframe;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.threeten.extra.Interval;

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
	public Interval getInterval(Instant timestamp) {
		LocalDateTime time = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC);
		int segmentIndex = (time.getDayOfYear() - 1) / length;
		int firstDayOffset = segmentIndex * length; 
		LocalDateTime firstDay = LocalDateTime.of(time.getYear(), 1, 1, 0, 0, 0, 0);
		return Interval.of(firstDay.plusDays(firstDayOffset).toInstant(ZoneOffset.UTC),
				firstDay.plusDays(firstDayOffset + length).toInstant(ZoneOffset.UTC));
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
