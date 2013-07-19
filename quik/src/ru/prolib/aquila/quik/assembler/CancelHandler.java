package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.api.QUIKResponse;
import ru.prolib.aquila.quik.api.QUIKTransactionHandler;
import ru.prolib.aquila.t2q.T2QException;

/**
 * Обработчик событий исполнения или отклонения транзакции отмены заявки.
 * <p>
 * Обрабатывает события QUIK API, генерируемые в связи с
 * обработкой транзакции регистрации заявки. 
 */
public class CancelHandler implements QUIKTransactionHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CancelHandler.class);
	}
	
	private final int transId;
	private final EditableOrder order;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param transId номер транзакции
	 * @param order снимаемая заявка
	 */
	public CancelHandler(int transId, EditableOrder order) {
		super();
		this.transId = transId;
		this.order = order;
	}
	
	int getTransId() {
		return transId;
	}
	
	EditableOrder getOrder() {
		return order;
	}

	@Override
	public void handle(QUIKResponse response) {
		if ( ! response.isFinal() ) return;
		QUIKEditableTerminal term = (QUIKEditableTerminal) order.getTerminal();
		synchronized ( order ) {
			Transaction t = order.getSystemInfo().getCancellation();
			t.setResponse(response);
			t.setResponseTime();
			if ( response.isError() &&
					order.getStatus() == OrderStatus.CANCEL_SENT )
			{
				order.setLastChangeTime(term.getCurrentTime());
				changeStatus(OrderStatus.CANCEL_FAILED);
			}
			Object args[] = { response, t.getLatency() };
			logger.debug("{}, latency={}", args);
		}
		removeHandler();
	}
	
	/**
	 * Инициировать снятие заявки.
	 */
	public void cancelOrder() throws OrderException {
		try {
			Long orderId = ((QUIKResponse) order.getSystemInfo()
					.getRegistration().getResponse()).getOrderId();
			String trspec = "TRANS_ID=" + transId
				+ "; ACTION=KILL_ORDER; ORDER_KEY=" + orderId + "; CLASSCODE="
				+ order.getSecurityDescriptor().getClassCode();
			order.getSystemInfo().getCancellation().setRequest(trspec);
			order.getSystemInfo().getCancellation().setRequestTime();
			send(trspec);
			changeStatus(OrderStatus.CANCEL_SENT);
		} catch ( OrderException e ) {
			changeStatus(OrderStatus.CANCEL_FAILED);
			removeHandler();
			throw e;
		}
	}
	
	/**
	 * Отправить транзакцию.
	 * <p>
	 * @param trspec спецификация транзакции
	 * @throws OrderException
	 */
	private final void send(String trspec) throws OrderException {
		try {
			getClient().send(trspec);
		} catch ( T2QException e ) {
			throw new OrderException(e);
		}
	}

	/**
	 * Удалить этот обработчик из пула.
	 */
	private final void removeHandler() {
		getClient().removeHandler(transId);
	}
	
	/**
	 * Ярлык подключения к API.
	 * <p>
	 * @return экземпляр подключения
	 */
	private final QUIKClient getClient() {
		return ((QUIKEditableTerminal) order.getTerminal()).getClient();
	}
	
	/**
	 * Сменить статус заявки и генерировать события.
	 * <p>
	 * @param status новый статус
	 */
	private final void changeStatus(OrderStatus status) {
		order.setStatus(status);
		order.fireChangedEvent();
		order.resetChanges();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CancelHandler.class ) {
			return false;
		}
		CancelHandler o = (CancelHandler) other;
		return new EqualsBuilder()
			.appendSuper(o.order == order)
			.append(o.transId, transId)
			.isEquals();
	}

}
