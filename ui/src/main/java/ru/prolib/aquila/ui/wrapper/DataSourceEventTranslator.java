package ru.prolib.aquila.ui.wrapper;

import ru.prolib.aquila.core.*;

/**
 * $Id: DataSourceEventTranslator.java 577 2013-03-14 23:17:54Z huan.kaktus $
 */
public class DataSourceEventTranslator implements EventListener {
	
	private EventDispatcher dispatcher;	
	private EventType onEventOccur;
	
	public DataSourceEventTranslator(EventDispatcher dispatcher, EventType onEventOccur) {
		this.dispatcher = dispatcher;
		this.onEventOccur = onEventOccur;
	}
	
	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public EventType OnEventOccur() {
		return onEventOccur;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event e) {
		fireOnEventOccur(e);
	}
	
	private void fireOnEventOccur(Event source) {
		dispatcher.dispatch(onEventOccur, new EFactory(source));
	}
	
	static class EFactory implements EventFactory {
		private final Event source;
		
		EFactory(Event source) {
			this.source = source;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new EventTranslatorEvent(type, source);
		}
		
	}

}
