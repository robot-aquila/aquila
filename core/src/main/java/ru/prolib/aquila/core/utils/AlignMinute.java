package ru.prolib.aquila.core.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Выравнивает дату согласно указанному периоду в минутах.
 * <p>
 * Например, для периода 5 минут выравненная дата будет указывать на начало
 * ближайшей (наиболее ранней) пятиминутки.
 * <p>
 * 2013-03-02<br>
 * $Id: AlignDateMinute.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class AlignMinute implements AlignTime {
	private final Calendar calendar;
	private final int period;
	
	public AlignMinute(int periodMinutes) {
		super();
		period = periodMinutes;
		calendar = Calendar.getInstance(); 
	}
	
	public int getPeriod() {
		return period;
	}

	@Override
	public synchronized Date align(Date date) {
		calendar.setTime(date);
		int minutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60
			+ calendar.get(Calendar.MINUTE)) / period * period; 
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, minutes % 60);
		calendar.set(Calendar.HOUR_OF_DAY, minutes / 60);
		return calendar.getTime();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AlignMinute.class ) {
			return false;
		}
		AlignMinute o = (AlignMinute) other;
		return o.period == period;
	}

}
