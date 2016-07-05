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
	 * Calculate changed attributes based on executions.
	 * <p>
	 * This method calculates changed attributes based on existing executions.
	 * In case when all executions gives a total volume greater or equals than
	 * initial volume then appropriate status change will be added. In this case
	 * the latest execution time will be used as the order finalization time.
	 * Returned change have to be applied to the order instance. It is possible
	 * to add some additional fields to the change before the applying.
	 * <p>
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @return attributes which should be changed
	 */
	@Deprecated
	public Map<Integer, Object> getChangeWhenExecutionAdded();
	
	/**
	 * Calculate changed attributes after new execution added.
	 * <p>
	 * This method allows predict how the order attributes changed if the new
	 * execution will be added. Note that this information should not be applied
	 * to the order directly. Use {@link #getChangeWhenExecutionAdded()} or
	 * {@link #updateWhenExecutionAdded()} to apply changes after adding
	 * execution.
	 * <p>
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @param executionTime - time of the execution
	 * @param executedVolume - executed volume have to be added to existing
	 * @param executedValue - executed value
	 * @return attributes which should be changed
	 */
	@Deprecated
	public OrderChange getChangeWhenExecutionAdded(Instant executionTime,
			long executedVolume, double executedValue);
	
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
	 * @param reason - reason
	 * @return tokens
	 */
	@Deprecated
	public Map<Integer, Object> getChangeWhenCancelFailed(Instant time, String reason);
	
	/**
	 * Calculate and apply changes of attributes based on executions.
	 * <p>
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * This method is a shortcut which allows apply changes obtained by calling
	 * {@link #getChangeWhenExecutionAdded()} method to the order container.
	 * Will cause appropriate events. Useful if no additional tokens should be
	 * added with those changes.
	 */
	@Deprecated
	public void updateWhenExecutionAdded();
	
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

	/**
	 * This method is deprecated and will be removed. Use {@link OrderChange}
	 * transaction to change order.
	 * <p>
	 * @param time - time
	 * @param reason - reason
	 */
	@Deprecated
	public void updateWhenCancelFailed(Instant time, String reason);
	
	public void fireArchived();

}
