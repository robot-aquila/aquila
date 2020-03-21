package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PositionParams;

/**
 * Market position.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionImpl.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImpl extends ObservableStateContainerImpl implements EditablePosition {
	private static final int[] TOKENS_FOR_AVAILABILITY = {
		PositionField.CURRENT_VOLUME,
		PositionField.CURRENT_PRICE,
		PositionField.OPEN_PRICE,
		PositionField.PROFIT_AND_LOSS,
		PositionField.USED_MARGIN
	};
	
	private final Symbol symbol;
	private final Account account;
	private Terminal terminal;
	private final EventType onPositionChange, onCurrentPriceChange;
	private final Security security;
	private final Portfolio portfolio;
	
	public PositionImpl(PositionParams params) {
		super(params);
		this.terminal = params.getTerminal();
		this.security = params.getSecurity();
		this.portfolio = params.getPortfolio();
		this.account = params.getAccount();
		this.symbol = params.getSymbol();
		final String pfx = params.getID() + ".";
		this.onPositionChange = new EventTypeImpl(pfx + "POSITION_CHANGE");
		this.onCurrentPriceChange = new EventTypeImpl(pfx + "CURRENT_PRICE_CHANGE");
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
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public Account getAccount() {
		return account;
	}

	@Override
	public EventType onPositionChange() {
		return onPositionChange;
	}
	
	@Override
	public EventType onCurrentPriceChange() {
		return onCurrentPriceChange;
	}

	@Override
	public CDecimal getUsedMargin() {
		return getCDecimal(PositionField.USED_MARGIN);
	}

	@Override
	public CDecimal getCurrentVolume() {
		return getCDecimal(PositionField.CURRENT_VOLUME);
	}

	@Override
	public CDecimal getCurrentPrice() {
		return getCDecimal(PositionField.CURRENT_PRICE);
	}
	
	@Override
	public CDecimal getOpenPrice() {
		return getCDecimal(PositionField.OPEN_PRICE);
	}
	
	@Override
	public void close() {
		super.close();
		lock.lock();
		try {
			terminal = null;
			onCurrentPriceChange.removeListeners();
			onCurrentPriceChange.removeAlternates();
			onPositionChange.removeListeners();
			onPositionChange.removeAlternates();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	protected EventFactory createEventFactory(Instant time) {
		return new PositionEventFactory(this, time);
	}
	
	@Override
	protected EventFactory createEventFactory(Instant time, Map<Integer, Object> oldVals, Map<Integer, Object> newVals) {
		return new PositionUpdateEventFactory(this, time, oldVals, newVals);
	}
	
	public static class PositionController implements OSCController {
		
		@Override
		public boolean hasMinimalData(ObservableStateContainer container, Instant time) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container, Instant time) {
			PositionImpl position = (PositionImpl) container;
			EventFactory factory = position.createEventFactory(time);
			if ( position.hasChanged(PositionField.CURRENT_VOLUME) ) {
				position.dispatcher.dispatch(position.onPositionChange, factory);
			}
			if ( position.hasChanged(PositionField.CURRENT_PRICE) ) {
				position.dispatcher.dispatch(position.onCurrentPriceChange, factory);					
			}
		}

		@Override
		public void processAvailable(ObservableStateContainer container, Instant time) {
			
		}

		@Override
		public Instant getCurrentTime(ObservableStateContainer container) {
			PositionImpl p = (PositionImpl) container;
			return p.isClosed() ? null : p.getTerminal().getCurrentTime();
		}
		
	}
	
	static class PositionEventFactory implements EventFactory {
		private final Position position;
		private final Instant time;
		
		PositionEventFactory(Position position, Instant time) {
			super();
			this.position = position;
			this.time = time;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new PositionEvent(type, position, time);
		}
		
	}
	
	static class PositionUpdateEventFactory extends OSCUpdateEventFactory {
		private final Position position;

		public PositionUpdateEventFactory(Position position,
				Instant time,
				Map<Integer, Object> old_values,
				Map<Integer, Object> new_values)
		{
			super(position, time, old_values, new_values);
			this.position = position;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new PositionUpdateEvent(type, position, time, oldValues, newValues);
		}
		
	}

	@Override
	public CDecimal getProfitAndLoss() {
		return getCDecimal(PositionField.PROFIT_AND_LOSS);
	}

	@Override
	public Security getSecurity() {
		return security;
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

}
