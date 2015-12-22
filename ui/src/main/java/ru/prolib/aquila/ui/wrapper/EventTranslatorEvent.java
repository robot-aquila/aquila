package ru.prolib.aquila.ui.wrapper;

import ru.prolib.aquila.core.*;

/**
 * $Id: DataSourceEventTranslatorEvent.java 577 2013-03-14 23:17:54Z huan.kaktus $
 */
public class EventTranslatorEvent extends EventImpl {
	private Event source;
	
	public EventTranslatorEvent(EventType eType, Event source) {
		super(eType);
		this.source = source;
	}
	
	public Event getSource() {
		return source;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof EventTranslatorEvent) {
			EventTranslatorEvent o = (EventTranslatorEvent) other;
			return o.getType() == getType() && o.source == source;
		}else {
			return false;
		}
	}

}
