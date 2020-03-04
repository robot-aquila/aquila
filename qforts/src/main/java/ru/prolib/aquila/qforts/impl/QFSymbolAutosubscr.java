package ru.prolib.aquila.qforts.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QFSymbolAutosubscr implements QFOrderTracker {
	
	public enum FeedStatus {
		NOT_REQUIRED,
		MAX_DETAILS,
		LESS_DETAILS
	}
	
	static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFSymbolAutosubscr.class);
	}
	
	public interface Action {
		void change(Symbol symbol, FeedStatus old_status, FeedStatus new_status);
	}
	
	public static class ANode {
		private final Action action;
		private final Symbol symbol;
		private final Set<Account> openPositions;
		private long orderCount;
		
		public ANode(Symbol symbol, Action action, Set<Account> open_positions) {
			this.action = action;
			this.symbol = symbol;
			this.openPositions = open_positions;
		}
		
		public ANode(Symbol symbol, Action action) {
			this(symbol, action, new HashSet<>());
		}
		
		FeedStatus currentStatus() {
			if ( orderCount > 0 ) {
				return FeedStatus.MAX_DETAILS;
			}
			if ( openPositions.size() > 0 ) {
				return FeedStatus.LESS_DETAILS;
			}
			return FeedStatus.NOT_REQUIRED;
		}
		
		/**
		 * Increment order counter.
		 * <p>
		 * @param order - an order instance
		 */
		public void increment(Order order) {
			FeedStatus old_status = currentStatus();
			orderCount ++;
			if ( orderCount == 1 ) {
				action.change(symbol, old_status, currentStatus());
			}
		}
		
		/**
		 * Decrement order counter.
		 * <p>
		 * @param order - an order instance
		 * @return true if it was last order and position zero. Actually true mean that node can be removed.
		 */
		public boolean decrement(Order order) {
			FeedStatus old_status = currentStatus(), new_status = old_status;
			if ( old_status != FeedStatus.MAX_DETAILS ) {
				throw new IllegalStateException("Unexpected call in status: " + old_status);
			}
			orderCount --;
			if ( orderCount <= 0 ) {
				CDecimal cur_pos = order.getPosition().getCurrentVolume();
				if ( cur_pos == null || cur_pos.toAbstract().compareTo(CDecimalBD.ZERO) == 0 ) {
					openPositions.remove(order.getAccount());
				} else {
					openPositions.add(order.getAccount());
				}
				action.change(symbol, old_status, new_status = currentStatus());
			}
			return new_status == FeedStatus.NOT_REQUIRED;
		}
		
	}
	
	private final Action action;
	private final Map<Symbol, ANode> nodes;
	
	public QFSymbolAutosubscr(Action action, Map<Symbol, ANode> nodes) {
		this.action = action;
		this.nodes = nodes;
	}
	
	public QFSymbolAutosubscr(Action action) {
		this(action, new HashMap<>());
	}
		
	protected ANode getNode(Order order) {
		Symbol symbol = order.getSymbol();
		ANode node = nodes.get(symbol);
		if ( node == null ) {
			node = new ANode(symbol, action);
			nodes.put(symbol, node);
		}
		return node;
	}

	@Override
	public void startTrackingOrder(Order order) {
		getNode(order).increment(order);
	}

	@Override
	public void stopTrackingOrder(Order order) {
		ANode node = getNode(order);
		if ( node.decrement(order) ) {
			nodes.remove(node.symbol);
		}
	}

}
