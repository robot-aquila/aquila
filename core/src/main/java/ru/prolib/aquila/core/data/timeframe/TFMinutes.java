package ru.prolib.aquila.core.data.timeframe;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.threeten.extra.Interval;

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
	public Interval getInterval(Instant timestamp) {
		LocalDateTime time = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC);
		long secondOfDay = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT,
				time.toLocalTime()) / length * length * 60;
		LocalDateTime from = LocalDateTime.of(time.toLocalDate(),
				LocalTime.ofSecondOfDay(secondOfDay));
		LocalDateTime to = from.plusMinutes(length);
		if ( to.toLocalDate().isAfter(from.toLocalDate()) ) {
			// Конец периода указывает на дату следующего дня.
			// В таком случае конец интервала выравнивается по началу след. дня.
			to = LocalDateTime.of(from.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
		}
		return Interval.of(from.toInstant(ZoneOffset.UTC), to.toInstant(ZoneOffset.UTC));
	}

	@Override
	public boolean isIntraday() {
		return true;
	}

	@Override
	public ChronoUnit getUnit() {
		return ChronoUnit.MINUTES;
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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(859, 175).append(length).toHashCode();
	}
	
	@Override
	public String toString() {
		return "M" + length;
	}

}
