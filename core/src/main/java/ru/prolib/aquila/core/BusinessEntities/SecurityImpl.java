package ru.prolib.aquila.core.BusinessEntities;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParams;

/**
 * Security implementation.
 */
public class SecurityImpl extends ObservableStateContainerImpl implements EditableSecurity {
	static final int[] TOKENS_FOR_AVAILABILITY = {
		SecurityField.DISPLAY_NAME,
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE,
	};
	
	static final int[] TOKENS_FOR_SESSION_UPDATE = {
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE,
		SecurityField.INITIAL_MARGIN,
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
	public CDecimal getLotSize() {
		return getCDecimal(SecurityField.LOT_SIZE);
	}

	@Override
	public CDecimal getUpperPriceLimit() {
		return getCDecimal(SecurityField.UPPER_PRICE_LIMIT);
	}

	@Override
	public CDecimal getLowerPriceLimit() {
		return getCDecimal(SecurityField.LOWER_PRICE_LIMIT);
	}
	
	@Override
	public CDecimal getTickValue() {
		return getCDecimal(SecurityField.TICK_VALUE);
	}

	@Override
	public CDecimal getTickSize() {
		return getCDecimal(SecurityField.TICK_SIZE);
	}

	@Override
	public Integer getScale() {
		CDecimal x = getTickSize();
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
	public CDecimal getOpenPrice() {
		return getCDecimal(SecurityField.OPEN_PRICE);
	}

	@Override
	public CDecimal getClosePrice() {
		return getCDecimal(SecurityField.CLOSE_PRICE);
	}

	@Override
	public CDecimal getHighPrice() {
		return getCDecimal(SecurityField.HIGH_PRICE);
	}

	@Override
	public CDecimal getLowPrice() {
		return getCDecimal(SecurityField.LOW_PRICE);
	}
	
	@Override
	public CDecimal getSettlementPrice() {
		return getCDecimal(SecurityField.SETTLEMENT_PRICE);
	}

	@Override
	public CDecimal getInitialMargin() {
		return getCDecimal(SecurityField.INITIAL_MARGIN);
	}
	
	@Override
	public Instant getExpirationTime() {
		return this.getInstant(SecurityField.EXPIRATION_TIME);
	}
	
	@Override
	public CDecimal getLastPrice() {
		Tick tick = getLastTrade();
		return tick == null ? null : tick.getPrice();
	}

	@Override
	public EventType onSessionUpdate() {
		return onSessionUpdate;
	}
	
	@Override
	public void close() {
		super.close();
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
	}
	
	public static class SecurityController implements OSCController {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container, Instant time) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container, Instant time) {
			SecurityImpl security = (SecurityImpl) container;
			if ( security.atLeastOneHasChanged(TOKENS_FOR_SESSION_UPDATE) ) {
				security.dispatcher.dispatch(security.onSessionUpdate, security.createEventFactory(time));
			}
		}

		@Override
		public void processAvailable(ObservableStateContainer container, Instant time) {
			
		}

		@Override
		public Instant getCurrentTime(ObservableStateContainer container) {
			SecurityImpl s = (SecurityImpl) container;
			return s.isClosed() ? null : s.getTerminal().getCurrentTime();
		}
		
	}

	static class SecurityEventFactory implements EventFactory {
		private final Security object;
		private final Instant time;
		
		SecurityEventFactory(Security object, Instant time) {
			this.object = object;
			this.time = time;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new SecurityEvent(type, object, time);
		}
	}
	
	static class SecurityUpdateEventFactory extends OSCUpdateEventFactory {
		private final Security security;

		public SecurityUpdateEventFactory(Security security, Instant time,
				Map<Integer, Object> old_values, Map<Integer, Object> new_values)
		{
			super(security, time, old_values, new_values);
			this.security = security;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new SecurityUpdateEvent(type, security, time, oldValues, newValues);
		}
		
	}
	
	static class SecurityTickEventFactory implements EventFactory {
		private final Security object;
		private final Instant time;
		private final Tick tick;
		
