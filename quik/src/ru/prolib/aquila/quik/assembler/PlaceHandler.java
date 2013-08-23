package ru.prolib.aquila.quik.assembler;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.api.*;
import ru.prolib.aquila.t2q.T2QException;

/**
 * Обработчик событий регистрации или отклонения новой заявки.
 * <p>
 * Обрабатывает события QUIK API, генерируемые в связи с обработкой
 * транзакции регистрации заявки.
 */
public class PlaceHandler implements QUIKTransactionHandler {
	private static final Logger logger;
	private static final Map<Direction, String> dir;
	
	static {
		logger = LoggerFactory.getLogger(PlaceHandler.class);
		dir = new Hashtable<Direction, String>();
		dir.put(Direction.BUY,  "B");
		dir.put(Direction.SELL, "S");
	}
	
	protected final EditableOrder order;
	
	public PlaceHandler(EditableOrder order) {
		super();
		this.order = order;
	}
	
	@Override
	public void handle(QUIKResponse response) {
		if ( ! response.isFinal() ) return;
		QUIKEditableTerminal term = (QUIKEditableTerminal) order.getTerminal();
		synchronized ( order ) {
			Transaction t = order.getSystemInfo().getRegistration(); 
			t.setResponse(response);
			t.setResponseTime();
			if ( response.isError() &&
					order.getStatus() == OrderStatus.SENT )
			{
				order.setLastChangeTime(term.getCurrentTime());
				changeStatus(OrderStatus.REJECTED);
			}
			Object args[] = { response, t.getLatency() };
			logger.debug("{}, latency={}", args);
		}
		removeHandler();
	}
	
	/**
	 * Инициировать размещение заявки.
	 */
	public void placeOrder() throws OrderException {
		try {
			Account account = order.getAccount();
			SecurityDescriptor descr = order.getSecurityDescriptor();
			String trspec = "TRANS_ID=" + order.getId()
				+ "; ACTION=NEW_ORDER; CLIENT_CODE=" +
					getClientCode(account)
				+ "; ACCOUNT=" + account.getSubCode2()
				+ "; CLASSCODE=" + descr.getClassCode()
				+ "; SECCODE=" + descr.getCode()
				+ "; OPERATION=" + dir.get(order.getDirection())
				+ "; QUANTITY=" + order.getQty();
			OrderType type = order.getType();
			if ( type == OrderType.MARKET ) {
				trspec += "; TYPE=M; PRICE=0";
	
			} else if ( type == OrderType.LIMIT ) {
				trspec += "; TYPE=L; PRICE="
					+ formatPrice(order, order.getPrice());
				
			} else {
				throw new OrderException("Unsupported order type: " + type);
			}
			order.getSystemInfo().getRegistration().setRequest(trspec);
			order.getSystemInfo().getRegistration().setRequestTime();
			send(trspec);
			changeStatus(OrderStatus.SENT);
			logger.debug(trspec);
			
		} catch ( OrderException e ) {
			changeStatus(OrderStatus.REJECTED);
			removeHandler();
			throw e;
		}
	}
	
	/**
	 * Сформировать код клиента с комментарием.
	 * <p>
	 * @param account торговый счет
	 * @return строка кода клиента с комментарием заявки
	 */
	private String getClientCode(Account account) {
		String code = account.getSubCode();
		String comment = StringUtils.substring(
				StringUtils.replace(order.getComment(), ";", "_"), 0, 20);
		return code + (comment.length() > 0 ? "/" + comment : "");
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
	 * Форматировать цену в соответствии с параметрами инструмента заявки.
	 * <p> 
	 * @param order заявка
	 * @param price значение цены
	 * @return строковое представление цены
	 * @throws OrderException
	 */
	private final String formatPrice(Order order, double price)
		throws OrderException
	{
		try {
			return order.getSecurity().shrinkPrice(price);
		} catch ( SecurityException e ) {
			throw new OrderException(e);
		}
	}
	
	/**
	 * Удалить этот обработчик из пула.
	 */
	private final void removeHandler() {
		getClient().removeHandler(order.getId());
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
		if ( other == null || other.getClass() != PlaceHandler.class ) {
			return false;
		}
		PlaceHandler o = (PlaceHandler) other;
		return o.order == order;
	}

}
