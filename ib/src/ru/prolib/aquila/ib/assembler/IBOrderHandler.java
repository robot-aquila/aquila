package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.*;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Обработчик локальной заявки.
 * <p>
 * Данный класс выполняет регистрацию и ведение локальной заявки (созданной
 * через объект терминала) до момента ее финализации.
 */
public class IBOrderHandler implements OrderHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBOrderHandler.class);
	}
	
	private final EditableOrder order;
	private final PlaceOrderRequest request;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param order заявка
	 * @param request параметры запроса на выставление заявки
	 */
	public IBOrderHandler(Order order, PlaceOrderRequest request) {
		super();
		this.order = (EditableOrder) order;
		this.request = request;
	}
	
	Order getOrder() {
		return order;
	}
	
	PlaceOrderRequest getPlaceOrderRequest() {
		return request;
	}

	/**
	 * Регистрация заявки.
	 * <p>
	 * Данный метод выполняет отправку запроса на регистрацию заявки в торговой
	 * системе. Никаких проверок текущего состояния заявки не выполняется.
	 */
	public void placeOrder() {
		synchronized ( order ) {
			applyStatus(OrderStatus.SENT);
			order.getSystemInfo().getRegistration().setRequestTime();
			order.getSystemInfo().getRegistration().setRequest(request);
			getClient().placeOrder(request.getOrderId(), request.getContract(),
					request.getOrder());
			logger.debug("Place order initiated: {}", order.getId());
		}
	}
	
	/**
	 * Снятие заявки.
	 * <p>
	 * Данный метод выполняет отправку запроса на снятие ранее
	 * зарегистрированной заявки.
	 */
	public void cancelOrder() {
		synchronized ( order ) {
			applyStatus(OrderStatus.CANCEL_SENT);
			order.getSystemInfo().getCancellation().setRequestTime();
			order.getSystemInfo().getCancellation().setRequest(order.getId());
			getClient().cancelOrder(order.getId());
			logger.debug("Cancel order initiated: {}", order.getId());
		}
	}
	
	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			Object args[] = { reqId, status, errorCode, errorMsg };
			logger.error("For order #{} (status {}) IB said: [{}] {}", args);
			// Начального статуса здесь быть не может потому, что на момент
			// до выставления заявки обработчик еще не зарегистрирован.
			// Финального статуса здесь так же быть не может потому, что после
			// финализации заявки, обработчик удаляет себя из пула.
			// В активном статусе ошибки нас не интересуют, так как это скорее
			// всего просто какие-то информационные сообщения.
			// А вот промежуточные статусы проверяем.
			Transaction trans;
			if ( status == OrderStatus.ACTIVE ) {
				return;
				
			} else if ( status == OrderStatus.SENT ) {
				trans = order.getSystemInfo().getRegistration();
				trans.setResponseTime();
				trans.setResponse(errorMsg);
				applyStatus(OrderStatus.REJECTED);
				
			} else if ( status == OrderStatus.CANCEL_SENT ) {
				trans = order.getSystemInfo().getCancellation();
				trans.setResponseTime();
				trans.setResponse(errorMsg);
				applyStatus(OrderStatus.CANCEL_FAILED);
				
			}
			closeHandler();
		}
	}

	@Override
	public void connectionOpened() {
		
	}

	@Override
	public void connectionClosed() {

	}

	@Override
	public void openOrder(int orderId, Contract contract, 
			com.ib.client.Order ibo, OrderState ibos)
	{
		OrderEntry entry = new OrderEntry(orderId, contract, ibo, ibos);
		getCache().update(entry); // Чисто в отладочных целях для просмотра кэша
		synchronized ( order ) {
			// Так как этот метод вызывается в первую очередь, здесь нас
			// интересует только подтверждение регистрации заявки.
			if ( order.getStatus() == OrderStatus.SENT ) {
				Transaction trans = order.getSystemInfo().getRegistration();
				trans.setResponseTime();
				trans.setResponse(entry);
				applyStatus(OrderStatus.ACTIVE);
				Object args[] = { orderId, trans.getLatency() };
				logger.debug("Order #{} activated, latency={}", args);
			}
		}
	}

	@Override
	public void orderStatus(int orderId, String ibStatus, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld)
	{
		OrderStatusEntry entry =
			new OrderStatusEntry(orderId, ibStatus, remaining, avgFillPrice);
		getCache().update(entry); // Чисто в отладочных целях для просмотра кэша
		synchronized ( order ) {
			
			order.setQtyRest(entry.getQtyRest());
			order.setAvgExecutedPrice(entry.getAvgExecutedPrice());
			order.setExecutedVolume(entry.getAvgExecutedPrice()
					* (double) (order.getQty() - entry.getQtyRest()));

			// Когда добавится согласование по сделкам, выставление
			// финального статуса нужно перенести туда. В этом случае,
			// контроль финализации полностью переходит на механизм
			// согласования по сделкам, который будет инициироваться
			// при поступлении соответствующих данных. А здесь в конце, в
			// любом случае удаляем обработчик из пула.
			OrderStatus entryStatus = entry.getStatus();
			if ( entryStatus != null && entryStatus.isFinal() ) {
				if ( entryStatus == OrderStatus.CANCELLED ) {
					Transaction trans = order.getSystemInfo().getCancellation();
					trans.setResponseTime();
					trans.setResponse(entry);
					Object args[] = {orderId, entryStatus, trans.getLatency() };
					logger.debug("Order #{} {}, latency={}", args);
				} else {
					Object args[] = { orderId, entryStatus };
					logger.debug("Order #{} {}", args);
				}
				applyStatus(entryStatus);
				closeHandler();
			} else if ( order.hasChanged()) {
				order.fireChangedEvent();
				order.resetChanges();
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBOrderHandler.class ) {
			return false;
		}
		IBOrderHandler o = (IBOrderHandler) other;
		return new EqualsBuilder()
			.append(o.order, order)
			.append(o.request, request)
			.isEquals();
	}
	
	/**
	 * Получить клиентское подключение к IB API.
	 * <p>
	 * @return подключение
	 */
	private IBClient getClient() {
		return getTerminal().getClient();
	}
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	private Cache getCache() {
		return getTerminal().getCache();
	}
	
	/**
	 * Получить экземпляр терминала заявки.
	 * <p> 
	 * @return терминал
	 */
	private IBEditableTerminal getTerminal() {
		return (IBEditableTerminal) order.getTerminal();
	}
	
	/**
	 * Применить статус заявки.
	 * <p>
	 * Устанавливает для заявки указанный статус, инициирует соответствующие
	 * события после чего сбрасывает признак изменений.
	 * <p>
	 * @param status новый статус
	 */
	private void applyStatus(OrderStatus status) {
		order.setStatus(status);
		order.fireChangedEvent();
		order.resetChanges();
	}
	
	/**
	 * Завершить работу обработчика.
	 * <p>
	 * Обработчик удаляется из пула.
	 */
	private void closeHandler() {
		getClient().removeHandler(order.getId());
		logger.debug("Closed for order: {}", order.getId());
	}

}
