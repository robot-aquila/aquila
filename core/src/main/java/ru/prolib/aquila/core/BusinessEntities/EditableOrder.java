package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;


/**
 * Order service interface.
 */
public interface EditableOrder extends Order, UpdatableContainer {
	
	/**
	 * Add new order execution.
	 * <p>
	 * This method is used to add new order execution. It will not update the
	 * order attributes related to executions but will fire the new
	 * execution event. Do not use this method to load existing executions.
	 * Use {@link #loadExecution(long, String, Instant, double, long, double)}
	 * instead.
	 * <p>
	 * @param id - ID of execution
	 * @param externalID - ID of execution assigned by remote trading system
	 * @param time - time of execution
	 * @param price - price per unit
	 * @param volume - executed volume
	 * @param value - value of execution in units of account currency
	 * @throws OrderException - will thrown if the order already in final state
	 * or if an execution with the same ID already exists
	 */
	public void addExecution(long id, String externalID, Instant time,
			double price, long volume, double value)
					throws OrderException;
	
	/**
	 * Load existing order execution.
	 * <p>
	 * This method is used to load an existing execution when the order is
	 * loading from external source. It will not cause any events and does not
	 * affect the order attributes. All general attributes of the order must be
	 * loaded before calling this method.
	 * <p>
	 * @param id - ID of execution
	 * @param externalID - ID of execution assigned by remote trading system
	 * @param time - time of execution
	 * @param price - price per unit
	 * @param volume - executed volume
	 * @param value - value of execution in units of account currency
	 * @throws OrderException - will thrown if an execution with the same ID already exists
	 */
	public void loadExecution(long id, String externalID, Instant time,
			double price, long volume, double value)
					throws OrderException;
	
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
	public void enableStatusEvents(boolean enable);
	
	/**
	 * Check that the order status events are enabled.
	 * <p>
	 * @return true if events are enable, false otherwise
	 */
	public boolean isStatusEventsEnabled();

}
