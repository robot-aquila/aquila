package ru.prolib.aquila.qforts.impl;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProvider;

public class QFReactor implements EventListener, DataProvider, SPRunnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFReactor.class);
	}
	
	private final QForts facade;
	private final QFObjectRegistry registry;
	private final AtomicLong seqOrderID;
	private final QFSessionSchedule schedule;
	private Terminal terminal;
	private TaskHandler taskHandler;
	
	public QFReactor(QForts facade, QFObjectRegistry registry,
			QFSessionSchedule schedule, AtomicLong seqOrderID)
	{
		this.facade = facade;
		this.registry = registry;
		this.schedule = schedule;
		this.seqOrderID = seqOrderID;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param terminal instance
	 */
	void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return terminal instance
	 */
	Terminal getTerminal() {
		return terminal;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param handler instance
	 */
	void setTaskHandler(TaskHandler handler) {
		this.taskHandler = handler;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return handler instance
	 */
	TaskHandler getTaskHandler() {
		return taskHandler;
	}

	@Override
	public void run() {
		Instant currentTime = null;
		synchronized ( this ) {
			if ( this.terminal == null ) {
				return;
			}
			currentTime = terminal.getCurrentTime();
		}
		try {
			switch ( schedule.getCurrentProc(currentTime) ) {
			case UPDATE_BY_MARKET:
				facade.updateByMarket();
				break;
			case MID_CLEARING:
				facade.midClearing();
				logger.debug("[I] Clearing finished @" + currentTime);
				break;
			case CLEARING:
				facade.clearing();
				logger.debug("[M] Clearing finished @" + currentTime);
				break;
			}
		} catch ( QFTransactionException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

	@Override
	public Instant getNextExecutionTime(Instant currentTime) {
		return schedule.getNextRunTime(currentTime);
	}

	@Override
	public boolean isLongTermTask() {
		return false;
	}

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		facade.registerSecurity(security);
		//logger.debug("Security registered: {}", security.getSymbol());
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, L1UpdatableStreamContainer container) {

	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, MDUpdatableStreamContainer container) {

	}

	@Override
	public void subscribeStateUpdates(EditablePortfolio portfolio) {
		facade.registerPortfolio(portfolio);
		//logger.debug("Portfolio registered: {}", portfolio.getAccount());
	}

	@Override
	public long getNextOrderID() {
		return seqOrderID.incrementAndGet();
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {
		synchronized ( this ) {
			if ( this.terminal != null ) {
				throw new IllegalStateException();
			}
			this.terminal = terminal;
		}
		terminal.onSecurityUpdate().addListener(this);
		terminal.onSecurityLastTrade().addListener(this);
		taskHandler = terminal.schedule(this);
		logger.debug("Reactor started for: {}", terminal.getTerminalID());
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		TaskHandler th = null;
		synchronized ( this ) {
			if ( this.terminal != terminal ) {
				throw new IllegalStateException();
			}
			this.terminal = null;
			th = taskHandler;
			taskHandler = null;
		}
		terminal.onSecurityUpdate().removeListener(this);
		terminal.onSecurityLastTrade().removeListener(this);
		th.cancel();
		logger.debug("Reactor stopped for: {}", terminal.getTerminalID());
	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {
		try {
			facade.registerOrder(order);
		} catch ( QFTransactionException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		try {
			facade.cancelOrder(order);
		} catch ( QFTransactionException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof SecurityEvent ) {
			Security security = ((SecurityEvent) event).getSecurity();
			if ( ! registry.isRegistered(security) ) {
				return;
			}
			Terminal terminal = security.getTerminal();
			if ( event.isType(terminal.onSecurityLastTrade()) ) {
				onSecurityTradeEvent((SecurityTickEvent) event);			
			} else if ( event.isType(terminal.onSecurityUpdate()) ) {
				onSecurityUpdateEvent((SecurityEvent) event);
			}
		}
	}
	
	private void onSecurityTradeEvent(SecurityTickEvent event) {
		Tick tick = event.getTick();
		try {
			// TODO: refactor me
			facade.handleOrders(event.getSecurity(),
					Tick.getSize(tick),
					Tick.getPrice(tick, event.getSecurity().getScale()));
		} catch ( QFTransactionException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}
	
	private void onSecurityUpdateEvent(SecurityEvent event) {
		if ( ! event.hasChanged(SecurityField.INITIAL_MARGIN) ) {
			return;
		}
		Security security = event.getSecurity();
		switch ( schedule.getCurrentPeriod(security.getTerminal().getCurrentTime()) ) {
		case QFSessionSchedule.PCVM1_1:
		case QFSessionSchedule.PCVM1_2:
		case QFSessionSchedule.PCVM2:
			try {
				facade.updateMargin(security);
			} catch ( QFTransactionException e ) {
				logger.error("Unexpected exception: ", e);
			}
			break;
		}
	}

}
