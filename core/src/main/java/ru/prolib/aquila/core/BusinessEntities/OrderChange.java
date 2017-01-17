package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

@Deprecated
public interface OrderChange extends ContainerTransaction {
	
	/**
	 * Check if order status will be changed.
	 * <p>
	 * @return true if the order status will be changed, false - otherwise
	 */
	public boolean isStatusChanged();
	
	/**
	 * Check if the order will be finalized.
	 * <p>
	 * @return true if the order will be finalized, false -otherwise
	 */
	public boolean isFinalized();
	
	/**
	 * Get new order status.
	 * <p>
	 * @return the new order status if it will be changed or null if status still unchanged
	 */
	public OrderStatus getStatus();
	
	/**
	 * Get time of finalization.
	 * <p>
	 * @return the time of finalization or null if the order still not finalized
	 */
	public Instant getDoneTime();
	
	/**
	 * Get current volume.
	 * <p>
	 * @return the order current volume after applying this change
	 */
	public long getCurrentVolume();
	
	/**
	 * Get executed value.
	 * <p>
	 * @return the order executed value after applying this change
	 */
	public double getExecutedValue();
	
	/**
	 * Get transaction order.
	 * <p>
	 * @return order
	 */
	public Order getOrder();
	
	/**
	 * Get transaction execution.
	 * <p>
	 * @return new order execution or null if execution is not defined
	 */
	public OrderExecution getExecution();

	public String getSystemMessage();
	
}
