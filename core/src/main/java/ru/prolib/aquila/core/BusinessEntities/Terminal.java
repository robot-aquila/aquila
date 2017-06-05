package ru.prolib.aquila.core.BusinessEntities;

import java.util.Set;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.concurrency.Lockable;

/**
 * Terminal interface.
 * <p>
 * 2012-05-30<br>
 * $Id: Terminal.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface Terminal extends Scheduler, BusinessEntity {
	
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
	
	public EventType onSecurityClose();
	
	/**
	 * Create order of custom type.
	 * <p>
	 * The method creates a new custom type order instance. The new order ID and
	 * the order time will be assigned also as order type, account, symbol,
	 * action, price, initial and current size. Newly created order should be
	 * placed to the marked using {@link #placeOrder(Order)} method.
	 * <p>
	 * @param account - account
	 * @param symbol - symbol
	 * @param type - order type
	 * @param action - order action
	 * @param qty - size of order
	 * @param price - price of order
	 * @param comment - user comment
	 * @return new order instance
	 */
	public Order createOrder(Account account, Symbol symbol, OrderType type,
			OrderAction action, long qty, FDecimal price, String comment);
	
	/**
	 * Create limit order.
	 * <p>
	 * This method creates a new limit order. The new order ID and the order
	 * time will be assigned also as order type, account, symbol, action,
	 * price, initial and current sizes. Newly created order may be placed to
	 * market using {@link #placeOrder(Order)} method.
	 * <p>
	 * @param account - account
	 * @param symbol - symbol
	 * @param action - order action
	 * @param qty - size of order
	 * @param price - limit price
	 * @return new order instance
	 */
	public Order createOrder(Account account, Symbol symbol, OrderAction action,
			long qty, FDecimal price);

	/**
	 * Create market order.
	 * <p>
	 * This method creates a new market order. The new order ID and the order
	 * time will be assigned also as order type, account, symbol, action,
	 * initial and current sizes. Newly created order may be placed to market
	 * using {@link #placeOrder(Order)} method.
	 * <p>
	 * @param account - account
	 * @param symbol - symbol
	 * @param action - order action
	 * @param qty - size of order
	 * @return new order instance
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
	public EventType onOrderExecution();
	
	public EventType onOrderArchived();
	
	public EventType onOrderClose();

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
	
	public EventType onPortfolioClose();
	
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
	 * When position object is closed.
	 * <p>
	 * Note: That event type does not indicate when the position is closed (i.e.
	 * when position volume is zero). It indicates when the position instance
	 * is closed and cannot be used in the future (it will not get updates
	 * anymore and cannot be obtained via terminal interface).
	 * <p>
	 * @return event type
	 */
	public EventType onPositionClose();
	
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
