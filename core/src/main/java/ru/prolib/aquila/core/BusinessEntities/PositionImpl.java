package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;

/**
 * Market position.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionImpl.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImpl extends ContainerImpl implements EditablePosition {
	private static final int[] TOKENS_FOR_AVAILABILITY = {
		PositionField.CURRENT_VOLUME
	};
	
	private final Symbol symbol;
	private final Account account;
	private Terminal terminal;
	private final EventType onPositionChange, onCurrentPriceChange;
	
	private static String getID(Terminal terminal, Account account,
			Symbol symbol, String suffix)
	{
		return String.format("%s.%s[%s].%s", terminal.getTerminalID(),
				account, symbol, suffix);
	}
	
	private String getID(String suffix) {
		return getID(terminal, account, symbol, suffix);
	}
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(getID(suffix));
	}

	public PositionImpl(EditableTerminal terminal, Account account,
			Symbol symbol, ContainerImpl.Controller controller)
	{
		super(terminal.getEventQueue(), getID(terminal, account, symbol, "POSITION"), controller);
		this.terminal = terminal;
		this.account = account;
		this.symbol = symbol;
		this.onPositionChange = newEventType("POSITION.POSITION_CHANGE");
		this.onCurrentPriceChange = newEventType("POSITION.CURRENT_PRICE_CHANGE");
	}
	
	public PositionImpl(EditableTerminal terminal, Account account, Symbol symbol) {
		this(terminal, account, symbol, new PositionController());
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
	public Double getVariationMargin() {
		return getDouble(PositionField.VARIATION_MARGIN);
	}

	@Override
	public Long getCurrentVolume() {
		return getLong(PositionField.CURRENT_VOLUME);
	}

	@Override
	public Double getCurrentPrice() {
		return getDouble(PositionField.CURRENT_PRICE);
	}
	
	@Override
	public Double getOpenPrice() {
		return getDouble(PositionField.OPEN_PRICE);
	}
	
	@Override
	public void close() {
		lock.lock();
		try {
			terminal = null;
			onCurrentPriceChange.removeListeners();
			onCurrentPriceChange.removeAlternates();
			onPositionChange.removeListeners();
			onPositionChange.removeAlternates();
			super.close();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	protected EventFactory createEventFactory() {
		return new PositionEventFactory(this);
	}
	
	static class PositionController implements ContainerImpl.Controller {
		
		@Override
		public boolean hasMinimalData(Container container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(Container container) {
			PositionImpl position = (PositionImpl) container;
			PositionEventFactory factory = new PositionEventFactory(position);
			if ( position.hasChanged(PositionField.CURRENT_VOLUME) ) {
				position.queue.enqueue(position.onPositionChange, factory);
			}
			if ( position.hasChanged(PositionField.CURRENT_PRICE) ) {
				position.queue.enqueue(position.onCurrentPriceChange, factory);					
			}
		}

		@Override
		public void processAvailable(Container container) {
			
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

}
