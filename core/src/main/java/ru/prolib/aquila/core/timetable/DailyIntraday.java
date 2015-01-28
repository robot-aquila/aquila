package ru.prolib.aquila.core.timetable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Суточное расписание.
 * <p>
 * Позволяет создавать суточное расписание по определенным датам. Определяется
 * двумя элементами: периодом дат и периодом времени. Таким образом можно
 * составить любое внутридневное расписание и ассоциировать его с определенными
 * днями. Например, расписание работы по рабочим дням, по четным дням месяца или
 * недели, по выходным и т.п.
 */
@XStreamAlias("DailyIntraday")
public class DailyIntraday implements TimePeriod {
	private final DatePeriod datePeriod;
	private final TimePeriod timePeriod;
	
	public DailyIntraday(DatePeriod datePeriod, TimePeriod timePeriod) {
		super();
		this.datePeriod = datePeriod;
		this.timePeriod = timePeriod;
	}
	
	/**
	 * Получить расписание по датам.
	 * <p>
	 * @return период дат
	 */
	public DatePeriod getDatePeriod() {
		return datePeriod;
	}
	
	/**
	 * Получить внутридневное расписание.
	 * <p>
	 * @return период внутридневного времени
	 */
	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	@Override
	public boolean contains(DateTime time) {
		return datePeriod.contains(time) && timePeriod.contains(time);
	}

	@Override
	public DateTime nextStartTime(DateTime time) {
		if ( ! datePeriod.contains(time) ) {
			time = datePeriod.nextDate(time);
			if ( time == null ) {
				return null; // конец расписания по дням
			}
			// Здесь мы в начале рабочего дня. Если внутридневное расписание
			// вернет null, то это ошибка. Несогласованность периодов разного
			// порядка не входит в компетенцию данного класса. 
			return timePeriod.nextStartTime(time);
		}
		// Здесь мы внутри рабочего дня. Но пока еще неизвестно, находится ли
		// время в границах внутридневного расписания. Если оно лежит за
		// границей завершения периода, то попытка получить время входа даст
		// нулевой результат. В этом случае нужно попытаться получить
		// время для следующего дня.
		DateTime next = timePeriod.nextStartTime(time);
		if ( next == null ) {
			next = datePeriod.nextDate(time);
			return next == null ? null : timePeriod.nextStartTime(next);
		} else {
			return next;
		}
	}

	@Override
	public DateTime nextEndTime(DateTime time) {
		if ( ! datePeriod.contains(time)
			|| timePeriod.nextEndTime(time) == null )
		{
			time = datePeriod.nextDate(time);
			if ( time == null ) {
				return null;
			}
		}
		return timePeriod.nextEndTime(time);		
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DailyIntraday.class ) {
			return false;
		}
		DailyIntraday o = (DailyIntraday) other;
		return new EqualsBuilder()
			.append(o.datePeriod, datePeriod)
			.append(o.timePeriod, timePeriod)
			.isEquals();
	}

}
