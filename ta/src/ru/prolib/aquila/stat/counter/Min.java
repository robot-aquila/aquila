package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * 2012-02-02
 * $Id: Min.java 197 2012-02-05 20:21:19Z whirlwind $
 * 
 * Расчитывает минимальное значение по другому счетчику.
 */
public class Min extends Observable implements Counter<Double>, Observer {
	private final Counter<Double> counter;
	private Double value;
	
	public Min(Counter<Double> counter) {
		super();
		this.counter = counter;
	}
	
	public Counter<Double> getCounter() {
		return counter;
	}

	@Override
	public void update(Observable o, Object arg) {
		Double current = counter.getValue();
		if ( value == null || current < value ) {
			value = current;
		}
		setChanged();
		notifyObservers();
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		value = null;
		counter.addObserver(this);
	}

	@Override
	public void stopService() throws CounterException {
		counter.deleteObserver(this);
		value = null;
	}

}
