package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;


/**
 * Order service interface.
 */
public interface EditableOrder extends Order, UpdatableContainer {
	
	/**
	 * Add order execution.
	 * <p>
	 * This method is used to add order execution. This is shortcut for the
	 * {@link #addExecution(OrderExecution)} method.
	 * <p>
	 * @param id - ID of execution
	 * @param externalID - ID of execution assigned by remote trading system
	 * @param time - time of execution
	 * @param price - price per unit
	 * @param volume - executed volume
	 * @param value - value of execution in units of account currency
	 * @return order execution instance
	 * @throws OrderException - will thrown if the order already in final state
	 * or if an execution with the same ID already exists
	 */
	public OrderExecution addExecution(long id, String externalID, Instant time,
			double price, long volume, double value)
					throws OrderException;
		
	/**
	 * Add order execution.
	 * <p>
	 * This method is used to add execution. The execution must have unique
	 * execution ID. This call will not cause any events and does not affect the
	 * order attributes. All attributes of the order should be changed
	 * after loading of the execution. To fire appropriate event tied to the
	 * execution the {@link #fireExecution(OrderExecution)} method should
	 * be used.
	 * <p>
	 * @param execution - execution instance
	 * @throws OrderException - will thrown if an execution with the same ID already exists
	 */
	public void addExecution(OrderExecution execution) throws OrderException;
	
	public void fireExecution(OrderExecution execution);
	
	/**
	 * Disable or enable order status events.
	 * <p>
	 * This method should be used to disable or enable order status events when
	 * the order is loading from external data source. Disabling those events
	 * will not cause any status change events like {@link #onCancelled()},
	 * {@link #onFilled()}, etc...
	 * <p>
	 * @param enable - enable or disable events
	 */
	public void setStatusEventsEnabled(boolean enable);
	
	/**
	 * Check that the order status events are enabled.
	 * <p>
	 * @return true if events are enable, false otherwise
	 */
	public boolean isStatusEventsEnabled();
		
	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @param time - time
	 * @return tokens
	 */
	@Deprecated
	public Map<Integer, Object> getChangeWhenCancelled(Instant time);
	
	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @param time - time
	 * @param reason - reason
	 * @return tokens
	 */
	@Deprecated
	public Map<Integer, Object> getChangeWhenRejected(Instant time, String reason);
	
	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @return tokens
	 */
	@Deprecated
	public Map<Integer, Object> getChangeWhenRegistered();
			
	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @param time - time
	 */
	@Deprecated
	public void updateWhenCancelled(Instant time);
	
	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @param time - time
	 * @param reason - reason
	 */
	@Deprecated
	public void updateWhenRejected(Instant time, String reason);
	
	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 */
	@Deprecated
	public void updateWhenRegistered();
	
	public void fireArchived();

}