		SecurityTickEventFactory(Security object, Instant time, Tick tick) {
			this.object = object;
			this.time = time;
			this.tick = tick;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new SecurityTickEvent(type, object, time, tick);
		}
		
	}
	
	static class SecurityMarketDepthEventFactory implements EventFactory {
		private final Security security;
		private final Instant time;
		private final MarketDepth marketDepth;
		
		SecurityMarketDepthEventFactory(Security security, Instant time, MarketDepth marketDepth) {
			this.security = security;
			this.time = time;
			this.marketDepth = marketDepth;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new SecurityMarketDepthEvent(type, security, time, marketDepth);
		}
		
	}

	@Override
	public void consume(L1Update update) {
		Tick tick = update.getTick();
		Instant time;
		EventType eventType;
		lock.lock();
		try {
			if ( isClosed() ) {
				return;
			}
			time = getController().getCurrentTime(this);
			switch ( tick.getType() ) {
			case ASK:
				if ( tick == Tick.NULL_ASK ) {
					bestAsk = tick = null;
				} else {
					bestAsk = tick;
				}
				eventType = onBestAsk;
				break;
			case BID:
				if ( tick == Tick.NULL_BID ) {
					bestBid = tick = null;
				} else {
					bestBid = tick;
				}
				eventType = onBestBid;
				break;
			case TRADE:
				lastTrade = tick;
				eventType = onLastTrade;
				break;
			default:
				throw new IllegalArgumentException("Unknown tick type: " + tick);	
			}
		} finally {
			lock.unlock();
		}
		dispatcher.dispatch(eventType, createTickEventFactory(time, tick));
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
		Instant time;
		MarketDepth md = null;
		lock.lock();
		try {
			if ( isClosed() ) {
				return;
			}
			time = getController().getCurrentTime(this);
			marketDepthBuilder.consume(update);
			md = marketDepthBuilder.getMarketDepth();
		} finally {
			lock.unlock();
		}
		dispatcher.dispatch(onMarketDepthUpdate, createMDEventFactory(time, md));
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
	protected EventFactory createEventFactory(Instant time) {
		return new SecurityEventFactory(this, time);
	}
	
	@Override
	protected EventFactory createEventFactory(Instant time,
			Map<Integer, Object> old_vals,
			Map<Integer, Object> new_vals)
	{
		return new SecurityUpdateEventFactory(this, time, old_vals, new_vals);	
	}
	
	/**
	 * Create an event factory instance to produce Depth of Market update events.
	 * <p>
	 * Override this method to produce specific events.
	 * <p>
	 * @param time - time of event
	 * @param md - depth of market
	 * @return event factory
	 */
	protected EventFactory createMDEventFactory(Instant time, MarketDepth md) {
		return new SecurityMarketDepthEventFactory(this, time, md);
	}
	
	/**
	 * Create an event factory to produce tick data events.
	 * <p>
	 * @param time - time of event
	 * @param tick - tick data
	 * @return event factory
	 */
	protected EventFactory createTickEventFactory(Instant time, Tick tick) {
		return new SecurityTickEventFactory(this, time, tick);
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public CDecimal round(CDecimal price) {
		CDecimal tick_size = getTickSize();
		return price.divideExact(tick_size, 0).multiply(tick_size);
	}

	@Override
	public CDecimal priceToValue(CDecimal price, CDecimal quantity) {
		CDecimal tick_size = getTickSize(), tick_val = getTickValue();
		CDecimal num_ticks = price.divideExact(tick_size, 0, RoundingMode.UNNECESSARY);
		return tick_val.multiply(num_ticks).multiply(quantity);
	}

	@Override
	public CDecimal priceToValue(CDecimal price) {
		return priceToValue(price, CDecimalBD.of(1L));
	}

	@Override
	public CDecimal priceToValueWR(CDecimal price, CDecimal quantity) {
		return priceToValue(round(price), quantity);
	}

	@Override
	public CDecimal priceToValueWR(CDecimal price) {
		return priceToValueWR(price, CDecimalBD.of(1L));
	}
	
}
