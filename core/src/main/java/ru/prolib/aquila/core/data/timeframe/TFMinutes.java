package ru.prolib.aquila.core.data.timeframe;

import org.joda.time.*;
import ru.prolib.aquila.core.BusinessEntities.TimeUnit;
import ru.prolib.aquila.core.data.TimeFrame;

/**
 * Минутный таймфрейм.
 */
public class TFMinutes implements TimeFrame {
	private final int length;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param length период интервала в минутах.
	 */
	public TFMinutes(int length) {
		super();
		this.length = length;
	}

	@Override
	public Interval getInterval(DateTime time) {
		DateTime from = time.withMillisOfDay(time.getMinuteOfDay() /
				length * length * 60000);
		DateTime to = from.plusMinutes(length);
		if ( to.toLocalDate().isAfter(from.toLocalDate()) ) {
			// Конец периода указывает на дату следующего дня.
			// В таком случае конец интервала выравнивается по началу след. дня.
			to = from.plusDays(1).withMillisOfDay(0);
		}
		return new Interval(from, to);
	}

	@Override
	public boolean isIntraday() {
		return true;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.MINUTE;
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
		if ( other == null || other.getClass() != TFMinutes.class ) {
			return false;
		}
		TFMinutes o = (TFMinutes) other;
		return o.length == length;
	}

}
