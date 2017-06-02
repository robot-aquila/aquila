package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.DataProvider;

/**
 * Terminal parameters.
 * <p>
 * This class is used to pass to terminal's constructor to reduce coupling with
 * its implementation. It may be useful for derived classes or for testing
 * purposes.
 */
public class TerminalParams {
	private static int lastAssignedNum = 0;	
	private Scheduler scheduler;
	private EventQueue eventQueue;
	private String terminalID;
	private DataProvider dataProvider;
	private ObjectFactory objectFactory;
	private EventDispatcher eventDispatcher;
	
	public static synchronized int getCurrentGeneratedNumber() {
		return lastAssignedNum;
	}
	
	public static synchronized String generateNextGeneratedID() {
		return "Terminal#" + (++lastAssignedNum);
	}
	
	public TerminalParams() {
		super();
	}
	
	public Scheduler getScheduler() {
		if ( scheduler == null ) {
			scheduler = new SchedulerLocal();
		}
		return scheduler;
	}
	
	public EventQueue getEventQueue() {
		if ( eventQueue == null ) {
			eventQueue = new EventQueueImpl(getTerminalID());
		}
		return eventQueue;
	}
	
	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public String getTerminalID() {
		return terminalID == null ? generateNextGeneratedID() : terminalID;
	}
	
	public ObjectFactory getObjectFactory() {
		if ( objectFactory == null ) {
			objectFactory = new ObjectFactoryImpl();
		}
		return objectFactory;
	}
	
	public EventDispatcher getEventDispatcher() {
		if ( eventDispatcher == null ) {
			eventDispatcher = new EventDispatcherImpl(getEventQueue());
		}
		return eventDispatcher;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	public void setTerminalID(String id) {
		this.terminalID = id;
	}
	
	public void setObjectFactory(ObjectFactory factory) {
		this.objectFactory = factory;
	}
	
	public void setEventQueue(EventQueue queue) {
		this.eventQueue = queue;
	}
	
	public void setEventDispatcher(EventDispatcher dispatcher) {
		this.eventDispatcher = dispatcher;
	}

}
