package ru.prolib.aquila.core.timetable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Внутридневной период по границе минуты часа.
 */
@XStreamAlias("HourMinutePeriod")
public class HMPeriod implements TimePeriod {
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
	public boolean contains(DateTime time) {
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
	public DateTime nextStartTime(DateTime time) {
		DateTime aligned = from.align(time);
		return aligned.compareTo(time) <= 0 ? null : aligned;
	}

	@Override
	public DateTime nextEndTime(DateTime time) {
		DateTime aligned = to.align(time);
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

}
