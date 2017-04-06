package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.List;

import ru.prolib.aquila.core.EventType;

/**
 * Order interface.
 * <p>
 * 2012-05-30<br>
 * $Id: Order.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public interface Order extends ObservableStateContainer {
	
	/**
	 * Получить тип события: в случае провала операции
	 * <p>
	 * @return тип события
	 */
	public EventType onFailed();

	/**
	 * Получить тип события: заявка зарегистрирована на бирже.
	 * <p>
	 * @return тип события
	 */
	public EventType onRegistered();
	
	/**
	 * Получить тип события: не удалось зарегистрировать заявку.
	 * <p>
	 * @return тип события
	 */
	public EventType onRegisterFailed();

	/**
	 * Получить тип события: заявка отменена.
	 * <p>
	 * @return тип события
	 */
	public EventType onCancelled();
	
	/**
	 * Получить тип события: не удалось отменить заявку.
	 * <p>
	 * @return тип события
	 */
	public EventType onCancelFailed();
	
	/**
	 * Получить тип события: заявка полностью исполнена.
	 * <p>
	 * Событие генерируется один раз.
	 * <p>
	 * @return тип события
	 */
	public EventType onFilled();
	
	/**
	 * Получить тип события: заявка частично исполнена.
	 * <p>
	 * Данное событие возникает только в случае отмены частично исполненной
	 * заявки. Событие генерируется один раз.
	 * <p>
	 * @return тип события
	 */
	public EventType onPartiallyFilled();
	
	/**
	 * Получить тип события: заявка завершена.
	 * <p>
	 * Данное событие возникает единственный раз, когда заявка переводится в
	 * один из финальных статусов: исполнена, частично исполнена, отменена
	 * или ошибка.
	 * <p>
	 * @return тип события
	 */
	public EventType onDone();
	
	/**
	 * Получить тип события: новая сделка по заявке.
	 * <p>
	 * Генерируется событие класса {@link OrderExecutionEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType onExecution();
	
	/**
	 * Get event type: when order archived.
	 * <p>
	 * @return event type
	 */
	public EventType onArchived();

	/**
	 * Get order ID.
	 * <p>
	 * @return order ID assigned by the system (cannot be zero or undefined)
	 */
	public long getID();
	
	/**
	 * Get order external (exchange) ID.
	 * <p>
	 * @return order ID
	 */
	public String getExternalID();

	/**
	 * Получить терминал заявки.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal();

	/**
	 * Get order action.
	 * <p>
	 * @return order action
	 */
	public OrderAction getAction();
	
	/**
	 * Get order type.
	 * <p>
	 * @return order type
	 */
	public OrderType getType();
	
	/**
	 * Get account.
	 * <p>
	 * @return account of an order
	 */
	public Account getAccount();
	
	/**
	 * Get symbol.
	 * <p>
	 * @return symbol of an order
	 */
	public Symbol getSymbol();
	
	/**
	 * Get initial volume.
	 * <p>
	 * @return order initial volume
	 */
	public Long getInitialVolume();
	
	/**
	 * Get current volume.
	 * <p>
	 * This method returns the volume which have to be executed to fill order completely.
	 * When this value reaches zero then the order is completely filled.
	 * <p>
	 * @return order current (unexecuted) volume
	 */
	public Long getCurrentVolume();
	
	/**
	 * Get order status.
	 * <p>
	 * @return order status
	 */
	public OrderStatus getStatus();
	
	/**
	 * Get price.
	 * <p>
	 * @return price specified in the order or null if price is not specified
	 */
	public FDecimal getPrice();
	
	/**
	 * Get order placement time.
	 * <p>
	 * @return order placement time or null if order is not placed to the market
	 */
	public Instant getTime();
	
	/**
	 * Get order execution or cancellation time.
	 * <p>
	 * @return time of finalization or null if the order is not in final status
	 */
	public Instant getTimeDone();	
	
	/**
	 * Get actual price of executed volume.
	 * <p>
	 * @return price of executed volume in base or account currency (money)
	 */
	public FMoney getExecutedValue();
	
	/**
	 * Get comment.
	 * <p>
	 * @return order comment
	 */
	public String getComment();
	
	/**
	 * Get list of order executions.
	 * <p>
	 * @return list of executions
	 */
	public List<OrderExecution> getExecutions();	
	
	/**
	 * Get order execution by execution ID.
	 * <p>
	 * @param executionID - id of an execution
	 * @return execution instance
	 * @throws OrderException - execution with such ID not exists
	 */
	public OrderExecution getExecution(long executionID) throws OrderException;
	
	/**
	 * Get system message.
	 * <p>
	 * @return get a trading system message associated with the order or null if
	 * message is not available.
	 */
	public String getSystemMessage();
	
	/**
	 * Get user defined long variable.
	 * <p>
	 * @return variable value or null if variable not defined
	 */
	public Long getUserDefinedLong();
	
	/**
	 * Get user defined string variable.
	 * <p>
	 * @return variable value or null if variable not defined
	 */
	public String getUserDefinedString();
	
	/**
	 * Get security instance of the order.
	 * <p>
	 * @return security instance
	 * @throws IllegalStateException - security not exists
	 */
	public Security getSecurity();
	
	/**
	 * Get portfolio instance of the order.
	 * <p>
	 * @return portfolio instance
	 * @throws IllegalStateException - portfolio not exists
	 */
	public Portfolio getPortfolio();
	
}
