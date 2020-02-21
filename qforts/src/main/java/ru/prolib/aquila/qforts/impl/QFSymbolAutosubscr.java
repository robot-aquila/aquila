package ru.prolib.aquila.qforts.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.SymbolDataService;

public class QFSymbolAutosubscr implements QFOrderTracker {
	static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFSymbolAutosubscr.class);
	}
	
	public static class Node {
		private final Lock nodeLock;
		private final SymbolDataService dataService;
		private final Symbol symbol;
		private final Position position;
		private long orderCount;
		private SubscrHandler handler;
		
		public Node(SymbolDataService dataService, Symbol symbol, Position position) {
			this.nodeLock = new ReentrantLock();
			this.dataService = dataService;
			this.symbol = symbol;
			this.position = position;
			this.orderCount = 0;
		}
		
		public void increment() {
			nodeLock.lock();
			try {
				orderCount ++;
				if ( handler == null ) {
					handler = dataService.onSubscribe(symbol, MDLevel.L1);
					//logger.debug("Subscribed for L1 of symbol {} (order count is {})", symbol, orderCount);
				}
			} finally {
				nodeLock.unlock();
			}
		}
		
		public void decrement() {
			nodeLock.lock();
			try {
				orderCount --;
				if ( orderCount <= 0 && handler != null) {
					CDecimal cur_pos = position.getCurrentVolume();
					//Object args[] = { symbol, orderCount, cur_pos };
					if ( cur_pos == null || cur_pos.toAbstract().compareTo(CDecimalBD.ZERO) == 0 ) {
						handler.close();
						handler = null;
						//logger.debug("Subscription of symbol {} is closed (order count is {}, cur.pos is {})", args);
					} else {
						//logger.debug("Keep subscription of symbol {} (order count is {}, cur.pos is {})", args);
					}
				}
			} finally {
				nodeLock.unlock();
			}
		}
		
	}
	
	private final Lock lock;
	private final SymbolDataService dataService;
	private final Map<Symbol, Node> nodes;
	
	public QFSymbolAutosubscr(SymbolDataService dataService, Lock lock, Map<Symbol, Node> nodes) {
		this.dataService = dataService;
		this.lock = lock;
		this.nodes = nodes;
	}
	
	public QFSymbolAutosubscr(SymbolDataService dataService) {
		this(dataService, new ReentrantLock(), new HashMap<>());
	}
	
	protected Node getNode(Order order) {
		Symbol symbol = order.getSymbol();
		Position position = order.getPosition();
		lock.lock();
		try {
			Node node = nodes.get(symbol);
			if ( node == null ) {
				node = new Node(dataService, symbol, position);
				nodes.put(symbol, node);
			}
			return node;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void startTrackingOrder(Order order) {
		getNode(order).increment();
	}

	@Override
	public void stopTrackingOrder(Order order) {
		getNode(order).decrement();
	}

}
