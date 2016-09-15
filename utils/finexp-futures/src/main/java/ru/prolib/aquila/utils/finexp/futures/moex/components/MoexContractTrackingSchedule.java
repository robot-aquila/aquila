package ru.prolib.aquila.utils.finexp.futures.moex.components;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import ru.prolib.aquila.core.utils.LocalTimePeriod;

public class MoexContractTrackingSchedule {
	private static final ZoneId ZONE;
	private static final LocalTime OPENING,
		CLEARING1_START,
		CLEARING1_END,
		CLEARING2_START,
		CLEARING2_END;
	private static final LocalTimePeriod TRACKING_PERIOD1,
		TRACKING_PERIOD2,
		TRACKING_PERIOD3,
		NEXT_IS_TRACKING_PERIOD1,
		NEXT_IS_TRACKING_PERIOD2,
		NEXT_IS_TRACKING_PERIOD3;

	static {
		ZONE = ZoneId.of("Europe/Moscow");
		OPENING = LocalTime.of(10, 0);
		CLEARING1_START = LocalTime.of(14, 0);
		CLEARING1_END = LocalTime.of(14,  5);
		CLEARING2_START = LocalTime.of(18, 45);
		CLEARING2_END = LocalTime.of(19, 0);
		TRACKING_PERIOD1 = new LocalTimePeriod(OPENING.plusMinutes(30), OPENING.plusMinutes(90), ZONE);
		TRACKING_PERIOD2 = new LocalTimePeriod(CLEARING1_END.plusMinutes(30), CLEARING1_END.plusMinutes(90), ZONE);
		TRACKING_PERIOD3 = new LocalTimePeriod(CLEARING2_END.plusMinutes(30), CLEARING2_END.plusMinutes(90), ZONE);
		NEXT_IS_TRACKING_PERIOD1 = new LocalTimePeriod(LocalTime.MIN, TRACKING_PERIOD1.from(), ZONE);
		NEXT_IS_TRACKING_PERIOD2 = new LocalTimePeriod(TRACKING_PERIOD1.from(), TRACKING_PERIOD2.from(), ZONE);
		NEXT_IS_TRACKING_PERIOD3 = new LocalTimePeriod(TRACKING_PERIOD2.from(), TRACKING_PERIOD3.from(), ZONE);
	}
	
	public boolean isTrackingPeriod(Instant time) {
		return isMarketOpeningTrackingPeriod(time)
			|| isIntradayClearingTrackingPeriod(time)
			|| isEveningClearingTrackingPeriod(time);
	}
	
	public boolean isMarketOpeningTrackingPeriod(Instant time) {
		return TRACKING_PERIOD1.contains(time);
	}
	
	public boolean isIntradayClearingTrackingPeriod(Instant time) {
		return TRACKING_PERIOD2.contains(time);
	}
	
	public boolean isEveningClearingTrackingPeriod(Instant time) {
		return TRACKING_PERIOD3.contains(time);
	}
	
	public Instant withMarketOpeningTime(Instant time) {
		return toZDT(time).with(OPENING).toInstant();
	}
	
	public Instant withIntradayClearingTime(Instant time) {
		return toZDT(time).with(CLEARING1_START).toInstant();
	}
	
	public Instant withEveningClearingTime(Instant time) {
		return toZDT(time).with(CLEARING2_START).toInstant();
	}
	
	public Instant getNextTrackingPeriodStart(Instant time) {
		if ( NEXT_IS_TRACKING_PERIOD1.contains(time) ) {
			return toZDT(time).with(TRACKING_PERIOD1.from()).toInstant();
		} else if ( NEXT_IS_TRACKING_PERIOD2.contains(time) ) {
			return toZDT(time).with(TRACKING_PERIOD2.from()).toInstant();
		} else if ( NEXT_IS_TRACKING_PERIOD3.contains(time) ) {
			return toZDT(time).with(TRACKING_PERIOD3.from()).toInstant();
		} else {
			return toZDT(time).plusDays(1).with(TRACKING_PERIOD1.from()).toInstant();
		}
	}
	
	public Instant getNextUpdateTime(Instant time) {
		return time.plusSeconds(60);
	}
	
	public ZonedDateTime toZDT(Instant time) {
		return ZonedDateTime.ofInstant(time, ZONE);
	}
	
	public LocalTime toZT(Instant time) {
		return toZDT(time).toLocalTime();
	}

}
