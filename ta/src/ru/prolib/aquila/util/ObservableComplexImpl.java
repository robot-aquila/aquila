package ru.prolib.aquila.util;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class ObservableComplexImpl extends Observable
		implements ObservableComplex, Observer
{
	private final LinkedList<ru.prolib.aquila.util.Observable> objects;
	private final LinkedList<Boolean> states;
	
	public ObservableComplexImpl() {
		super();
		objects = new LinkedList<ru.prolib.aquila.util.Observable>();
		states = new LinkedList<Boolean>();
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		int index = objects.indexOf(o);
		if ( index >= 0 ) {
			states.set(index, true);
			
			int totalChanged = 0;
			for ( int i = 0; i <  states.size(); i ++ ) {
				if ( states.get(i) ) {
					totalChanged ++;
				}
			}
			if ( totalChanged == states.size() ) {
				setChanged();
				notifyObservers();
			}
		}
	}

	@Override
	public synchronized
		void addObservable(ru.prolib.aquila.util.Observable object)
	{
		if ( objects.indexOf(object) == -1 ) {
			objects.addLast(object);
			states.addLast(false);
			object.addObserver(this);
		}
	}

	@Override
	public synchronized void reset() {
		for ( int i = 0; i < states.size(); i ++ ) {
			states.set(i, false);
		}
	}

	@Override
	public synchronized
		void deleteObservable(ru.prolib.aquila.util.Observable object)
	{
		int index = objects.indexOf(object);
		if ( index >= 0 ) {
			object.deleteObserver(this);
			objects.remove(index);
			states.remove(index);
		}
	}

}
