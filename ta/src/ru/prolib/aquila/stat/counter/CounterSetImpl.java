package ru.prolib.aquila.stat.counter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.ObservableComplex;
import ru.prolib.aquila.util.ObservableComplexImpl;

/**
 * Набор счетчиков.
 * 
 * Позволяет определять список счетчиков, идентифицируя каждый из них
 * произвольной строкой, которая может быть использована в том числе и в
 * операциях вывода. Выполняет запуск и останов сервисов счетчиков одним
 * вызовом. Генерирует уведомление в момент когда все счетчики были изменены,
 * что может быть использовано для определения момента формирования строк
 * отчета. 
 * 
 * 2012-02-03
 * $Id: CounterSetImpl.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class CounterSetImpl extends Observable implements CounterSet,Observer {
	private final ObservableComplex observable;
	private final LinkedHashMap<String, Counter<?>> counters;
	
	public CounterSetImpl() {
		this(new ObservableComplexImpl());
	}
	
	public CounterSetImpl(ObservableComplex observable) {
		super();
		this.observable = observable;
		counters = new LinkedHashMap<String, Counter<?>>();
	}

	@Override
	public void add(String id, Counter<?> counter)
		throws CounterAlreadyExistsException
	{
		if ( counters.containsKey(id) ) {
			throw new CounterAlreadyExistsException(id);
		}
		counters.put(id, counter);
	}

	@Override
	public void remove(String id) {
		counters.remove(id);
	}

	@Override
	public Counter<?> get(String id) throws CounterNotExistsException {
		if ( ! counters.containsKey(id) ) {
			throw new CounterNotExistsException(id);
		}
		return counters.get(id);
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		observable.addObserver(this);
		Iterator<Map.Entry<String, Counter<?>>> it = iterator();
		while ( it.hasNext() ) {
			Counter<?> counter = it.next().getValue();
			observable.addObservable(counter);
			counter.startService(locator);
		}
	}

	@Override
	public void stopService() throws CounterException {
		Iterator<Map.Entry<String, Counter<?>>> it = iterator();
		while ( it.hasNext() ) {
			Counter<?> counter = it.next().getValue(); 
			counter.stopService();
			observable.deleteObservable(counter);
		}
		observable.deleteObserver(this);
	}

	@Override
	public Iterator<Map.Entry<String, Counter<?>>> iterator() {
		return counters.entrySet().iterator();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		observable.reset();
		setChanged();
		notifyObservers();
	}

}
