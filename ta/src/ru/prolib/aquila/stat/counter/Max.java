package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * 2012-02-02
 * $Id: Max.java 197 2012-02-05 20:21:19Z whirlwind $
 * 
 * Расчитывает максимальное значение по другому счетчику.
 */
public class Max extends Observable implements Observer,Counter<Double>  {
	private final Counter<Double> counter;
	private Double value = null;
	
	public Max(Counter<Double> counter) {
		super();
		this.counter = counter;
	}
	
	public Counter<Double> getCounter() {
		return counter;
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

	@Override
	public void update(Observable o, Object arg) {
		Double current = counter.getValue();
		if ( value == null || current > value ) {
			value = current;
		}
		setChanged();
		notifyObservers();
	}

}
