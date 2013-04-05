package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * Подсчет количества с условием по валидатору.
 * 
 * Уведомления отправляются каждый раз после обновления исходного счетчика
 * независимо от результата вызова валидатора. То есть, уведомления этого
 * счетчика будут приходить каждый раз при обновлении исходного счетчика.
 * 
 * 2012-02-06
 * $Id: Count.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class Count extends Observable implements Counter<Double>, Observer {
	private final Counter<Double> counter;
	private final Validator validator;
	private Double value = 0.0d;
	
	public Count(Counter<Double> counter) {
		this(counter, new ValidatorAll());
	}
	
	public Count(Counter<Double> counter, Validator validator) {
		super();
		this.counter = counter;
		this.validator = validator;
	}
	
	public Validator getValidator() {
		return validator;
	}
	
	public Counter<Double> getCounter() {
		return counter;
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( validator.shouldCounted(counter.getValue()) ) {
			value ++;
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
