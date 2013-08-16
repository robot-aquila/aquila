package ru.prolib.aquila.core.timetable;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import com.thoughtworks.xstream.annotations.*;

/**
 * Составной внутридневной период. 
 */
@XStreamAlias("HourMinuteComposite")
public class HMCompositePeriod implements TimePeriod {
	@XStreamImplicit(itemFieldName="period")
	private final List<HMPeriod> periods;
	
	public HMCompositePeriod() {
		super();
		periods = new Vector<HMPeriod>();
	}

	@Override
	public synchronized boolean contains(DateTime time) {
		for ( HMPeriod p : periods ) {
			if ( p.contains(time) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized DateTime nextStartTime(DateTime time) {
		for ( HMPeriod p : periods ) {
			DateTime next = p.nextStartTime(time);
			if ( next != null && time.compareTo(next) <= 0 ) {
				return next;
			}
		}
		return null;
	}

	@Override
	public synchronized DateTime nextEndTime(DateTime time) {
		List<DateTime> list = new Vector<DateTime>();
		for ( HMPeriod p : periods ) {
			DateTime end = p.nextEndTime(time);
			if ( end != null ) {
				list.add(end);
			}
		}
		if ( list.size() == 0 ) {
			return null;
		}
		Collections.sort(list);
		return list.get(0);
	}
	
	/**
	 * Добавить период.
	 * <p>
	 * @param period период
	 */
	public synchronized void add(HMPeriod period) {
		for ( HMPeriod p : periods ) {
			if ( p.overlap(period) ) {
				throw new IllegalArgumentException("Period " + period
						+ " overlaps with " + p);
			}
		}
		periods.add(period);
	}
	
	/**
	 * Получить список составных периодов.
	 * <p>
	 * @return список периодов
	 */
	public synchronized List<HMPeriod> getPeriods() {
		return new Vector<HMPeriod>(periods);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != HMCompositePeriod.class ) {
			return false;
		}
		HMCompositePeriod o = (HMCompositePeriod) other;
		return new EqualsBuilder()
			.append(o.periods, periods)
			.isEquals();
	}

}
