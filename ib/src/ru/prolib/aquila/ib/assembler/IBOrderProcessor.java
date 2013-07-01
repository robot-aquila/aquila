package ru.prolib.aquila.ib.assembler;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderProcessor;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.Cache;

/**
 * Драйвер выставления заявок через IB API.
 */
public class IBOrderProcessor implements OrderProcessor {
	private static final Logger logger;
	private static final Map<OrderDirection, String> dirs;
	
	static {
		logger = LoggerFactory.getLogger(IBOrderProcessor.class);
		dirs = new Hashtable<OrderDirection, String>();
		dirs.put(OrderDirection.BUY, "BUY");
		dirs.put(OrderDirection.SELL, "SELL");
	}
	
	private final IBEditableTerminal terminal;
	
	public IBOrderProcessor(IBEditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	IBEditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public void cancelOrder(Order order) throws OrderException {
		getClient().cancelOrder(order.getId().intValue());
		logger.debug("Cancel order: {}", order.getId());
	}

	@Override
	public void placeOrder(Order order) throws OrderException {
		OrderType type = order.getType();
		IBClient client = getClient();
		if ( type == OrderType.MARKET ) {
			com.ib.client.Order o = new com.ib.client.Order();
			o.m_action = dirs.get(order.getDirection());
			o.m_totalQuantity = order.getQty().intValue();
			o.m_orderType = "MKT";
			int reqId = client.nextReqId();
			terminal.registerPendingOrder(reqId, (EditableOrder) order);
			client.placeOrder(reqId, getCache()
					.getContract(order.getSecurityDescriptor())
					.getDefaultContract(), o);
			logger.debug("Place order: {}", reqId);
			
		} else {
			throw new OrderException("Type unsupported: " + order.getType());
		}
	}
	
	/**
	 * Получить экземпляр подключения к IB API.
	 * <p>
	 * @return клиентское подключение
	 */
	private IBClient getClient() {
		return terminal.getClient();
	}
	
	/**
	 * Получить фасад кэша данных.
	 * <p>
	 * @return кэш данных
	 */
	private Cache getCache() {
		return terminal.getCache();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBOrderProcessor.class ) {
			return false;
		}
		IBOrderProcessor o = (IBOrderProcessor) other;
		return terminal == o.terminal;
	}

}
