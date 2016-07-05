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

}
