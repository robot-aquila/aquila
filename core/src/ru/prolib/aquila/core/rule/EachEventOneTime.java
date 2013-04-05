package ru.prolib.aquila.core.rule;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.CompositeEventRule;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventType;

/**
 * Генерировать композитное событие, когда событие каждого типа будет получено
 * один раз. 
 * <p>
 * 2012-04-24<br>
 * $Id: EachEventOneTime.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class EachEventOneTime implements CompositeEventRule {
	private static final Logger logger = LoggerFactory.getLogger(EachEventOneTime.class);
	
	/**
	 * Конструктор
	 */
	public EachEventOneTime() {
		super();
	}

	@Override
	public boolean testNewEvent(Event event, LinkedHashMap<EventType,
								Event> state)
	{
		if ( state.get(event.getType()) != null ) {
			logger.error("The event was received more than one time: {}",event);
		}
		return true;
	}

	@Override
	public boolean testNewState(LinkedHashMap<EventType, Event> state) {
		for ( EventType type : state.keySet() ) {
			if ( state.get(type) == null ) {
				return false;
			}
		}
		return true;
	}

}
