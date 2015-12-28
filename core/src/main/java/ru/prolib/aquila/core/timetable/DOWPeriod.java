package ru.prolib.aquila.core.timetable;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Внутринедельный период.
 */
@XStreamAlias("DayOfWeekPeriod")
@XStreamConverter(PeriodConverter.class)
public class DOWPeriod implements DatePeriod {
	private final DOWSpan from, to;
	
	/**
	 * Разобрать строку периода.
	 * <p>
	 * @param s строка периода в формате A-B, где A и B строковое представление
	 * констант {@link DOW}.
	 * @return период
	 */
	static DOWPeriod parse(String s) {
		String chunks[] = StringUtils.split(s, "-", 2);
		if ( chunks.length == 2 ) {
			return new DOWPeriod(DOW.valueOf(chunks[0]),DOW.valueOf(chunks[1]));
		} else if ( chunks.length == 1 ) {
			DOW dow = DOW.valueOf(chunks[0]);
			return new DOWPeriod(dow, dow);
		}
		throw new IllegalArgumentException("Bad period specification: " + s);
	}
	
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
	public boolean contains(LocalDateTime time) {
		return from.lessOrEquals(time) && to.greaterOrEquals(time);
	}
	
	@Override
	public LocalDateTime nextDate(LocalDateTime time) {
		time = time.plusDays(1);
		if ( contains(time) ) {
			return time.toLocalDate().atStartOfDay();
		}
		LocalDateTime aligned = from.align(time);
		if ( aligned.isBefore(time) ) {
			aligned = aligned.plusWeeks(1);
		}
		return aligned.toLocalDate().atStartOfDay();
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
	public boolean isEndDate(LocalDateTime time) {
		return to.lessOrEquals(time) && to.greaterOrEquals(time);
	}
	
	@Override
	public LocalDateTime nextEndDate(LocalDateTime time) {
		if ( time.getDayOfWeek().getValue() == to.getDayOfWeek().getNumber() ) {
			return time;
		}
		return to.align(time).toLocalDate().atStartOfDay();
	}
	
	/**
	 * Проверить вхождение в период.
	 * <p>
	 * @param span день недели
	 * @return true, если день внутри диапазона
	 */
	public boolean contains(DOWSpan span) {
		int n = span.getDayOfWeek().getNumber();
		return n >= from.getDayOfWeek().getNumber()
			&& n <= to.getDayOfWeek().getNumber();
	}
	
	/**
	 * Проверить наличие перекрытия с другим периодом.
	 * <p>
	 * @param o другой период
	 * @return true - периоды имеют общую область (перекрываются)
	 */
	public boolean overlap(DOWPeriod o) {
		return contains(o.from) || contains(o.to)
			|| o.contains(from) || o.contains(to);
	}
	
	@Override
	public String toString() {
		return from.toString() + (to.equals(from) ? "" : "-" + to.toString());
	}

}
