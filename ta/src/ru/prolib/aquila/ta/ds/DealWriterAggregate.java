package ru.prolib.aquila.ta.ds;

import java.util.*;

import ru.prolib.aquila.ta.Deal;


/**
 * Агрегат, дублирующий вызовы для каждого зарегистрированного устройства.
 */
public class DealWriterAggregate implements DealWriter {
	private final Set<DealWriter> writers;
	
	public DealWriterAggregate() {
		writers = new HashSet<DealWriter>();
	}

	@Override
	public synchronized boolean addDeal(Deal deal) throws DealWriterException {
		boolean result = false;
		Iterator<DealWriter> i = writers.iterator();
		while ( i.hasNext() ) {
			if ( i.next().addDeal(deal) ) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public synchronized boolean flush() throws DealWriterException {
		boolean result = false;
		Iterator<DealWriter> i = writers.iterator();
		while ( i.hasNext() ) {
			if ( i.next().flush() ) {
				result = true;
			}
		}
		return result;
	}
	
	public synchronized void attachWriter(DealWriter writer) {
		writers.add(writer);
	}
	
	public synchronized void detachWriter(DealWriter writer) {
		writers.remove(writer);
	}

	public synchronized Set<DealWriter> getWriters() {
		return new HashSet<DealWriter>(writers);
	}

}
