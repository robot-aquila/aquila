package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
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
	public FMoney getUsedMargin() {
		return getMoney(PositionField.USED_MARGIN);
	}

	@Override
	public Long getCurrentVolume() {
		return getLong(PositionField.CURRENT_VOLUME);
	}

	@Override
	public FDecimal getCurrentPrice() {
		return getDecimal(PositionField.CURRENT_PRICE);
	}
	
	@Override
	public FDecimal getOpenPrice() {
		return getDecimal(PositionField.OPEN_PRICE);
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
	protected EventFactory createEventFactory() {
		return new PositionEventFactory(this);
	}
	
	public static class PositionController implements OSCController {
		
		@Override
		public boolean hasMinimalData(ObservableStateContainer container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container) {
			PositionImpl position = (PositionImpl) container;
			PositionEventFactory factory = new PositionEventFactory(position);
			if ( position.hasChanged(PositionField.CURRENT_VOLUME) ) {
				position.dispatcher.dispatch(position.onPositionChange, factory);
			}
			if ( position.hasChanged(PositionField.CURRENT_PRICE) ) {
				position.dispatcher.dispatch(position.onCurrentPriceChange, factory);					
			}
		}

		@Override
		public void processAvailable(ObservableStateContainer container) {
			
		}
		
	}
	
	static class PositionEventFactory implements EventFactory {
		private final Position position;
		
		PositionEventFactory(Position position) {
			super();
			this.position = position;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new PositionEvent(type, position);
		}
		
	}

	@Override
	public FMoney getProfitAndLoss() {
		return getMoney(PositionField.PROFIT_AND_LOSS);
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
