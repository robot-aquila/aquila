package ru.prolib.aquila.probe.internal;

import java.io.File;

import org.joda.time.Interval;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;
import ru.prolib.aquila.probe.timeline.TLSTimeline;
import ru.prolib.aquila.probe.timeline.TLSTimelineFactory;

public class XFactory {
	
	public XFactory() {
		super();
	}
	
	public PROBETerminal newTerminal(String id) {
		return new PROBETerminal(id);
	}
	
	public EventQueueStarter newQueueStarter(EventQueue queue, long timeout) {
		return new EventQueueStarter(queue, timeout);
	}
	
	public SecurityHandlerFORTS newSecurityHandlerFORTS(PROBETerminal terminal,
			EditableSecurity security, SecurityProperties properties)
	{
		return new SecurityHandlerFORTS(terminal, security, properties);
	}
	
	public TickDataDispatcher newTickDataDispatcher(Aqiterator<Tick> it,
			TickHandler handler)
	{
		return new TickDataDispatcher(it, handler);
	}
	
	public DataProvider newDataProvider(PROBETerminal terminal) {
		return new DataProvider(terminal, this);
	}
	
	public PROBEDataStorage newDataStorage(File root) {
		return new PROBEDataStorage(root);
	}
	
	public TLSTimeline newTimeline(EventSystem es, Interval interval) {
		return new TLSTimelineFactory(es).produce(interval);
	}

}
