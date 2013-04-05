package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * Суммирует значения счетчика при каждом обновлении.
 * 
 * 2012-02-06
 * $Id: Sum.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class Sum extends Observable implements Counter<Double>, Observer {
	private final Counter<Double> counter;
	private final Validator validator;
	private Double value = 0.0d;
	
	public Sum(Counter<Double> counter) {
		this(counter, new ValidatorAll());
	}
	
	public Sum(Counter<Double> counter, Validator validator) {
		super();
		this.counter = counter;
		this.validator = validator;
	}
	
	public Counter<Double> getCounter() {
		return counter;
	}
	
	public Validator getValidator() {
		return validator;
	}

	@Override
	public void update(Observable o, Object arg) {
		Double current = counter.getValue();
		if ( validator.shouldCounted(current) ) {
			value += counter.getValue();
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
		counter.addObserver(this);
	}

	@Override
	public void stopService() throws CounterException {
		counter.deleteObserver(this);
	}

}
