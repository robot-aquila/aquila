package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

/**
 * Typical implementation of ALO data provider based on terminal orders.
 */
public class ALODataProviderImpl implements ALODataProvider, EventListener {
	private final Set<Terminal> terminals;
	private final Set<Order> orders;
	private final ALOValidator validator;
	
	ALODataProviderImpl(Set<Terminal> terminals,
						Set<Order> orders,
						ALOValidator validator)
	{
		this.terminals = terminals;
		this.orders = orders;
		this.validator = validator;
	}
	
	public ALODataProviderImpl(ALOValidator validator) {
		this(new HashSet<>(), new HashSet<>(), validator);
	}
	
	public ALODataProviderImpl(ALOValidator validator, Terminal terminal) {
		this(validator);
		addTerminal(terminal);
	}
	
	public ALOValidator getValidator() {
		return validator;
	}
	
	public Set<Terminal> getTrackedTerminals() {
		return terminals;
	}
	
	public Set<Order> getTrackedOrders() {
		return orders;
	}
	
	/**
	 * Add terminal to track orders.
	 * <p>
	 * @param terminal - terminal to add
	 */
	public synchronized void addTerminal(Terminal terminal) {
		if ( ! terminals.contains(terminal) ) {
			terminals.add(terminal);
			terminal.onOrderRegistered().addListener(this);
			terminal.onOrderDone().addListener(this);
			for ( Order order : terminal.getOrders() ) {
				addOrder(order);
			}
			invalidate();
		}
	}
	
	public synchronized void removeTerminal(Terminal terminal) {
		if ( terminals.contains(terminal) ) {
			Set<Order> remove = new HashSet<>();
			for ( Order order : orders ) {
				if ( order.getTerminal() == terminal ) {
					remove.add(order);
				}
			}
			orders.removeAll(remove);
			terminal.onOrderDone().removeListener(this);
			terminal.onOrderRegistered().removeListener(this);
			terminals.remove(terminal);
			invalidate();
		}
	}

	@Override
	public synchronized Collection<ALOData> getOrderVolumes() {
		// TODO: Add data caching
		Map<CDecimal, ALOData> map = new HashMap<>();
		for ( Order order : orders ) {
			if ( ! validator.isValid(order) ) {
				continue;
			}
			CDecimal price = order.getPrice();
			ALOData data = map.get(price);
			CDecimal bVol = CDecimalBD.ZERO, sVol = CDecimalBD.ZERO;
			if ( data != null ) {
				bVol = data.getTotalBuyVolume();
				sVol = data.getTotalSellVolume();
			}
			switch ( order.getAction() ) {
			case BUY:
			case COVER:
				bVol = bVol.add(order.getCurrentVolume());
				break;
			case SELL:
			case SELL_SHORT:
				sVol = sVol.add(order.getCurrentVolume());
				break;
			}
			map.put(price, new ALODataImpl(price, bVol, sVol));
		}
		return map.values();
	}

	@Override
	public synchronized void onEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.onOrderRegistered()) ) {
				if ( addOrder(((OrderEvent) event).getOrder()) ) {
					invalidate();
				}
				
			} else if ( event.isType(terminal.onOrderDone()) ) {
				orders.remove(((OrderEvent) event).getOrder());
				invalidate();
				
			}
		}
	}
	
	private synchronized void invalidate() {
		// Stub method
	}
	
	/**
	 * Add order if it meets all requirements.
	 * <p>
	 * @param order - order to add
	 * @return true if order was added, false - otherwise
	 */
	private synchronized boolean addOrder(Order order) {
		if ( validator.isValid(order) ) {
			orders.add(order);
			return true;
		} else {
			return false;
		}
	}

}
