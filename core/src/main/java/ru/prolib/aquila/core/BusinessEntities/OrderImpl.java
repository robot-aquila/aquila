package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.OrderParams;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.OrderParamsBuilder;

/**
 * Order model.
 */
public class OrderImpl extends ObservableStateContainerImpl implements EditableOrder {
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
	private final Security security;
	private final Portfolio portfolio;
	private final Position position;
	private final long id;
	private final EventType onCancelFailed, onCancelled, onDone, onFailed,
		onFilled, onPartiallyFilled, onRegistered, onRegisterFailed,
		onExecution, onArchived;
	private boolean statusEventsEnabled = true;
	private List<OrderExecution> executions = new ArrayList<OrderExecution>();
	private Map<Long, OrderExecution> executionByID = new HashMap<>();
	
	public OrderImpl(OrderParams params) {
		super(params);
		this.terminal = params.getTerminal();
		this.security = params.getSecurity();
		this.portfolio = params.getPortfolio();
		this.position = params.getPosition();
		this.account = params.getAccount();
		this.symbol = params.getSymbol();
		this.id = params.getOrderID();
		final String pfx = params.getID() + ".";
		onCancelFailed = new EventTypeImpl(pfx + "CANCEL_FAILED");
		onCancelled = new EventTypeImpl(pfx + "CANCELLED");
		onDone = new EventTypeImpl(pfx + "DONE");
		onFailed = new EventTypeImpl(pfx + "FAILED");
		onFilled = new EventTypeImpl(pfx + "FILLED");
		onPartiallyFilled = new EventTypeImpl(pfx + "PARTIALLY_FILLED");
		onRegistered = new EventTypeImpl(pfx + "REGISTERED");
		onRegisterFailed = new EventTypeImpl(pfx + "REGISTER_FAILED");
		onExecution = new EventTypeImpl(pfx + "EXECUTION");
		onArchived = new EventTypeImpl(pfx + "ARCHIVED");		
	}
	
	@Deprecated
	public OrderImpl(EditableTerminal terminal, Account account, Symbol symbol, long id,
			OSCController controller)
	{
		this(new OrderParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(id)
				.withController(controller)
				.buildParams());
	}
	
	@Deprecated
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
	public EventType onExecution() {
		return onExecution;
	}
	
	@Override
	public EventType onArchived() {
		return onArchived;
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
	public String getSystemMessage() {
		return getString(OrderField.SYSTEM_MESSAGE);
	}

	@Override
	public FDecimal getPrice() {
		return getDecimal(OrderField.PRICE);
	}

	@Override
	public Instant getTime() {
		return getInstant(OrderField.TIME);
	}

	@Override
	public Instant getTimeDone() {
		return getInstant(OrderField.TIME_DONE);
	}

	@Override
	public FMoney getExecutedValue() {
		return getMoney(OrderField.EXECUTED_VALUE);
	}

	@Override
	public String getComment() {
		return getString(OrderField.COMMENT);
	}
	
	@Override
	public Long getUserDefinedLong() {
		return getLong(OrderField.USER_DEFINED_LONG);
	}
	
	@Override
	public String getUserDefinedString() {
		return getString(OrderField.USER_DEFINED_STRING);
	}
	
	@Override
	public void close() {
		super.close();
		lock.lock();
		try {
			terminal = null;
			onCancelFailed.removeAlternatesAndListeners();
			onCancelled.removeAlternatesAndListeners();
			onExecution.removeAlternatesAndListeners();
			onDone.removeAlternatesAndListeners();
			onFailed.removeAlternatesAndListeners();
			onFilled.removeAlternatesAndListeners();
			onPartiallyFilled.removeAlternatesAndListeners();
			onRegistered.removeAlternatesAndListeners();
			onRegisterFailed.removeAlternatesAndListeners();
			onArchived.removeAlternatesAndListeners();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	protected EventFactory createEventFactory() {
		return new OrderEventFactory(this);
	}
	
	public static class OrderController implements OSCController {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container) {
			if ( container.isAvailable() ) {
				processAvailable(container);
			}
		}

		@Override
		public void processAvailable(ObservableStateContainer container) {
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
				if ( order.isStatusEventsEnabled() ) {
					if ( dummy != null ) {
						order.dispatcher.dispatch(dummy, factory);
					}
					if ( status.isError() ) {
						order.dispatcher.dispatch(order.onFailed, factory);
					}
					if ( status.isFinal() ) {
						order.dispatcher.dispatch(order.onDone, factory);
					}
				}
			}
		}
		
	}
	
	static class OrderEventFactory implements EventFactory {
		protected final Order order;
		
		OrderEventFactory(Order order) {
			super();
			this.order = order;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new OrderEvent(type, order);
		}
		
	}
	
	static class OrderExecutionEventFactory extends OrderEventFactory {
		protected final OrderExecution execution;
		
		OrderExecutionEventFactory(Order order, OrderExecution execution) {
			super(order);
			this.execution = execution;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new OrderExecutionEvent(type, order, execution);
		}
		
	}

	@Override
	public List<OrderExecution> getExecutions() {
		lock.lock();
		try {
			return new ArrayList<OrderExecution>(executions);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void addExecution(OrderExecution execution) throws OrderException {
		lock.lock();
		try {
			long id = execution.getID();
			if ( executionByID.containsKey(id) ) {
				throw new OrderException("Execution already exists: " + id);
			}
			executionByID.put(id, execution);
			executions.add(execution);	
		} finally {
			lock.unlock();
		}
	}

	@Override
	public OrderExecution addExecution(long id, String externalID, Instant time,
			FDecimal price, long volume, FMoney value) throws OrderException
	{
		lock.lock();
		try {
			OrderExecution execution = new OrderExecutionImpl(terminal, id,
					externalID, symbol, getAction(), this.id, time, price,
					volume, value);
			addExecution(execution);
			return execution;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setStatusEventsEnabled(boolean enable) {
		lock.lock();
		try {
			statusEventsEnabled = enable;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isStatusEventsEnabled() {
		lock.lock();
		try {
			return statusEventsEnabled;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public OrderExecution getExecution(long executionID) throws OrderException {
		lock.lock();
		try {
			OrderExecution execution = executionByID.get(executionID);
			if ( execution == null ) {
				throw new OrderException("Execution not exists: " + executionID);
			}
			return execution;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void fireArchived() {
		dispatcher.dispatch(onArchived, new OrderEventFactory(this));
	}

	@Override
	public void fireExecution(OrderExecution execution) {
		dispatcher.dispatch(onExecution, new OrderExecutionEventFactory(this, execution));
	}
	
	@Override
	public Security getSecurity() {
		return security;
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public Position getPosition() {
		return position;
	}

}
