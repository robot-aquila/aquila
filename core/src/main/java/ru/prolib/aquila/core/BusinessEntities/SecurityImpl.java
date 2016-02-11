package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
import ru.prolib.aquila.core.*;

/**
 * Security implementation.
 */
public class SecurityImpl extends ContainerImpl implements EditableSecurity {
	private static final int[] TOKENS_FOR_AVAILABILITY = {
		SecurityField.DISPLAY_NAME,
		SecurityField.SCALE,
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE,
		SecurityField.INITIAL_PRICE
	};
	
	private static final int[] TOKENS_FOR_SESSION_UPDATE = {
		SecurityField.SCALE,
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE,
		SecurityField.INITIAL_MARGIN,
		SecurityField.INITIAL_PRICE,
		SecurityField.OPEN_PRICE,
		SecurityField.HIGH_PRICE,
		SecurityField.LOW_PRICE,
		SecurityField.CLOSE_PRICE,
	};
	
	private final EventType onSessionUpdate, onBestAsk, onBestBid, onLastTrade,
		onMarketDepthUpdate;
	private final Symbol symbol;
	private Terminal terminal;
	private Tick bestAsk, bestBid, lastTrade;
	private MarketDepth marketDepth;
	private Vector<Tick> askQuotes, bidQuotes;
	private int marketDepthLevels = 10;
	
	private static String getID(Terminal terminal, Symbol symbol, String suffix) {
		return String.format("%s.%s.%s", terminal.getTerminalID(), symbol, suffix);
	}
	
	private String getID(String suffix) {
		return getID(terminal, symbol, suffix);
	}
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(getID("SECURITY." + suffix));
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param terminal - owner terminal instance
	 * @param symbol - the symbol
	 * @param controller - controller
	 */
	public SecurityImpl(EditableTerminal terminal, Symbol symbol, ContainerImpl.Controller controller) {
		super(terminal.getEventQueue(), getID(terminal, symbol, "SECURITY"), controller);
		this.terminal = terminal;
		this.symbol = symbol;
		this.onSessionUpdate = newEventType("SESSION_UPDATE");
		this.onBestAsk = newEventType("BEST_ASK");
		this.onBestBid = newEventType("BEST_BID");
		this.onLastTrade = newEventType("LAST_TRADE");
		this.onMarketDepthUpdate = newEventType("MARKET_DEPTH_UPDATE");
		this.askQuotes = new Vector<Tick>();
		this.bidQuotes = new Vector<Tick>();
		this.marketDepth = new MarketDepth(symbol, askQuotes, bidQuotes, 0);
	}
	
	public SecurityImpl(EditableTerminal terminal, Symbol symbol) {
		this(terminal, symbol, new SecurityController());
	}

