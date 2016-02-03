package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.Container;
import ru.prolib.aquila.core.data.ContainerImpl;
import ru.prolib.aquila.core.data.SecurityField;

/**
 * Security implementation.
 */
public class SecurityImpl extends ContainerImpl implements EditableSecurity {
	private static final int[] TOKENS_FOR_AVAILABILITY = {
		SecurityField.DISPLAY_NAME,
		SecurityField.SCALE,
		SecurityField.LOT_SIZE,
		SecurityField.TICK_SIZE,
		SecurityField.TICK_VALUE
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
	
	private final EventType onSessionUpdate;
	private final Symbol symbol;
	private Terminal terminal;
	
	private static String getID(Terminal terminal, Symbol symbol, String suffix) {
		return String.format("%s.%s.%s", terminal.getTerminalID(), symbol, suffix);
	}
	
	private String getID(String suffix) {
		return getID(terminal, symbol, suffix);
	}
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(getID(suffix));
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
		this.onSessionUpdate = newEventType("SECURITY.SESSION_UPDATE");
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
	
}
