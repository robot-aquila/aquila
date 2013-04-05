package ru.prolib.aquila.stat;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.text.StrBuilder;

/**
 * 2012-02-02
 * $Id$
 *
 * Отчет о трейде.
 * Трейд - это последовательность сделок, на открытие и закрытие позиции,
 * которые последовательно меняют текущую позицию сначала в состояние отличное
 * от нуля, а затем возвращают портфель в нулевую позицию.
 * 
 * Отчет о трейде фиксирует изменения позиции начиная с первого изменения в
 * сторону увеличения (лонг) или уменьшения (шорт) и заканчивая последним
 * изменением, которое приводит состояние портфеля к нулевому балансу.
 */
public class TradeReport {
	private final LinkedList<PositionChange> changes;
	
	public TradeReport() {
		super();
		changes = new LinkedList<PositionChange>();
	}
	
	public PositionChange getFirstChange() throws TradeReportException {
		if ( changes.size() == 0 ) {
			throw new TradeReportException();
		}
		return changes.getFirst();
	}
	
	public PositionChange getLastChange() throws TradeReportException {
		if ( changes.size() == 0 ) {
			throw new TradeReportException();
		}
		return changes.getLast();
	}
	
	public void addChange(PositionChange change) {
		changes.add(change);
	}
	
	public List<PositionChange> getChanges() {
		return new LinkedList<PositionChange>(changes);
	}
	
	public int getChangeCount() {
		return changes.size();
	}
	
	public boolean isShort() throws TradeReportException {
		return getFirstChange().getQty() < 0;
	}
	
	public boolean isLong() throws TradeReportException {
		return getFirstChange().getQty() > 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == this ) {
			return true;
		}
		if ( o == null ) {
			return false;
		}
		if ( o.getClass() != getClass() ) {
			return false;
		}
		TradeReport other = (TradeReport)o;
		if ( changes.size() != other.changes.size() ) {
			return false;
		}
		for ( int i = 0; i < changes.size(); i ++ ) {
			if ( ! changes.get(i).equals(other.changes.get(i)) ) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return new StrBuilder()
			.append("[")
			.appendWithSeparators(changes, "]\n[")
			.append("]")
			.toString();
	}

}
