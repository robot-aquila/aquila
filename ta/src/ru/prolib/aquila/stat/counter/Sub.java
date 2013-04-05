package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.ObservableComplex;
import ru.prolib.aquila.util.ObservableComplexImpl;

/**
 * Расчитывает разницу между значениями двух счетчиков.
 * 
 * 2012-02-02
 * $Id: Sub.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class Sub extends Observable implements Counter<Double>,Observer {
	private final ObservableComplex observable;
	private final Counter<Double> c1,c2;
	private Double value;
	
	public Sub(Counter<Double> c1, Counter<Double> c2,
			   ObservableComplex observable)
	{
		super();
		this.observable = observable;
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public Sub(Counter<Double> c1, Counter<Double> c2) {
		this(c1, c2, new ObservableComplexImpl());
	}
	
	public Counter<Double> getCounter1() {
		return c1;
	}
	
	public Counter<Double> getCounter2() {
		return c2;
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		observable.addObserver(this);
		observable.addObservable(c1);
		observable.addObservable(c2);
		value = null;
	}

	@Override
	public void stopService() throws CounterException {
		observable.deleteObservable(c2);
		observable.deleteObservable(c1);
		observable.deleteObserver(this);
		value = null;
	}

	@Override
	public void update(Observable o, Object arg) {
		observable.reset();
		value = c1.getValue() - c2.getValue();
		setChanged();
		notifyObservers();
	}

}