	@Override
	public Terminal getTerminal() {
		lock.lock();
		try {
			return terminal;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public Integer getLotSize() {
		return getInteger(SecurityField.LOT_SIZE);
	}

	@Override
	public Double getUpperPriceLimit() {
		return getDouble(SecurityField.UPPER_PRICE_LIMIT);
	}

	@Override
	public Double getLowerPriceLimit() {
		return getDouble(SecurityField.LOWER_PRICE_LIMIT);
	}
	
	@Override
	public Double getTickValue() {
		return getDouble(SecurityField.TICK_VALUE);
	}

	@Override
	public Double getTickSize() {
		return getDouble(SecurityField.TICK_SIZE);
	}

	@Override
	public Integer getScale() {
		return getInteger(SecurityField.SCALE);
	}
	
	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public String getDisplayName() {
		return getString(SecurityField.DISPLAY_NAME);
	}
	
	@Override
	public Double getOpenPrice() {
		return getDouble(SecurityField.OPEN_PRICE);
	}

	@Override
	public Double getClosePrice() {
		return getDouble(SecurityField.CLOSE_PRICE);
	}

	@Override
	public Double getHighPrice() {
		return getDouble(SecurityField.HIGH_PRICE);
	}

	@Override
	public Double getLowPrice() {
		return getDouble(SecurityField.LOW_PRICE);
	}
	
	@Override
	public Double getInitialPrice() {
		return getDouble(SecurityField.INITIAL_PRICE);
	}

	@Override
	public Double getInitialMargin() {
		return getDouble(SecurityField.INITIAL_MARGIN);
	}

	@Override
	public EventType onSessionUpdate() {
		return onSessionUpdate;
	}
	
	@Override
	public void close() {
		lock.lock();
		try {
			terminal = null;
			onSessionUpdate.removeListeners();
			onSessionUpdate.removeAlternates();
			onBestAsk.removeListeners();
			onBestAsk.removeAlternates();
			onBestBid.removeListeners();
			onBestBid.removeAlternates();
			onLastTrade.removeListeners();
			onLastTrade.removeAlternates();
			onMarketDepthUpdate.removeListeners();
			onMarketDepthUpdate.removeAlternates();
			super.close();
		} finally {
			lock.unlock();
		}
	}
	
	static class SecurityController implements ContainerImpl.Controller {

		@Override
		public boolean hasMinimalData(Container container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(Container container) {
			SecurityImpl security = (SecurityImpl) container;
			if ( security.atLeastOneHasChanged(TOKENS_FOR_SESSION_UPDATE) ) {
				SecurityEventFactory factory = new SecurityEventFactory(security);
				security.queue.enqueue(security.onSessionUpdate, factory);
			}
		}

		@Override
		public void processAvailable(Container container) {
			
		}
		
	}

	static class SecurityEventFactory implements EventFactory {
		final Security object;
		
		SecurityEventFactory(Security object) {
			this.object = object;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new SecurityEvent(type, object);
		}
	}
	
	static class SecurityTickEventFactory implements EventFactory {
		final Security object;
		final Tick tick;
		
		SecurityTickEventFactory(Security object, Tick tick) {
			this.object = object;
			this.tick = tick;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new SecurityTickEvent(type, object, tick);
		}
		
	}

	@Override
	public void update(Tick tick) {
		lock.lock();
		try {
			switch ( tick.getType() ) {
			case ASK:
				if ( tick == Tick.NULL_ASK ) {
					bestAsk = null;
				} else {
					bestAsk = tick;
				}
				queue.enqueue(onBestAsk, new SecurityTickEventFactory(this, bestAsk));
				break;
			case BID:
				if ( tick == Tick.NULL_BID ) {
					bestBid = null;
				} else {
					bestBid = tick;
				}
				queue.enqueue(onBestBid, new SecurityTickEventFactory(this, bestBid));
				break;
			case TRADE:
				lastTrade = tick;
				queue.enqueue(onLastTrade, new SecurityTickEventFactory(this, lastTrade));
				break;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public EventType onBestBid() {
		return onBestBid;
	}

	@Override
	public EventType onBestAsk() {
		return onBestAsk;
	}

	@Override
	public EventType onLastTrade() {
		return onLastTrade;
	}

	@Override
	public Tick getBestBid() {
		lock.lock();
		try {
			return bestBid;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Tick getBestAsk() {
		lock.lock();
		try {
			return bestAsk;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Tick getLastTrade() {
		lock.lock();
		try {
			return lastTrade;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void update(MDUpdate update) {
		boolean hasAsk = false, hasBid = false;
		lock.lock();
		try {
			MDUpdateHeader header = update.getHeader();
			MDUpdateType updateType = header.getType();
			switch ( updateType ) {
			case UPDATE:
				for ( MDUpdateRecord record : update.getRecords() ) {
					Tick tick = record.getTick();
					switch ( tick.getType() ) {
					case ASK:
						hasAsk = true;
						break;
					case BID:
						hasBid = true;
						break;
					default:
						throw new IllegalArgumentException("Invalid tick type: " + tick.getType());
					}
					switch ( record.getTransactionType() ) {
					case DELETE:
						removeQuote(tick);
						break;
					default:
						replaceQuote(tick);
					}
				}
				
				break;
			case REFRESH:
				askQuotes.clear();
				bidQuotes.clear();
				for ( MDUpdateRecord record : update.getRecords() ) {
					Tick tick = record.getTick();
					switch ( tick.getType() ) {
					case ASK:
						askQuotes.add(tick);
						hasAsk = true;
						break;
					case BID:
						bidQuotes.add(tick);
						hasBid = true;
						break;
					default:
						throw new IllegalArgumentException("Invalid tick type: " + tick.getType());
					}
				}
				break;
			}

			if ( hasAsk || hasBid ) {
				if ( hasAsk ) {
					sortAndDetruncateQuotes(askQuotes, true);
				}
				if ( hasBid ) {
					sortAndDetruncateQuotes(bidQuotes, false);
				}
				marketDepth = new MarketDepth(symbol, askQuotes, bidQuotes,
						header.getTime().toEpochMilli());
				queue.enqueue(onMarketDepthUpdate,
						new SecurityMarketDepthEventFactory(this, marketDepth, update));
			}
		} finally {
			lock.unlock();
		}
	}
	
	static class SecurityMarketDepthEventFactory implements EventFactory {
		private final Security security;
		private final MarketDepth marketDepth;
		private final MDUpdate update;
		
		SecurityMarketDepthEventFactory(Security security, MarketDepth marketDepth, MDUpdate update) {
			this.security = security;
			this.marketDepth = marketDepth;
			this.update = update;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new SecurityMarketDepthEvent(type, security, marketDepth, update);
		}
		
	}
	
	static class TickPriceComparator implements Comparator<Tick> {
		private final int multiplier;
		
		public TickPriceComparator(boolean ascending) {
			super();
			this.multiplier = ascending ? 1 : -1;
		}

		@Override
		public int compare(Tick a, Tick b) {
			double ap = a.getPrice(), bp = b.getPrice();
			return (ap < bp ? -1 : ap == bp ? 0 : 1) * multiplier;
		}

	}
	
	private void sortAndDetruncateQuotes(Vector<Tick> ticks, boolean ascending) {
		Collections.sort(ticks, new TickPriceComparator(ascending));
		ticks.setSize(Math.min(marketDepthLevels, ticks.size()));
	}
	
	/**
	 * Remove tick by price.
	 * <p>
	 * @param tick - the tick to remove
	 * @return true if removed, otherwise false
	 */
	private boolean removeQuote(Tick tick) {
		List<Tick> target = tick.getType() == TickType.ASK ? askQuotes : bidQuotes;
		List<Integer> to_remove = findQuoteWithSamePrice(target, tick);
		for ( int i : to_remove ) {
			target.remove(i);
		}
		return to_remove.size() > 0;
	}
	
	/**
	 * Replace tick by price or add if not exists.
	 * <p>
	 * @param tick - the tick to replace
	 */
	private void replaceQuote(Tick tick) {
		removeQuote(tick);
		List<Tick> target = tick.getType() == TickType.ASK ? askQuotes : bidQuotes;
		target.add(tick);
	}
	
	private List<Integer> findQuoteWithSamePrice(List<Tick> target, Tick expected) {
		List<Integer> to_remove = new Vector<Integer>();
		for ( int i = 0; i < target.size(); i ++ ) {
			Tick other = target.get(i);
			// TODO: may cause comparison problems
			if ( other.getPrice() == expected.getPrice() ) {
				to_remove.add(i);
			}
		}
		return to_remove;
	}

	@Override
	public EventType onMarketDepthUpdate() {
		return onMarketDepthUpdate;
	}

	@Override
	public MarketDepth getMarketDepth() {
		lock.lock();
		try {
			return marketDepth;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	protected EventFactory createEventFactory() {
		return new SecurityEventFactory(this);
	}
	
}
