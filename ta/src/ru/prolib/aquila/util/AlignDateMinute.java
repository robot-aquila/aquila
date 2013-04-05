package ru.prolib.aquila.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Выравнивает дату согласно указанному периоду в минутах.
 * Например, для периода 5 минут выравненная дата будет указывать на начало
 * ближайшей (наиболее ранней) пятиминутки.
 */
public class AlignDateMinute implements AlignDate {
	private final int period;
	
	public AlignDateMinute(int periodMinutes) {
		super();
		period = periodMinutes;
	}
	
	public int getPeriod() {
		return period;
	}

	@Override
	public Date align(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int minutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60
			+ calendar.get(Calendar.MINUTE)) / period * period; 
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, minutes % 60);
		calendar.set(Calendar.HOUR_OF_DAY, minutes / 60);
		return calendar.getTime();
	}

}
