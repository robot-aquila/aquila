package ru.prolib.aquila.core.BusinessEntities;

import java.util.Set;

import ru.prolib.aquila.core.EventType;

/**
 * Terminal interface.
 * <p>
 * 2012-05-30<br>
 * $Id: Terminal.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface Terminal extends Scheduler {
	
	/**
	 * Get ID of the terminal.
	 * <p>
	 * @return terminal ID
	 */
	public String getTerminalID();

	/**
	 * When terminal is ready to work.
	 * <p>
	 * @return event type
	 */
	public EventType onTerminalReady();
	
	/**
	 * When terminal is not ready to work.
	 * <p>
	 * @return event type
	 */
	public EventType onTerminalUnready();
	
	public EventType onSecurityMarketDepthUpdate();

	public EventType onSecurityBestBid();
	
	public EventType onSecurityBestAsk();
	
	public EventType onSecurityLastTrade();
	
	/**
	 * Создать лимитную заявку.
	 * <p>
	 * Данный метод создает лимитную заявку. Новой заявке автоматически
	 * назначается очередной номер, по которому можно обращаться к заявке
	 * через терминал. В завершении генерируется событие о доступности новой
	 * заявки. Для подачи заявки в торговую систему следует использовать
	 * метод {@link #placeOrder(Order)}.
	 * <p>
	 * @param account торговый счет
	 * @param symbol инструмент
	 * @param action операция (направление заявки)
	 * @param qty количество
	 * @param price цена
	 * @return экземпляр заявки
	 */
	public Order createOrder(Account account, Symbol symbol, OrderAction action,
			long qty, double price);

	/**
	 * Создать рыночную заявку.
	 * <p>
 	 * Данный метод создает рыночную заявку. Новой заявке автоматически
	 * назначается очередной номер, по которому можно обращаться к заявке
	 * через терминал. В завершении генерируется событие о доступности новой
	 * заявки. Для подачи заявки в торговую систему следует использовать
	 * метод {@link #placeOrder(Order)}.
	 * <p>
	 * @param account торговый счет
	 * @param symbol инструмент
	 * @param action операция (направление заявки)
	 * @param qty количество
	 * @return экземпляр заявки
	 */
	public Order createOrder(Account account, Symbol symbol, OrderAction action,
			long qty);
		
	/**
	 * Subscribe for security data.
	 * <p>
	 * This method should be used to request security data stream. Some trading
	 * systems may require initial request to subscribe for security updates.
	 * This method is universal way to ask terminal to subscribe for the data.
	 * This method should be used to each security which will be used in a
	 * program to get it available for work with every terminal implementation.
	 * <p>
	 * @param symbol - the symbol
	 */
	public void subscribe(Symbol symbol);

	/**
	 * Test that order exists.
	 * <p>
	 * @param id - the order ID
	 * @return true if order with such identifier exists, false otherwise
	 */
	public boolean isOrderExists(long id);
	
	/**
	 * Получить список заявок.
	 * <p>
	 * @return список заявок
	 */
	public Set<Order> getOrders();
	
	/**
	 * Получить количество заявок.
	 * <p>
	 * @return количество заявок
	 */
	public int getOrderCount();
	
	/**
	 * Получить заявку по идентификатору.
	 * <p>
	 * @param id идентификатор заявки
	 * @return заявка
	 * @throws OrderNotExistsException - TODO:
	 */
	public Order getOrder(long id) throws OrderException;
	
	/**
	 * Получить тип события: при поступлении информации о новой заявке.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderCancelFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderCancelled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderUpdate();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderDone();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderPartiallyFilled();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderRegistered();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderRegisterFailed();
	
	/**
	 * Перехватчик событий соответствующего типа от всех заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType onOrderDeal();

	/**
	 * Проверить доступность информации о портфеле.
	 * <p>
	 * @param account идентификатор портфеля
	 * @return true если информация доступна, иначе - false
	 */
	public boolean isPortfolioExists(Account account);
		
	/**
	 * Получить список доступных портфелей.
	 * <p>
	 * @return список портфелей
	 */
	public Set<Portfolio> getPortfolios();
	
	/**
	 * Получить портфель по идентификатору.
	 * <p>
	 * @param account счет портфеля
	 * @return экземпляр портфеля
	 * @throws PortfolioNotExistsException - TODO:
	 */
	public Portfolio getPortfolio(Account account) throws PortfolioException;
	
	/**
	 * Получить портфель по-умолчанию.
	 * <p>
	 * Метод возвращает портфель в зависимости от реализации терминала. Это
	 * может быть единственный доступный портфель или первый попавшийся портфель
	 * из набора доступных.
	 * <p>
	 * @throws PortfolioException - TODO:
	 * @return портфель по-умолчанию
	 */
	public Portfolio getDefaultPortfolio() throws PortfolioException;
	
	/**
	 * Get count of existing portfolios.
	 * <p>
	 * @return count of portfolios
	 */
	public int getPortfolioCount();
	
	/**
	 * Получить тип события: при доступности информации по портфелю.
	 * <p>
	 * @return тип события
	 */
	public EventType onPortfolioAvailable();

	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType onPortfolioUpdate();
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType onPositionAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType onPositionUpdate();
	
	public EventType onPositionChange();
	
	public EventType onPositionCurrentPriceChange();
	
	/**
	 * Получить список доступных инструментов
	 * <p>
	 * @return список инструментов
	 */
	public Set<Security> getSecurities();
	
	/**
	 * Получить инструмент по дескриптору
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @return инструмент
	 * @throws SecurityNotExistsException - TODO:
	 */
	public Security getSecurity(Symbol symbol) throws SecurityException;
	
	/**
	 * Проверить наличие инструмента по дескриптору.
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @return наличие инструмента
	 */
	public boolean isSecurityExists(Symbol symbol);

	/**
	 * Получить тип события: при появлении информации о новом инструменте.
	 * <p>
	 * Генерируется событие {@link SecurityEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType onSecurityAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех инструментов.
	 * <p>
	 * @return тип события
	 */
	public EventType onSecurityUpdate();
	
	public EventType onSecuritySessionUpdate();
		
	/**
	 * Получить количество доступных инструментов.
	 * <p>
	 * @return количество инструментов
	 */
	public int getSecurityCount();
	
	/**
	 * Place order for execution.
	 * <p>
	 * @param order - the order.
	 * @throws OrderException - TODO:
	 */
	public void placeOrder(Order order) throws OrderException;
	
	/**
	 * Cancel order.
	 * <p>
	 * @param order - the order.
	 * @throws OrderException - TODO:
	 */
	public void cancelOrder(Order order) throws OrderException;
	
	/**
	 * Lock object.
	 */
	public void lock();
	
	/**
	 * Unlock object.
	 */
	public void unlock();
	
	/**
	 * Check that terminal is closed.
	 * <p>
	 * @return true if terminal closed, false otherwise
	 */
	public boolean isClosed();
	
	/**
	 * Start terminal.
	 */
	public void start();
	
	/**
	 * Stop terminal.
	 */
	public void stop();
	
	/**
	 * Check that terminal is started.
	 * <p>
	 * @return true if terminal started, false otherwise
	 */
	public boolean isStarted();

}
