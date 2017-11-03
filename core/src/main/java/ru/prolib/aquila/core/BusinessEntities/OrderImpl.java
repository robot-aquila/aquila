package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public CDecimal getInitialVolume() {
		return getCDecimal(OrderField.INITIAL_VOLUME);
	}

	@Override
	public CDecimal getCurrentVolume() {
		return getCDecimal(OrderField.CURRENT_VOLUME);
	}
	
	@Override
	public String getSystemMessage() {
		return getString(OrderField.SYSTEM_MESSAGE);
	}

	@Override
	public CDecimal getPrice() {
		return getCDecimal(OrderField.PRICE);
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
	public CDecimal getExecutedValue() {
		return getCDecimal(OrderField.EXECUTED_VALUE);
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
	protected EventFactory createEventFactory(Instant time) {
		return new OrderEventFactory(this, time);
	}
	
	protected EventFactory createExecutionEventFactory(Instant time, OrderExecution execution) {
		return new OrderExecutionEventFactory(this, time, execution);
	}
	
	public static class OrderController implements OSCController {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container, Instant time) {
			return container.isDefined(TOKENS_FOR_AVAILABILITY);
		}

		@Override
		public void processUpdate(ObservableStateContainer container, Instant time) {
			if ( container.isAvailable() ) {
				processAvailable(container, time);
			}
		}

		@Override
		public void processAvailable(ObservableStateContainer container, Instant time) {
			OrderImpl order = (OrderImpl) container;
			EventFactory factory = order.createEventFactory(time);
			if ( order.hasChanged(OrderField.STATUS) ) {
				OrderStatus status = order.getStatus();	
				EventType dummy = null;
				switch ( status ) {
				case CANCEL_FAILED:
					dummy = order.onCancelFailed;
					break;
				case CANCELLED:
					CDecimal iv = order.getInitialVolume(), cv = order.getCurrentVolume();
					if ( iv != null ) {
						dummy = iv.compareTo(cv) == 0 ? order.onCancelled : order.onPartiallyFilled;
					} else if ( cv != null ) {
						dummy = cv.compareTo(iv) == 0 ? order.onCancelled : order.onPartiallyFilled;
					} else {
						dummy = order.onCancelled;
					}
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

		@Override
		public Instant getCurrentTime(ObservableStateContainer container) {
			OrderImpl o = (OrderImpl) container;
			return o.isClosed() ? null : o.getTerminal().getCurrentTime();
		}
		
	}
	
	static class OrderEventFactory implements EventFactory {
		protected final Order order;
		protected final Instant time;
		protected final Set<Integer> updatedTokens;
		
		OrderEventFactory(Order order, Instant time) {
			super();
			this.order = order;
			this.time = time;
			this.updatedTokens = order.getUpdatedTokens();
		}

		@Override
		public Event produceEvent(EventType type) {
			OrderEvent e = new OrderEvent(type, order, time);
			e.setUpdatedTokens(updatedTokens);
			return e;
		}
		
	}
	
	static class OrderExecutionEventFactory extends OrderEventFactory {
		protected final OrderExecution execution;
		
		OrderExecutionEventFactory(Order order, Instant time, OrderExecution execution) {
			super(order, time);
			this.execution = execution;
		}
		
		@Override
		public Event produceEvent(EventType type) {
			return new OrderExecutionEvent(type, order, time, execution);
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
			CDecimal price, CDecimal volume, CDecimal value) throws OrderException
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
		dispatcher.dispatch(onArchived, createEventFactory(getController().getCurrentTime(this)));
	}

	@Override
	public void fireExecution(OrderExecution execution) {
		dispatcher.dispatch(onExecution,
				createExecutionEventFactory(getController().getCurrentTime(this), execution));
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
