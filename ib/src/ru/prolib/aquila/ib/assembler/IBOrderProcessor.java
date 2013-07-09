package ru.prolib.aquila.ib.assembler;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.api.IBClient;

/**
 * Драйвер выставления заявок через IB API.
 */
public class IBOrderProcessor implements OrderProcessor {
	private static final Logger logger;
	private static final Map<Direction, String> dirs;
	
	static {
		logger = LoggerFactory.getLogger(IBOrderProcessor.class);
		dirs = new Hashtable<Direction, String>();
		dirs.put(Direction.BUY, "BUY");
		dirs.put(Direction.SELL, "SELL");
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
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			if ( status.isFinal() || status == OrderStatus.CANCEL_SENT ) {
				return;
			} else if ( status != OrderStatus.ACTIVE ) {
				throw new OrderException("Rejected by status: " + status);
			}
			((IBOrderHandler) getClient().getOrderHandler(order.getId())) 
				.cancelOrder();
		}
	}

	@Override
	public void placeOrder(Order order) throws OrderException {
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			if ( status != OrderStatus.PENDING
				&& status != OrderStatus.CONDITION )
			{
				throw new OrderException("Rejected by status: " + status);
			}
			
			int id = order.getId();
			PlaceOrderRequest req = new PlaceOrderRequest(getContract(order));
			req.getOrder().m_orderId = id;
			req.getOrder().m_action = dirs.get(order.getDirection());
			req.getOrder().m_totalQuantity = order.getQty().intValue();
		
			OrderType type = order.getType();
			if ( type == OrderType.MARKET ) {
				req.getOrder().m_orderType = "MKT";
			
			} else if ( type == OrderType.LIMIT ) {
				req.getOrder().m_orderType = "LMT";
				req.getOrder().m_lmtPrice = order.getPrice(); 
			
			} else {
				throw new OrderException("Unsupported order type: " + type);
				
			}
			
			IBOrderHandler handler = new IBOrderHandler(order, req);
			getClient().setOrderHandler(id, handler);
			handler.placeOrder();
			logger.debug("Place order initiated: {}", id);
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
	 * Получить дескриптор контракта.
	 * <p>
	 * Возвращает дескриптор контракта IB, соответствующий инструменту заявки.
	 * <p>
	 * @return дескриптор контракта
	 */
	private Contract getContract(Order order) {
		return terminal.getCache()
			.getContract(order.getSecurityDescriptor())
			.getDefaultContract();
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
