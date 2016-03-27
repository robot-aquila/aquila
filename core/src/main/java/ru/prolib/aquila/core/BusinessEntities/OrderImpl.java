package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.*;

/**
 * Order model.
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
		onFilled, onPartiallyFilled, onRegistered, onRegisterFailed, onExecution;
	private boolean statusEventsEnabled = true;
	private List<OrderExecution> executions = new ArrayList<OrderExecution>();
	private Map<Long, OrderExecution> executionByID = new HashMap<>();
	
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
		onExecution = newEventType("EXECUTION");
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
	public EventType onExecution() {
		return onExecution;
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
			onExecution.removeListeners();
			onExecution.removeAlternates();
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
	
	@Override
	protected EventFactory createEventFactory() {
		return new OrderEventFactory(this);
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
				if ( order.isStatusEventsEnabled() ) {
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
	
	private OrderExecution createExecution(long id, String externalID,
			Instant time, double price, long volume, double value)
					throws OrderException
	{
		if ( executionByID.containsKey(id) ) {
			throw new OrderException("Execution already exists: " + id);
		}
		OrderExecution exec = new OrderExecutionImpl(terminal, id, externalID,
				symbol, getAction(), this.id, time, price, volume, value);
		executionByID.put(id, exec);
		executions.add(exec);
		return exec;
	}

	@Override
	public void addExecution(long id, String externalID, Instant time,
			double price, long volume, double value) throws OrderException
	{
		lock.lock();
		try {
			OrderExecution execution = createExecution(id, externalID, time, price, volume, value);
			queue.enqueue(onExecution, new OrderExecutionEventFactory(this, execution));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void loadExecution(long id, String externalID, Instant time,
			double price, long volume, double value) throws OrderException
	{
		lock.lock();
		try {
			createExecution(id, externalID, time, price, volume, value);
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
	public Map<Integer, Object> getChangeWhenExecutionAdded() {
		lock.lock();
		try {
			long initialVolume = getInitialVolume(), executedVolume = 0;
			double executedValue = 0.0d;
			Instant lastExecutionTime = null;
			for ( OrderExecution execution : executions ) {
				executedVolume += execution.getVolume();
				executedValue += execution.getValue();
				lastExecutionTime = execution.getTime();
			}
			long currentVolume = initialVolume - executedVolume;
			Map<Integer, Object> tokens = new HashMap<Integer, Object>();
			tokens.put(OrderField.CURRENT_VOLUME, currentVolume);
			tokens.put(OrderField.EXECUTED_VALUE, executedValue);
			if ( currentVolume <= 0L ) {
				tokens.put(OrderField.STATUS, OrderStatus.FILLED);
				tokens.put(OrderField.DONE_TIME, lastExecutionTime);
			}
			return tokens;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, Object> getChangeWhenCancelled(Instant time) {
		lock.lock();
		try {
			Map<Integer, Object> tokens = new HashMap<>();
			tokens.put(OrderField.STATUS, OrderStatus.CANCELLED);
			tokens.put(OrderField.DONE_TIME, time);
			return tokens;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, Object> getChangeWhenRejected(Instant time, String reason) {
		lock.lock();
		try {
			Map<Integer, Object> tokens = new HashMap<>();
			tokens.put(OrderField.STATUS, OrderStatus.REJECTED);
			tokens.put(OrderField.DONE_TIME, time);
			tokens.put(OrderField.SYSTEM_MESSAGE, reason);
			return tokens;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, Object> getChangeWhenRegistered() {
		lock.lock();
		try {
			Map<Integer, Object> tokens = new HashMap<>();
			tokens.put(OrderField.STATUS, OrderStatus.ACTIVE);
			return tokens;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, Object> getChangeWhenCancelFailed(Instant time, String reason) {
		lock.lock();
		try {
			Map<Integer, Object> tokens = new HashMap<>();
			tokens.put(OrderField.DONE_TIME, time);
			tokens.put(OrderField.SYSTEM_MESSAGE, reason);
			tokens.put(OrderField.STATUS, OrderStatus.CANCEL_FAILED);
			return tokens;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void updateWhenExecutionAdded() {
		update(getChangeWhenExecutionAdded());
	}

	@Override
	public void updateWhenCancelled(Instant time) {
		update(getChangeWhenCancelled(time));
	}

	@Override
	public void updateWhenRejected(Instant time, String reason) {
		update(getChangeWhenRejected(time, reason));
	}

	@Override
	public void updateWhenRegistered() {
		update(getChangeWhenRegistered());
	}

	@Override
	public void updateWhenCancelFailed(Instant time, String reason) {
		update(getChangeWhenCancelFailed(time, reason));
	}

}
