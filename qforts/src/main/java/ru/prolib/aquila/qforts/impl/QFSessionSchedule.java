package ru.prolib.aquila.qforts.impl;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.prolib.aquila.core.utils.LocalTimePeriod;

public class QFSessionSchedule {
	/**
	 * 00:00-10:00
	 */
	public static final int VPZ = 0;
	/**
	 * 10:00-14:00
	 */
	public static final int PCVM1_1 = 1;
	/**
	 * 14:00-14:04
	 */
	public static final int VP1 = 2;
	/**
	 * 14:04-14:05
	 */
	public static final int PIC = 3;
	/**
	 * 14:05-18:45
	 */
	public static final int PCVM2 = 4;
	/**
	 * 18:45-18:59
	 */
	public static final int VP2 = 5;
	/**
	 * 18:59-19:00
	 */
	public static final int PMC = 6;
	/**
	 * 19:00-23:50
	 */
	public static final int PCVM1_2 = 7;
	/**
	 * 23:50-00:00
	 */
	public static final int VP3 = 8;
	
	private static final ZoneId ZONE;
	private static final Map<LocalTimePeriod, Integer> PERIOD_MAP;
	private static final Map<Integer, QFSessionProc> PROC_MAP;
	
	static {
		ZONE = ZoneId.of("Europe/Moscow");
		PERIOD_MAP = new HashMap<>();
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of( 0,  0), LocalTime.of(10,  0), ZONE), VPZ);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(10,  0), LocalTime.of(14,  0), ZONE), PCVM1_1);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(14,  0), LocalTime.of(14,  4), ZONE), VP1);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(14,  4), LocalTime.of(14,  5), ZONE), PIC);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(14,  5), LocalTime.of(18, 45), ZONE), PCVM2);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(18, 45), LocalTime.of(18, 59), ZONE), VP2);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(18, 59), LocalTime.of(19,  0), ZONE), PMC);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(19,  0), LocalTime.of(23, 50), ZONE), PCVM1_2);
		PERIOD_MAP.put(new LocalTimePeriod(LocalTime.of(23, 50), LocalTime.of( 0,  0), ZONE), VP3);
		PROC_MAP = new HashMap<>();
		PROC_MAP.put(PCVM1_1,	QFSessionProc.UPDATE_BY_MARKET);
		PROC_MAP.put(PIC,		QFSessionProc.MID_CLEARING);
		PROC_MAP.put(PCVM2,		QFSessionProc.UPDATE_BY_MARKET);
		PROC_MAP.put(PMC,		QFSessionProc.CLEARING);
		PROC_MAP.put(PCVM1_2,	QFSessionProc.UPDATE_BY_MARKET);
	}
	
	/**
	 * Get ID of current period.
	 * <p>
	 * @param currentTime - time to detect period
	 * @return ID of current period (see class constants)
	 */
	public int getCurrentPeriod(Instant currentTime) {
		Iterator<Map.Entry<LocalTimePeriod, Integer>> it = PERIOD_MAP.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<LocalTimePeriod, Integer> entry = it.next();
			if( entry.getKey().contains(currentTime) ) {
				return entry.getValue();
			}
		}
		throw new IllegalStateException();
	}
	
	public QFSessionProc getCurrentProc(Instant currentTime) {
		return PROC_MAP.get(getCurrentPeriod(currentTime));
	}
	
	public Instant getNextRunTime(Instant currentTime) {
		int cp = getCurrentPeriod(currentTime);
		Instant nextTime = null;
		switch ( cp ) {
		case VPZ:
			return toZDT(currentTime, LocalTime.of(10,  0)).toInstant();
		case PCVM1_1:
		case PCVM1_2:
		case PCVM2:
			nextTime = currentTime.plusSeconds(5);
			return getCurrentPeriod(nextTime) == cp ? nextTime : getNextRunTime(nextTime);
		case VP1:
			return toZDT(currentTime, LocalTime.of(14,  4)).toInstant();
		case PIC:
			return toZDT(currentTime, LocalTime.of(14,  5)).toInstant();
		case VP2:
			return toZDT(currentTime, LocalTime.of(18, 59)).toInstant();
		case PMC:
			return toZDT(currentTime, LocalTime.of(19,  0)).toInstant();
		case VP3:
			return toZDT(currentTime, LocalTime.of(10,  0)).plusDays(1).toInstant();
		}
		throw new IllegalStateException();
	}
	
	private ZonedDateTime toZDT(Instant time, LocalTime timeWith) {
		return toZDT(time).with(timeWith);
	}
	
	private ZonedDateTime toZDT(Instant time) {
		return ZonedDateTime.ofInstant(time, ZONE);
	}

}
