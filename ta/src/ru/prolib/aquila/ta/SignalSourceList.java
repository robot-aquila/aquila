package ru.prolib.aquila.ta;

import java.util.LinkedList;

public class SignalSourceList {
	private final LinkedList<ISignalSource> sources;
	
	public SignalSourceList() {
		sources = new LinkedList<ISignalSource>();
	}
	
	/**
	 * Добавить источник сигналов в список.
	 * @param src
	 * @return номер источника в списке (начиная с единицы)
	 */
	public synchronized int addSignalSource(ISignalSource src) {
		sources.add(src);
		return sources.size();
	}
	
	public synchronized SignalList getCurrentSignals() throws ValueException {
		SignalList list = new SignalList();
		for ( int i = 0; i < sources.size(); i ++ ) {
			list.setSourceId(i + 1);
			sources.get(i).analyze(list);
		}
		return list;
	}

}
