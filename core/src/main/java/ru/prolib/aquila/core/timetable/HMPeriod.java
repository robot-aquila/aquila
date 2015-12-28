package ru.prolib.aquila.core.timetable;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Внутридневной период по границам минуты часа.
 */
@XStreamAlias("HourMinutePeriod")
@XStreamConverter(PeriodConverter.class)
public class HMPeriod implements TimePeriod {
	private static final DecimalFormat f = new DecimalFormat("##");
	private final HMSpan from, to;
	
	public HMPeriod(HMSpan from, HMSpan to) {
		super();
		if ( from.toMinutes() >= to.toMinutes() ) {
			throw new IllegalArgumentException("Wrong period: "+ from +"-"+ to);
		}
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Получить время начало периода.
	 * <p>
	 * @return время начала периода
	 */
	public HMSpan getPeriodFrom() {
		return from;
	}
	
	/**
	 * Получить время окончания периода.
	 * <p>
	 * @return время окончания периода
	 */
	public HMSpan getPeriodTo() {
		return to;
	}
	
	/**
	 * Проверить наличие перекрытия с другим периодом.
	 * <p>
	 * @param o другой период
	 * @return true - периоды имеют общую область (перекрываются)
	 */
	public boolean overlap(HMPeriod o) {
		// Имеются 4 временные точки, образованные границами двух периодов.
		// Вхождение в период границы другого периода означает, что существуют
		// общие точки, а это и есть пересечение.
		return contains(o.from) || contains(new HMSpan(o.to.toMinutes() - 1))
			|| o.contains(from) || o.contains(new HMSpan(to.toMinutes() - 1));
	}

	@Override
	public boolean contains(LocalDateTime time) {
		return from.lessOrEquals(time) && to.greater(time);
	}
	
	/**
	 * Проверить вхождение в период.
	 * <p>
	 * @param span внутридневная временная метка
	 * @return true, если метка принадлежит установленному периоду
	 */
	public boolean contains(HMSpan span) {
		int m = span.toMinutes();
		return m >= from.toMinutes() && m < to.toMinutes();
	}

	@Override
	public LocalDateTime nextStartTime(LocalDateTime time) {
		LocalDateTime aligned = from.align(time);
		return aligned.compareTo(time) <= 0 ? null : aligned;
	}

	@Override
	public LocalDateTime nextEndTime(LocalDateTime time) {
		LocalDateTime aligned = to.align(time);
		return aligned.compareTo(time) <= 0 ? null : aligned;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != HMPeriod.class ) {
			return false;
		}
		HMPeriod o = (HMPeriod) other;
		return new EqualsBuilder()
			.append(o.from, from)
			.append(o.to, to)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return from.toString() + "-" + to.toString();
	}
	
	/**
	 * Разобрать строку периода.
	 * <p>
	 * @param s строка периода в формате HH:MM-HH:MM
	 * @return период
	 */
	public static HMPeriod parse(String s) {
		String spans[] = StringUtils.split(s, "-", 2);
		try {
			return new HMPeriod(parseSpan(spans[0]), parseSpan(spans[1]));
		} catch ( Exception e ) {
			throw new IllegalArgumentException("Bad period specification: "
					+ s, e);
		}
	}
	
	private static HMSpan parseSpan(String s) throws ParseException {
		String chunks[] = StringUtils.split(s, ":", 2);
		if ( chunks.length == 2 ) {
			return new HMSpan(f.parse(chunks[0]).intValue(),
					f.parse(chunks[1]).intValue());
		}
		throw new IllegalArgumentException("Bad span spec: " + s);
	}

}
