package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The factory of order common transactions.
 */
public class OrderTransactionFactory {
	
	/**
	 * Create new order execution.
	 * <p>
	 * @param order - order
	 * @param executionID - execution ID
	 * @param externalID - execution external ID
	 * @param executionTime - execution time
	 * @param price - execution price
	 * @param volume - execution volume
	 * @param executedValue - executed value (money)
	 * @return transaction
	 */
	public OrderChange createNewExecution(EditableOrder order, long executionID,
			String externalID, Instant executionTime, double price, long volume,
			double executedValue)
	{
		long currentVolume = order.getCurrentVolume() - volume;
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, currentVolume);
		tokens.put(OrderField.EXECUTED_VALUE, order.getExecutedValue() + executedValue);
		if ( currentVolume <= 0L ) {
			tokens.put(OrderField.STATUS, OrderStatus.FILLED);
			tokens.put(OrderField.TIME_DONE, executionTime);
		}
		OrderExecution execution = new OrderExecutionImpl(order.getTerminal(),
				executionID, externalID, order.getSymbol(), order.getAction(),
				order.getID(), executionTime, price, volume, executedValue);
		onNewExecution(order, tokens, execution);
		return new OrderChangeImpl(order, tokens, execution);
	}
	
	/**
	 * Create new order execution.
	 * <p>
	 * @param order - order
	 * @param executionID - execution ID
	 * @param price - execution price
	 * @param volume - execution volume
	 * @param executedValue - executed value (money)
	 * @return transaction
	 */
	public OrderChange createNewExecution(EditableOrder order, long executionID,
			double price, long volume, double executedValue)
	{
		return createNewExecution(order, executionID, null, 
			order.getTerminal().getCurrentTime(), price, volume, executedValue);
	}
	
	/**
	 * Create change of order finalization.
	 * <p>
	 * @param order - order
	 * @param finalStatus - order final status
	 * @param timeDone - time of finalization
	 * @param systemMessage - system message
	 * @return transaction
	 */
	public OrderChange createFinalization(EditableOrder order,
			OrderStatus finalStatus, Instant timeDone, String systemMessage)
	{
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.STATUS, finalStatus);
		tokens.put(OrderField.TIME_DONE, timeDone);
		tokens.put(OrderField.SYSTEM_MESSAGE, systemMessage);
		onFinalization(order, tokens);
		return new OrderChangeImpl(order, tokens);
	}
	
	/**
	 * Create change of order finalization with current time of terminal.
	 * <p>
	 * @param order - order
	 * @param finalStatus - order final status
	 * @return transaction
	 */
	public OrderChange createFinalization(EditableOrder order, OrderStatus finalStatus) {
		return createFinalization(order, finalStatus, order.getTerminal().getCurrentTime(), null);
	}

	public OrderChange createCancellation(EditableOrder order, Instant timeDone) {
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.STATUS, OrderStatus.CANCELLED);
		tokens.put(OrderField.TIME_DONE, timeDone);
		onCancellation(order, tokens);
		return new OrderChangeImpl(order, tokens);
	}
	
	public OrderChange createCancellation(EditableOrder order) {
		return createCancellation(order, order.getTerminal().getCurrentTime());
	}
	
	public OrderChange createRegistration(EditableOrder order) {
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.STATUS, OrderStatus.ACTIVE);
		onRegistration(order, tokens);
		return new OrderChangeImpl(order, tokens);
	}
	
	public OrderChange createRejection(EditableOrder order, String reason) {
		return createFinalization(order, OrderStatus.REJECTED,
				order.getTerminal().getCurrentTime(), reason);
	}

	protected void onRegistration(EditableOrder order, Map<Integer, Object> tokens) {
		
	}
	
	protected void onCancellation(EditableOrder order, Map<Integer, Object> tokens) {
		
	}
	
	protected void onFinalization(EditableOrder order, Map<Integer, Object> tokens) {
		
	}
	
	protected void onNewExecution(EditableOrder order,
			Map<Integer, Object> tokens, OrderExecution execution)
	{
		
	}

}
