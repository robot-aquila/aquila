package ru.prolib.aquila.stat.counter;

import java.util.Iterator;
import java.util.Map;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.Observable;

/**
 * 2012-02-03
 * $Id: CounterSet.java 197 2012-02-05 20:21:19Z whirlwind $
 * 
 * Интерфейс набора счетчиков.
 */
public interface CounterSet extends Observable {

	/**
	 * Добавить счетчик с указанным идентификатором.
	 * 
	 * @param id уникальный идентификатор
	 * @param counter счетчик
	 * @throws CounterAlreadyExistsException
	 */
	public void add(String id, Counter<?> counter)
		throws CounterAlreadyExistsException;
	
	/**
	 * Удалить счетчик с указанным идентификатором.
	 * 
	 * @param id идентификатор счетчика
	 */
	public void remove(String id);
	
	/**
	 * Получить счетчик по идентификатору.
	 * 
	 * @param id идентификатор
	 * @return экземпляр счетчика
	 * @throws CounterNotExistsException TODO
	 */
	public Counter<?> get(String id) throws CounterNotExistsException;
	
	/**
	 * Запустить все счетчики.
	 * 
	 * @param locator
	 * @throws CounterException
	 */
	public void startService(ServiceLocator locator) throws CounterException;
	
	/**
	 * Остановить счетчики.
	 * 
	 * @throws CounterException
	 */
	public void stopService() throws CounterException;
	
	/**
	 * Получить итератор счетчиков.
	 * 
	 * @return
	 */
	public Iterator<Map.Entry<String, Counter<?>>> iterator();

}
