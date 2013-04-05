package ru.prolib.aquila.ChaosTheory;

import java.util.Set;

import ru.prolib.aquila.util.Observable;

/**
 * Интерфейс хранилища заявок.
 * Наблюдаемый объект. Уведомляет о появлении новой заявки. В качестве второго
 * аргумента метода
 * {@link java.util.Observer#update(java.util.Observable, Object) передается
 * экземпляр новой заявки. 
 */
public interface PortfolioOrders extends Observable {

	/**
	 * Добавить заявку в пул наблюдателя.
	 * @param order
	 * @throws OrderException
	 */
	public void startWatch(Order order) throws PortfolioException;
	
	/**
	 * Дожидаться исполнения или отмены заявки не дольше указанного времени.
	 * @param order заявка
	 * @param timeout таймаут в миллисекундах
	 * @throws PortfolioException
	 * @throws PortfolioTimeoutException
	 * @throws InterruptedException
	 */
	public void waitForComplete(Order order, long timeout)
		throws PortfolioTimeoutException,
			   PortfolioException,
			   InterruptedException;
	
	/**
	 * Получить список активных заявок
	 * @return
	 */
	public Set<Order> getActiveOrders();
	
}