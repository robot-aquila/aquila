package ru.prolib.aquila.ib.subsys.order;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.getter.IBGetSecurityDescriptorContract;
import ru.prolib.aquila.ib.subsys.api.IBClient;

/**
 * Обработчик заявок через IB API.
 * <p>
 * 2012-12-11<br>
 * $Id: IBOrderProcessor.java 490 2013-02-05 19:42:02Z whirlwind $
 */
@Deprecated
public class IBOrderProcessor implements OrderProcessor {
	private final EditableTerminal terminal;
	private final IBClient client;
	private final Counter transNumerator;
	private final G<Contract> gSecDescr2Contract;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param client экземпляр клиента IB API
	 * @param transNumerator нумератор транзакций
	 * @param gSecDescr2Contract геттер контракта на основе дескр. инструмента
	 */
	public IBOrderProcessor(EditableTerminal terminal, IBClient client,
			Counter transNumerator, G<Contract> gSecDescr2Contract)
	{
		super();
		this.terminal = terminal;
		this.client = client;
		this.transNumerator = transNumerator;
		this.gSecDescr2Contract = gSecDescr2Contract;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Конструктор создает геттер контракта типа
	 * {@link IBGetSecurityDescriptorContract}. 
	 * <p>
	 * @param terminal терминал
	 * @param client экземпляр клиента IB API
	 * @param transId нумератор транзакций
	 */
	public IBOrderProcessor(EditableTerminal terminal, IBClient client,
			Counter transId)
	{
		this(terminal, client, transId, new IBGetSecurityDescriptorContract());
	}
	
	public EditableTerminal getTerminal() {
		return terminal;
	}

	/**
	 * Получить экземпляр клиента.
	 * <p>
	 * @return экземпляр клиента
	 */
	public IBClient getClient() {
		return client;
	}
	
	/**
	 * Получить нумератор транзакций.
	 * <p>
	 * @return нумератор транзакций
	 */
	public Counter getTransNumerator() {
		return transNumerator;
	}
	
	/**
	 * Получить геттер контракта на основе дескриптора инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<Contract> getSecDescr2ContractConverter() {
		return gSecDescr2Contract;
	}

	@Override
	public void cancelOrder(Order order) throws OrderException {
		try {
			client.cancelOrder(order.getId().intValue());
		} catch ( IBException e ) {
			throw new OrderException(e);
		}
	}
	
	@Override
	public void placeOrder(Order order) throws OrderException {
		Security security = null;
		try {
			security = order.getSecurity();
		} catch ( SecurityException e ) {
			throw new OrderException(e);
		}
		if ( order.getType() == OrderType.MARKET ) {
			com.ib.client.Order ibOrder = new com.ib.client.Order();
			ibOrder.m_action = (order.getDirection() == OrderDirection.BUY
					? "BUY" : "SELL");
			ibOrder.m_totalQuantity = order.getQty().intValue();
			ibOrder.m_orderType = "MKT";
			// TODO: разместить заявку только по номеру контракта нельзя.
			// Обязательно требуется указывать целевую биржу.
			int transId = transNumerator.incrementAndGet();
			try {
				terminal.registerPendingOrder(transId, (EditableOrder) order);
				client.placeOrder(transId, gSecDescr2Contract
						.get(security.getDescriptor()), ibOrder);
			} catch ( IBException e ) {
				throw new OrderException(e);
			} catch ( ValueException e ) {
				throw new OrderException(e);
			}
		} else {
			throw new OrderException("Unsupported: " + order.getType());
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 164921)
			.append(client)
			.append(transNumerator)
			.append(gSecDescr2Contract)
			.append(terminal)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other == null || other.getClass() != IBOrderProcessor.class
			? false : fieldsEquals(other);
	}
	
	protected boolean fieldsEquals(Object other) {
		IBOrderProcessor o = (IBOrderProcessor) other;
		return new EqualsBuilder()
			.append(client, o.client)
			.append(transNumerator, o.transNumerator)
			.append(gSecDescr2Contract, o.gSecDescr2Contract)
			.appendSuper(terminal == o.terminal)
			.isEquals();
	}

}
