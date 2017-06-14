package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParams;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParamsBuilder;

/**
 * Security implementation.
 */
public class SecurityImpl extends ObservableStateContainerImpl implements EditableSecurity {
	private static final int[] TOKENS_FOR_AVAILABILITY = {
		SecurityField.DISPLAY_NAME,
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE,
		SecurityField.SETTLEMENT_PRICE
	};
	
	private static final int[] TOKENS_FOR_SESSION_UPDATE = {
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE,
		SecurityField.INITIAL_MARGIN,
		SecurityField.SETTLEMENT_PRICE,
		SecurityField.OPEN_PRICE,
		SecurityField.HIGH_PRICE,
		SecurityField.LOW_PRICE,
		SecurityField.CLOSE_PRICE,
	};
	
	private final EventType onSessionUpdate, onBestAsk, onBestBid, onLastTrade,
		onMarketDepthUpdate;
	private final Symbol symbol;
	private final MDBuilder marketDepthBuilder;
	private Terminal terminal;
	private Tick bestAsk, bestBid, lastTrade;
	
	public SecurityImpl(SecurityParams params) {
		super(params);
		this.terminal = params.getTerminal();
		this.symbol = params.getSymbol();
		final String pfx = params.getID() + ".";
		this.onSessionUpdate = new EventTypeImpl(pfx + "SESSION_UPDATE");
		this.onBestAsk = new EventTypeImpl(pfx + "BEST_ASK");
		this.onBestBid = new EventTypeImpl(pfx + "BEST_BID");
		this.onLastTrade = new EventTypeImpl(pfx + "LAST_TRADE");
		this.onMarketDepthUpdate = new EventTypeImpl(pfx + "MARKET_DEPTH_UPDATE");
		this.marketDepthBuilder = new MDBuilder(symbol);
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param terminal - owner terminal instance
	 * @param symbol - the symbol
	 * @param controller - controller
	 */
	@Deprecated
	public SecurityImpl(EditableTerminal terminal, Symbol symbol, OSCController controller) {
		this(new SecurityParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withSymbol(symbol)
				.withController(controller)
				.buildParams());
	}
	
	@Deprecated
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
	public FDecimal getUpperPriceLimit() {
		return getDecimal(SecurityField.UPPER_PRICE_LIMIT);
	}

	@Override
	public FDecimal getLowerPriceLimit() {
		return getDecimal(SecurityField.LOWER_PRICE_LIMIT);
	}
	
	@Override
	public FMoney getTickValue() {
		return getMoney(SecurityField.TICK_VALUE);
	}

	@Override
	public FDecimal getTickSize() {
		return getDecimal(SecurityField.TICK_SIZE);
	}

	@Override
	public Integer getScale() {
		FDecimal x = getTickSize();
		return x == null ? null : x.getScale();
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
	public FDecimal getSettlementPrice() {
		return getDecimal(SecurityField.SETTLEMENT_PRICE);
	}

	@Override
	public FMoney getInitialMargin() {
		return getMoney(SecurityField.INITIAL_MARGIN);
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
		} finally {
			lock.unlock();
		}
		super.close();
	}
	
	public static class SecurityController implements OSCController {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container) {
			SecurityImpl security = (SecurityImpl) container;
			if ( security.atLeastOneHasChanged(TOKENS_FOR_SESSION_UPDATE) ) {
				SecurityEventFactory factory = new SecurityEventFactory(security);
				security.dispatcher.dispatch(security.onSessionUpdate, factory);
			}
		}

		@Override
		public void processAvailable(ObservableStateContainer container) {
			
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
	public void consume(L1Update update) {
		Tick tick = update.getTick();
		boolean hasAsk = false, hasBid = false, hasTrade = false;
		lock.lock();
		try {
			if ( isClosed() ) {
				return;
			}
			switch ( tick.getType() ) {
			case ASK:
				if ( tick == Tick.NULL_ASK ) {
					bestAsk = tick = null;
				} else {
					bestAsk = tick;
				}
				hasAsk = true;
				break;
			case BID:
				if ( tick == Tick.NULL_BID ) {
					bestBid = tick = null;
				} else {
					bestBid = tick;
				}
				hasBid = true;
				break;
			case TRADE:
				lastTrade = tick;
				hasTrade = true;
				break;
			}
			if ( hasAsk ) {
				dispatcher.dispatch(onBestAsk, new SecurityTickEventFactory(this, tick));
			}
			if ( hasBid ) {
				dispatcher.dispatch(onBestBid, new SecurityTickEventFactory(this, tick));
			}
			if ( hasTrade ) {
				dispatcher.dispatch(onLastTrade, new SecurityTickEventFactory(this, tick));
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
	public void consume(MDUpdate update) {
		MarketDepth md = null;
		lock.lock();
		try {
			if ( isClosed() ) {
				return;
			}
			marketDepthBuilder.setPriceScale(getScale());
			marketDepthBuilder.consume(update);
			md = marketDepthBuilder.getMarketDepth();
			dispatcher.dispatch(onMarketDepthUpdate, new SecurityMarketDepthEventFactory(this, md));
		} finally {
			lock.unlock();
		}
	}
	
	static class SecurityMarketDepthEventFactory implements EventFactory {
		private final Security security;
		private final MarketDepth marketDepth;
		
		SecurityMarketDepthEventFactory(Security security, MarketDepth marketDepth) {
			this.security = security;
			this.marketDepth = marketDepth;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new SecurityMarketDepthEvent(type, security, marketDepth);
		}
		
	}
	
	@Override
	public EventType onMarketDepthUpdate() {
		return onMarketDepthUpdate;
	}

	@Override
	public MarketDepth getMarketDepth() {
		return marketDepthBuilder.getMarketDepth();
	}
	
	@Override
	protected EventFactory createEventFactory() {
		return new SecurityEventFactory(this);
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
	}
	
}
