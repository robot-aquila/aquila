package ru.prolib.aquila.stat.counter;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.Observable;

/**
 * Интерфейс счетчика.
 * 
 * Стратегия уведомления наблюдателей счетчика зависит от специфики источника.
 * Это значит, что счетчики производные от других не могут обновляться чем
 * исходный счетчик. Это нужно учитывать при использованнии конкретных
 * счетчиков.
 * 
 * Сброс используется в основном для агрегатных счетчиков. Например, для
 * расчета максимального значения по другому счетчику сброс должен приводит к
 * установке текущего значения счетчика в качестве начального. Для неагрегатных
 * счетчиков сброс должен приводить к перерасчету значения обычным способом.
 * В связи с этим возникает одно важно требование: сброс счетчиков должен
 * выполняться в том порядке, в каком счетчики были созданы. 
 *
 * 2012-02-02
 * $Id$
 */
public interface Counter<T> extends Observable {

	/**
	 * Получить значение счетчика.
	 * 
	 * @return значение счетчика или null, если значение недоступно.
	 */
	public T getValue();
	
	/**
	 * Начать расчет значения счетчика.
	 * 
	 * @param locator
	 * @throws CounterException
	 */
	public void startService(ServiceLocator locator) throws CounterException;
	
	/**
	 * Прекратить расчет значения счетчика.
	 * 
	 * @throws CounterException
	 */
	public void stopService() throws CounterException;
	
	/**
	 * Сброс счетчика.
	 */
	//public void reset();
	
}
