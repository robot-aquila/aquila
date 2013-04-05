package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.ObservableComplex;
import ru.prolib.aquila.util.ObservableComplexImpl;

/**
 * Ratio - отношение значения второго счетчика к значению первого счетчика.
 * Фактически выполняет деление c2 / c1 где c1 - значение первого счетчика,
 * c2 - значение второго. 
 * 
 * В качестве примера использования можно рассмотреть максимальную просадку
 * портфеля в процентах. Для этого необходимо два счетчика: c1 - max equity,
 * c2 - equity absolute drawdown.
 * 
 * 2012-02-03
 * $Id: Ratio.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class Ratio extends Observable implements Counter<Double>,Observer {
	private static Logger logger = LoggerFactory.getLogger(Ratio.class);
	private final ObservableComplex observable;
	private final Counter<Double> c1,c2;
	private Double value;
	
	public Ratio(Counter<Double> c1, Counter<Double> c2,
				 ObservableComplex observable)
	{
		super();
		this.observable = observable;
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public Ratio(Counter<Double> c1, Counter<Double> c2) {
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
	}

	@Override
	public void stopService() throws CounterException {
		observable.deleteObservable(c2);
		observable.deleteObservable(c1);
		observable.deleteObserver(this);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		observable.reset();
		Double divisor = c1.getValue();
		if ( divisor != null && divisor != 0.0d ) {
			value = c2.getValue() / divisor;
		} else {
			value = 0.0d;
			logger.warn("Division by zero. Forced zero result.");
		}
		setChanged();
		notifyObservers();
	}

}
