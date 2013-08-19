package ru.prolib.aquila.core.timetable;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import com.thoughtworks.xstream.annotations.*;

/**
 * Составной внутринедельный период дат.
 * <p>
 */
@XStreamAlias("DayOfWeekComposite")
public class DOWCompositePeriod implements DatePeriod {
	@XStreamImplicit(itemFieldName="period")
	private final List<DOWPeriod> periods;

	public DOWCompositePeriod() {
		super();
		periods = new Vector<DOWPeriod>();
	}

	@Override
	public synchronized boolean contains(DateTime time) {
		for ( DOWPeriod p : periods ) {
			if ( p.contains(time) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized DateTime nextDate(DateTime time) {
		List<DateTime> list = new Vector<DateTime>();
		for ( DOWPeriod p : periods ) {
			DateTime next = p.nextDate(time);
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
	public synchronized boolean isEndDate(DateTime time) {
		for ( DOWPeriod p : periods ) {
			if ( p.isEndDate(time) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized DateTime nextEndDate(DateTime time) {
		List<DateTime> list = new Vector<DateTime>();
		for ( DOWPeriod p : periods ) {
			DateTime next = p.nextEndDate(time);
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
	public synchronized List<DOWPeriod> getPeriods() {
		return new Vector<DOWPeriod>(periods);
	}
	
	/**
	 * Добавить период.
	 * <p>
	 * @param period период
	 */
	public synchronized void add(DOWPeriod period) {
		for ( DOWPeriod p : periods ) {
			if ( p.overlap(period) ) {
				throw new IllegalArgumentException("Period " + period
						+ " overlaps with " + p);
			}
		}
		periods.add(period);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DOWCompositePeriod.class ) {
			return false;
		}
		DOWCompositePeriod o = (DOWCompositePeriod) other;
		return new EqualsBuilder()
			.append(o.periods, periods)
			.isEquals();
	}

}
