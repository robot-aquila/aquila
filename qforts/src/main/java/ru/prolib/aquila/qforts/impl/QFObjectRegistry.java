package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QFObjectRegistry {
	private final LinkedHashSet<EditablePortfolio> portfolios;
	private final LinkedHashSet<EditableSecurity> securities;
	private final LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> orders;
	
	QFObjectRegistry(LinkedHashSet<EditablePortfolio> portfolios,
			LinkedHashSet<EditableSecurity> securities,
			LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> orders)
	{
		this.portfolios = portfolios;
		this.securities = securities;
		this.orders = orders;
	}
	
	public QFObjectRegistry() {
		this(new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashMap<>());
	}
	
	public synchronized boolean isRegistered(Portfolio portfolio) {
		return portfolios.contains(portfolio);
	}
	
	public synchronized boolean isRegistered(Security security) {
		return securities.contains(security);
	}
	
	public synchronized boolean isRegistered(Order order) {
		LinkedHashSet<EditableOrder> x = orders.get(order.getSymbol());
		return x != null && x.contains(order);
	}
	
	public synchronized void register(EditablePortfolio portfolio) {
		portfolios.add(portfolio);
	}
	
	public synchronized void register(EditableSecurity security) {
		securities.add(security);
	}
	
	public synchronized void register(EditableOrder order) {
		LinkedHashSet<EditableOrder> dummy = orders.get(order.getSymbol());
		if ( dummy == null ) {
			dummy = new LinkedHashSet<>();
			orders.put(order.getSymbol(), dummy);
		}
		dummy.add(order);
	}
	
	public synchronized List<EditableSecurity> getSecurityList() {
		return new ArrayList<>(securities);
	}
	
	public synchronized List<EditablePortfolio> getPortfolioList() {
		return new ArrayList<>(portfolios);
	}
	
	public synchronized List<EditableOrder> getOrderList(Symbol symbol, CDecimal price) {
		List<EditableOrder> result = new ArrayList<>();
		LinkedHashSet<EditableOrder> dummy = orders.get(symbol);
		if ( dummy != null ) {
			for ( EditableOrder o : dummy ) {
				OrderAction a = o.getAction();
				if ( a == OrderAction.BUY ) {
					if ( price.compareTo(o.getPrice()) <= 0 ) {
						result.add(o);
					}
				} else if ( a == OrderAction.SELL ) {
					if ( price.compareTo(o.getPrice()) >= 0 ) {
						result.add(o);
					}
				} else {
					throw new IllegalStateException("Unsupported order action: " + a);
				}
			}
		}
		return result;
	}
	
	public synchronized void purgeOrder(Order order) {
		LinkedHashSet<EditableOrder> dummy = orders.get(order.getSymbol());
		if ( dummy != null ) {
			dummy.remove(order);
		}
	}

}
