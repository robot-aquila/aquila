package ru.prolib.aquila.core;

import java.util.Observer;

/**
 * Simple observable object interface.
 * <p>
 * Declares simple observable object which can fit {@link java.util.Observable}. This interface is
 * for public use only.  Therefore it doesn't declare any methods except management of subscription
 * of single listener.
 */
public interface IObservable {
	
	public void addObserver(Observer o);
	
	public void deleteObserver(Observer o);

}
