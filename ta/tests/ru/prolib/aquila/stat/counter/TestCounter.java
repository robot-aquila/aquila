package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

public class TestCounter<T> extends Observable implements Counter<T>, Observer {
	private T value;
	
	public TestCounter() {
		super();
	}

	@Override
	public void update(Observable o, Object arg) {

	}

	@Override
	public T getValue() {
		return value;
	}
	
	public void setValueAndNotifyObservers(T newValue) {
		value = newValue;
		setChanged();
		notifyObservers();
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		
	}

	@Override
	public void stopService() throws CounterException {
		
	}

}
