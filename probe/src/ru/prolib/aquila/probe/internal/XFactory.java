package ru.prolib.aquila.probe.internal;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

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

}
