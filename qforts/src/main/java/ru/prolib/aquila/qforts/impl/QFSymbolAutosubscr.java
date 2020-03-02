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
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.SymbolDataService;

/**
 * То что здесь сейчас - оно не будет работать как надо.
 * 
 * Возможные состояния:
 * 
 * 1) Когда есть активные заявки, всегда необходима тиковая точность.
 * 2) Активных заявок может не быть, но может быть открытая позиция, которая требует
 * регулярного пересчета. 
 * 
 * Значит для ноды нужно считать сколько активных заявок и сколько открытых позиций по
 * инструменту. Позиции могут быть открыты по разным портфелям. По этому та нода, что
 * сейчас она работать не будет.
 * 
 * Определим список возможныз состояний инструмента:
 * 
 * NOT_REQUIRED - подача данных не требуется
 * MAX_DETAIS  - требуется подача данных с максимальной детализацией
 * LESS_DETAILS - требуется подача данных пониженной точности
 * 
 * По поводу позиций. Проверять статус позиции нужно на декременте счетчика ноды.
 * Считать позиции нужно не счетчиком, а через сет, в который складывать аккаунты,
 * на которых открыта позиция по инструменту. Добавлять в него, если после удаления
 * заявки из обзора позиция ненулевая и удалять, если нулевая. Таким образом,
 * при удалении заявки результатом может быть NOT_REQUIRED или LESS_DETAILS.
 * 
 * ВАЖНО! Если при подачи сюда заявки поза еще не будет пересчитана, то схема
 * работать не будет. Как бы сформулировать тест на это дело? 
 * 
 */
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
	
	@Deprecated
	public static class Node {
		private final SymbolDataService dataService;
		private final Symbol symbol;
		private final Position position;
		private long orderCount;
		private SubscrHandler handler;
		
		public Node(SymbolDataService dataService, Symbol symbol, Position position) {
			this.dataService = dataService;
			this.symbol = symbol;
			this.position = position;
			this.orderCount = 0;
		}
		
		public void increment() {
			orderCount ++;
			if ( handler == null ) {
				handler = dataService.onSubscribe(symbol, MDLevel.L1);
				//logger.debug("Subscribed for L1 of symbol {} (order count is {})", symbol, orderCount);
			}
		}
		
		public void decrement() {
			orderCount --;
			if ( orderCount <= 0 && handler != null ) {
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
		}
		
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
	
	public QFSymbolAutosubscr(SymbolDataService dataService, Map<Symbol, ANode> nodes) {
		this(new QFSymbolAutosubscrActionSDS(dataService), nodes);
	}
	
	public QFSymbolAutosubscr(SymbolDataService dataService) {
		this(dataService, new HashMap<>());
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
