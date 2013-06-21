package ru.prolib.aquila.ib.api;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

/**
 * Интерфейс обработчика данных заявки.
 */
public interface OrderHandler extends ResponseHandler {
	
	public void openOrder(int orderId, Contract contract, Order order,
			OrderState orderState);
	
	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld);

}
