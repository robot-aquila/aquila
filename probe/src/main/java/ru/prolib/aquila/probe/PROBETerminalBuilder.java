package ru.prolib.aquila.probe;

import java.io.File;

import org.joda.time.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.BasicTerminalParams;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.probe.internal.*;
import ru.prolib.aquila.probe.timeline.*;

public class PROBETerminalBuilder {
	private BasicTerminalBuilder childBuilder;
	private Timeline timeline;
	private EventSystem eventSystem;
	private Interval timeInterval;
	private Scheduler scheduler;
	private PROBEServiceLocator locator;
	//private PROBEDataStorage dataStorage;
	private DataProvider dataProvider;
	
	public PROBETerminalBuilder() {
		this(null);
	}
	
	public PROBETerminalBuilder(BasicTerminalBuilder childBuilder) {
		super();
		this.childBuilder = childBuilder;
	}
	
	private BasicTerminalBuilder getChildBuilder() {
		if ( childBuilder == null ) {
			childBuilder = new BasicTerminalBuilder();
		}
		return childBuilder;
	}
	
	private EventSystem getEventSystem() {
		if ( eventSystem == null ) {
			eventSystem = new EventSystemImpl();
		}
		return eventSystem;
	}
	
	private Interval getTimeInterval() {
		if ( timeInterval == null ) {
			timeInterval = new Interval(new DateTime(0), new DateTime()); 
		}
		return timeInterval;
	}
	
	private Timeline getTimeline() {
		if ( timeline == null ) {
			timeline = new TLSTimelineFactory(getEventSystem())
				.produce(getTimeInterval());
		}
		return timeline;
	}
	
	private Scheduler getScheduler() {
		if ( scheduler == null ) {
			scheduler = new SchedulerImpl(getTimeline());
		}
		return scheduler;
	}
	
	private BasicTerminalParams getBasicTerminalParams() {
		return getChildBuilder()
			.withEventSystem(getEventSystem())
			.withScheduler(getScheduler())
			.buildParams();
	}
	
	private PROBEServiceLocator getServiceLocator() {
		if ( locator == null ) {
			locator = new PROBEServiceLocator();
		}
		return locator;
	}
	
	//private PROBEDataStorage getDataStorage() {
	//	if ( dataStorage == null ) {
	//		dataStorage = new PROBEDataStorage(new File(""));
	//	}
	//	return dataStorage;
	//}
	
	private DataProvider getDataProvider() {
		if ( dataProvider == null ) {
			dataProvider = new DataProvider();
		}
		return dataProvider;
	}
	
	public PROBETerminal buildTerminal() {
		throw new RuntimeException("Not implemented");
		/*
		PROBEServiceLocator locator = getServiceLocator();
		PROBETerminal terminal =
				new PROBETerminal(getBasicTerminalParams(), locator);
		locator.setDataStorage(getDataStorage());
		locator.setDataProvider(getDataProvider());
		locator.setTimeline(getTimeline());
		return terminal;
		*/
	}
	
	public PROBETerminalBuilder withEventSystem(EventSystem facade) {
		return this;
	}
	
	public PROBETerminalBuilder withCommonEventSystemAndQueueId(String id) {
		return this;
	}
	
	public PROBETerminalBuilder 
		withServiceLocator(PROBEServiceLocator locator)
	{
		return this;
	}
	
	public PROBETerminalBuilder withScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		return this;
	}
	
	public PROBETerminalBuilder withTimeline(Timeline timeline) {
		this.timeline = timeline;
		return this;
	}
	
	public PROBETerminalBuilder
		withCommonTimelineAndTimeInterval(Interval interval)
	{
		return this;
	}
	
	public PROBETerminalBuilder withDataProvider(DataProvider provider) {
		this.dataProvider = provider;
		return this;
	}
	
	//public PROBETerminalBuilder withDataStorage(PROBEDataStorage storage) {
	//	return this;
	//}
	
	//public PROBETerminalBuilder withCommonDataStorageAndPath(String path) {
	//	this.dataStorage = new PROBEDataStorage(new File(path));
	//	return this;
	//}

}
