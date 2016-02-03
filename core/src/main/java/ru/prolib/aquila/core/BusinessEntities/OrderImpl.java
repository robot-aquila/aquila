package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.Container;
import ru.prolib.aquila.core.data.ContainerImpl;
import ru.prolib.aquila.core.data.OrderField;

/**
 * Order.
 */
public class OrderImpl extends ContainerImpl implements EditableOrder {
	private static final int[] TOKENS_FOR_AVAILABILITY = {
		OrderField.ACTION,
		OrderField.TYPE,
		OrderField.STATUS,
		OrderField.INITIAL_VOLUME,
		OrderField.CURRENT_VOLUME,
	};

	private Terminal terminal;
	private final Account account;
	private final Symbol symbol;
	private final long id;
	private final EventType onCancelFailed, onCancelled, onDone, onFailed,
		onFilled, onPartiallyFilled, onRegistered, onRegisterFailed, onDeal;
	
	private static String getID(Terminal terminal, Account account,
			Symbol symbol, long id)
	{
		return String.format("%s.%s[%s].ORDER#%d", terminal.getTerminalID(),
				account, symbol, id);
	}
	
	private static String getID(Terminal terminal, Account account,
			Symbol symbol, long id, String suffix)
	{
		return getID(terminal, account, symbol, id) + "." + suffix;
	}
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(getID(terminal, account, symbol, id, suffix));
	}
	
	public OrderImpl(EditableTerminal terminal, Account account,
			Symbol symbol, long id, ContainerImpl.Controller controller)
	{
		super(terminal.getEventQueue(), getID(terminal, account, symbol, id), controller);
		this.terminal = terminal;
		this.account = account;
		this.symbol = symbol;
		this.id = id;
		onCancelFailed = newEventType("CANCEL_FAILED");
		onCancelled = newEventType("CANCELLED");
		onDone = newEventType("DONE");
		onFailed = newEventType("FAILED");
		onFilled = newEventType("FILLED");
		onPartiallyFilled = newEventType("PARTIALLY_FILLED");
		onRegistered = newEventType("REGISTERED");
		onRegisterFailed = newEventType("REGISTER_FAILED");
		onDeal = newEventType("DEAL");
	}
	
	public OrderImpl(EditableTerminal terminal, Account account, Symbol symbol, long id) {
		this(terminal, account, symbol, id, new OrderController());
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
	public Account getAccount() {
		return account;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public EventType onRegistered() {
		return onRegistered;
	}

	@Override
	public EventType onRegisterFailed() {
		return onRegisterFailed;
	}

	@Override
	public EventType onCancelled() {
		return onCancelled;
	}

	@Override
	public EventType onCancelFailed() {
		return onCancelFailed;
	}

	@Override
	public EventType onFilled() {
		return onFilled;
	}

	@Override
	public EventType onPartiallyFilled() {
		return onPartiallyFilled;
	}

	@Override
	public EventType onDone() {
		return onDone;
	}
	
	@Override
	public EventType onFailed() {
		return onFailed;
	}

	@Override
	public EventType onDeal() {
		return onDeal;
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public OrderAction getAction() {
		return (OrderAction) getObject(OrderField.ACTION);
	}

	@Override
	public OrderType getType() {
		return (OrderType) getObject(OrderField.TYPE);
	}

	@Override
	public OrderStatus getStatus() {
		return (OrderStatus) getObject(OrderField.STATUS);
	}

	@Override
	public String getExternalID() {
		return getString(OrderField.EXTERNAL_ID);
	}

	@Override
	public Long getInitialVolume() {
		return getLong(OrderField.INITIAL_VOLUME);
	}

	@Override
	public Long getCurrentVolume() {
		return getLong(OrderField.CURRENT_VOLUME);
	}

	@Override
	public Double getPrice() {
		return getDouble(OrderField.PRICE);
	}

	@Override
	public Instant getTime() {
		return getInstant(OrderField.TIME);
	}

	@Override
	public Instant getDoneTime() {
		return getInstant(OrderField.DONE_TIME);
	}

	@Override
	public Double getExecutedValue() {
		return getDouble(OrderField.EXECUTED_VALUE);
	}

	@Override
	public String getComment() {
		return getString(OrderField.COMMENT);
	}
	
	@Override
	public void close() {
		lock.lock();
		try {
			terminal = null;
			onCancelFailed.removeListeners();
			onCancelFailed.removeAlternates();
			onCancelled.removeListeners();
			onCancelled.removeAlternates();
			onDeal.removeListeners();
			onDeal.removeAlternates();
			onDone.removeListeners();
			onDone.removeAlternates();
			onFailed.removeListeners();
			onFailed.removeAlternates();
			onFilled.removeListeners();
			onFilled.removeAlternates();
			onPartiallyFilled.removeListeners();
			onPartiallyFilled.removeAlternates();
			onRegistered.removeListeners();
			onRegistered.removeAlternates();
			onRegisterFailed.removeListeners();
			onRegisterFailed.removeAlternates();
			super.close();
		} finally {
			lock.unlock();
		}
	}
	
	static class OrderController implements ContainerImpl.Controller {

		@Override
		public boolean hasMinimalData(Container container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(Container container) {
			processAvailable(container);
		}

		@Override
		public void processAvailable(Container container) {
			OrderImpl order = (OrderImpl) container;
			OrderEventFactory factory = new OrderEventFactory(order);
			if ( order.hasChanged(OrderField.STATUS) ) {
				OrderStatus status = order.getStatus();				
				EventType dummy = null;
				switch ( status ) {
				case CANCEL_FAILED:
					dummy = order.onCancelFailed;
					break;
				case CANCELLED:
					dummy = (order.getCurrentVolume() == order.getInitialVolume()
						? order.onCancelled : order.onPartiallyFilled);
					break;
				case FILLED:
					dummy = order.onFilled;
					break;
				case ACTIVE:
					dummy = order.onRegistered;
					break;
				case REJECTED:
					dummy = order.onRegisterFailed;
					break;
				default:
					break;	
				}
				if ( dummy != null ) {
					order.queue.enqueue(dummy, factory);
				}
				if ( status.isError() ) {
					order.queue.enqueue(order.onFailed, factory);
				}
				if ( status.isFinal() ) {
					order.queue.enqueue(order.onDone, factory);
				}
			}
		}
		
	}
	
	static class OrderEventFactory implements EventFactory {
		private final Order order;
		
		OrderEventFactory(Order order) {
			super();
			this.order = order;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new OrderEvent(type, order);
		}
		
	}

}
