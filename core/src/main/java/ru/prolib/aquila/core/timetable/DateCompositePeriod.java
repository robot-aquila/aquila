package ru.prolib.aquila.core.timetable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Составной период дат.
 */
@XStreamAlias("DateComposite")
public class DateCompositePeriod implements DatePeriod {
	@XStreamImplicit(itemFieldName="period")
	@XStreamConverter(PeriodConverter.class)
	private final List<DatePeriod> periods;

	public DateCompositePeriod() {
		super();
		periods = new Vector<DatePeriod>();
	}

	@Override
	public synchronized boolean contains(LocalDateTime time) {
		for ( DatePeriod p : periods ) {
			if ( p.contains(time) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized LocalDateTime nextDate(LocalDateTime time) {
		List<LocalDateTime> list = new Vector<LocalDateTime>();
		for ( DatePeriod p : periods ) {
			LocalDateTime next = p.nextDate(time);
			if ( next != null ) {
				list.add(next);
			}
		}
		if ( list.size() == 0 ) {
			return null;
		}
		Collections.sort(list);
		return list.get(0);
	}

	@Override
	public synchronized boolean isEndDate(LocalDateTime time) {
		for ( DatePeriod p : periods ) {
			if ( p.isEndDate(time) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized LocalDateTime nextEndDate(LocalDateTime time) {
		List<LocalDateTime> list = new Vector<LocalDateTime>();
		for ( DatePeriod p : periods ) {
			LocalDateTime next = p.nextEndDate(time);
			if ( next != null ) {
				list.add(next);
			}
		}
		if ( list.size() == 0 ) {
			return null;
		}
		Collections.sort(list);
		return list.get(0);
	}
	
	/**
	 * Получить список составных периодов.
	 * <p>
	 * @return список периодов
	 */
	public synchronized List<DatePeriod> getPeriods() {
		return new Vector<DatePeriod>(periods);
	}
	
	/**
	 * Добавить период.
	 * <p>
	 * @param period период
	 */
	public synchronized void add(DatePeriod period) {
		periods.add(period);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DateCompositePeriod.class ) {
			return false;
		}
		DateCompositePeriod o = (DateCompositePeriod) other;
		return new EqualsBuilder()
			.append(o.periods, periods)
			.isEquals();
	}

}
