package ru.prolib.aquila.core.timetable;

import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Составной внутридневной период. 
 */
@XStreamAlias("TimeComposite")
public class TimeCompositePeriod implements TimePeriod {
	@XStreamImplicit(itemFieldName="period")
	@XStreamConverter(PeriodConverter.class)
	private final List<TimePeriod> periods;
	
	public TimeCompositePeriod() {
		super();
		periods = new Vector<TimePeriod>();
	}

	@Override
	public synchronized boolean contains(LocalDateTime time) {
		for ( TimePeriod p : periods ) {
			if ( p.contains(time) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized LocalDateTime nextStartTime(LocalDateTime time) {
		for ( TimePeriod p : periods ) {
			LocalDateTime next = p.nextStartTime(time);
			if ( next != null && time.compareTo(next) <= 0 ) {
				return next;
			}
		}
		return null;
	}

	@Override
	public synchronized LocalDateTime nextEndTime(LocalDateTime time) {
		List<LocalDateTime> list = new Vector<LocalDateTime>();
		for ( TimePeriod p : periods ) {
			LocalDateTime end = p.nextEndTime(time);
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
		periods.add(period);
	}
	
	/**
	 * Получить список составных периодов.
	 * <p>
	 * @return список периодов
	 */
	public synchronized List<TimePeriod> getPeriods() {
		return new Vector<TimePeriod>(periods);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TimeCompositePeriod.class ) {
			return false;
		}
		TimeCompositePeriod o = (TimeCompositePeriod) other;
		return new EqualsBuilder()
			.append(o.periods, periods)
			.isEquals();
	}

}
