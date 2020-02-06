package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QFObjectRegistry implements IQFObjectRegistry {
	
	static class OrderInfo {
		private final Symbol symbol;
		private final CDecimal price;
		private final OrderAction action;
		
		OrderInfo(Symbol symbol, CDecimal price, OrderAction action) {
			this.symbol = symbol;
			this.price = price;
			this.action = action;
		}
	}
	
	private final Object monitor = new Object();
	private final LinkedHashSet<EditablePortfolio> portfolios;
	private final LinkedHashSet<EditableSecurity> securities;
	private final LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> orders;
	private final Map<EditableOrder, OrderInfo> cachedOrderInfo;
	
	QFObjectRegistry(LinkedHashSet<EditablePortfolio> portfolios,
			LinkedHashSet<EditableSecurity> securities,
			LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> orders)
	{
		this.portfolios = portfolios;
		this.securities = securities;
		this.orders = orders;
		this.cachedOrderInfo = new HashMap<>();
	}
	
	public QFObjectRegistry() {
		this(new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashMap<>());
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#isRegistered(ru.prolib.aquila.core.BusinessEntities.Portfolio)
	 */
	@Override
	public boolean isRegistered(Portfolio portfolio) {
		synchronized ( monitor ) {
			return portfolios.contains(portfolio);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#isRegistered(ru.prolib.aquila.core.BusinessEntities.Security)
	 */
	@Override
	@Deprecated
	public boolean isRegistered(Security security) {
		synchronized ( monitor ) {
			return securities.contains(security);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#isRegistered(ru.prolib.aquila.core.BusinessEntities.Order)
	 */
	@Override
	public boolean isRegistered(Order order) {
		synchronized ( monitor ) {
			return cachedOrderInfo.containsKey(order);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#register(ru.prolib.aquila.core.BusinessEntities.EditablePortfolio)
	 */
	@Override
	public void register(EditablePortfolio portfolio) {
		synchronized ( monitor ) {
			portfolios.add(portfolio);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#register(ru.prolib.aquila.core.BusinessEntities.EditableSecurity)
	 */
	@Override
	@Deprecated
	public void register(EditableSecurity security) {
		synchronized ( monitor ) {
			securities.add(security);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#register(ru.prolib.aquila.core.BusinessEntities.EditableOrder)
	 */
	@Override
	public void register(EditableOrder order) {
		Symbol symbol;
		OrderInfo order_info;
		order.lock();
		try {
			symbol = order.getSymbol();
			order_info = new OrderInfo(symbol, order.getPrice(), order.getAction());
		} finally {
			order.unlock();
		}
		synchronized ( monitor ) {
			LinkedHashSet<EditableOrder> symbol_orders = orders.get(symbol);
			if ( symbol_orders == null ) {
				symbol_orders = new LinkedHashSet<>();
				orders.put(symbol, symbol_orders);
			}
			symbol_orders.add(order);
			cachedOrderInfo.put(order, order_info);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#getSecurityList()
	 */
	@Override
	public List<EditableSecurity> getSecurityList() {
		synchronized ( monitor ) {
			return new ArrayList<>(securities);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#getPortfolioList()
	 */
	@Override
	public List<EditablePortfolio> getPortfolioList() {
		synchronized ( monitor ) {
			return new ArrayList<>(portfolios);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#getOrderList(ru.prolib.aquila.core.BusinessEntities.Symbol, ru.prolib.aquila.core.BusinessEntities.CDecimal)
	 */
	@Override
	public List<EditableOrder> getOrderList(Symbol symbol, CDecimal price) {
		synchronized ( monitor ) {
			List<EditableOrder> result = new ArrayList<>();
			LinkedHashSet<EditableOrder> symbol_order = orders.get(symbol);
			if ( symbol_order != null ) {
				for ( EditableOrder order : symbol_order ) {
					OrderInfo order_info = cachedOrderInfo.get(order);
					if ( order_info.action == OrderAction.BUY ) {
						if ( price.compareTo(order_info.price) <= 0 ) {
							result.add(order);
						}
					} else if ( order_info.action == OrderAction.SELL ) {
						if ( price.compareTo(order_info.price) >= 0 ) {
							result.add(order);
						}
					} else {
						throw new IllegalStateException("Unsupported order action: " + order_info.action);
					}
				}
			}
			return result;
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.qforts.impl.IQFObjectRegistry#purgeOrder(ru.prolib.aquila.core.BusinessEntities.Order)
	 */
	@Override
	public void purgeOrder(Order order) {
		synchronized ( monitor ) {
			OrderInfo order_info = cachedOrderInfo.remove(order);
			if ( order_info == null ) {
				return;
			}
			LinkedHashSet<EditableOrder> symbol_orders = orders.get(order_info.symbol);
			if ( symbol_orders != null ) {
				symbol_orders.remove(order);
			}
		}
	}

}
