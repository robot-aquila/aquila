package ru.prolib.aquila.core.timetable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Внутринедельный период.
 */
@XStreamAlias("DayOfWeekPeriod")
public class DOWPeriod implements DatePeriod {
	private final DOWSpan from, to;
	
	public DOWPeriod(DOWSpan from, DOWSpan to) {
		super();
		if ( to.getDayOfWeek().getNumber() < from.getDayOfWeek().getNumber() ) {
			throw new IllegalArgumentException("Wrong period: " + from +"-"+ to);
		}
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param from день недели начала периода
	 * @param to день недели окончания периода
	 */
	public DOWPeriod(DOW from, DOW to) {
		this(new DOWSpan(from), new DOWSpan(to));
	}
	
	/**
	 * Получить день начала периода.
	 * <p>
	 * @return день начала периода
	 */
	public DOWSpan getPeriodFrom() {
		return from;
	}
	
	/**
	 * Получить день окончания периода.
	 * <p>
	 * @return день окончания периода
	 */
	public DOWSpan getPeriodTo() {
		return to;
	}

	@Override
	public boolean contains(DateTime time) {
		return from.lessOrEquals(time) && to.greaterOrEquals(time);
	}
	
	@Override
	public DateTime nextDate(DateTime time) {
		time = time.plusDays(1);
		if ( contains(time) ) {
			return time.withTimeAtStartOfDay();
		}
		DateTime aligned = from.align(time);
		if ( aligned.isBefore(time) ) {
			aligned = aligned.plusWeeks(1);
		}
		return aligned.withTimeAtStartOfDay();
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DOWPeriod.class ) {
			return false;
		}
		DOWPeriod o = (DOWPeriod) other;
		return new EqualsBuilder()
			.append(o.from, from)
			.append(o.to, to)
			.isEquals();
	}
	
	@Override
	public boolean isEndDate(DateTime time) {
		return to.lessOrEquals(time) && to.greaterOrEquals(time);
	}
	
	@Override
	public DateTime nextEndDate(DateTime time) {
		if ( time.getDayOfWeek() == to.getDayOfWeek().getNumber() ) {
			return time;
		}
		return to.align(time).withTimeAtStartOfDay();
	}

}
