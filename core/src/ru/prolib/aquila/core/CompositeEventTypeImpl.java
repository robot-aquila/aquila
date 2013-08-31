package ru.prolib.aquila.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Композитный типа события.
 * <p>
 * Класс предназначен для упрощения кода обработки нескольких событий в
 * соответствии с определенным правилом. Каждый такого типа отлавливает
 * несколько других типов событий и генерирует композитное событие в случае,
 * если требуемые условия выполнены. 
 * <p>
 * 2012-04-21<br>
 * $Id: CompositeEventTypeImpl.java 219 2012-05-20 12:16:45Z whirlwind $
 */
@Deprecated
public class CompositeEventTypeImpl extends EventTypeImpl
								 implements EventListener, CompositeEventType
{
	private final Map<EventType, Event> cache =
		new LinkedHashMap<EventType, Event>();
	private final CompositeEventRule rule;
	private final CompositeEventGenerator generator;

	/**
	 * Создать композитный тип события.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param types список обслуживаемых типов событий
	 * @param rule правило композиции
	 * @param generator генератор событий
	 */
	public CompositeEventTypeImpl(EventDispatcher dispatcher,
								  List<EventType> types,
								  CompositeEventRule rule,
								  CompositeEventGenerator generator)
	{
		this(dispatcher, types.toArray(new EventType[types.size()]),
				rule, generator);
	}
	
	/**
	 * Создать композитный тип события.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param types список обслуживаемых типов событий
	 * @param rule правило композиции
	 * @param generator генератор событий
	 */
	public CompositeEventTypeImpl(EventDispatcher dispatcher,
								  EventType[] types,
								  CompositeEventRule rule,
								  CompositeEventGenerator generator)
	{
		super(dispatcher);
		this.rule = rule;
		this.generator = generator;
		init(rule, generator, types);
	}
	
	private final void init(CompositeEventRule rule,
							CompositeEventGenerator generator,
							EventType[] types)
	{
		if ( rule == null ) {
			throw new NullPointerException("Rule cannot be null");
		}
		if ( generator == null ) {
			throw new NullPointerException("Generator cannot be null");
		}
		if ( types.length == 0 ) {
			throw new IllegalArgumentException("Event types not listed");
		}
		for ( int i = 0; i < types.length; i ++ ) {
			if ( types[i] == null ) {
				throw new NullPointerException("Event type " + i + " is null");
			}
			cache.put(types[i], null);
		}
	}
	
	/**
	 * Получить правило генерации события.
	 * <p>
	 * @return экземпляр правила
	 */
	public CompositeEventRule getRule() {
		return rule;
	}
	
	/**
	 * Получить генератор события.
	 * <p>
	 * @return генератор события
	 */
	public CompositeEventGenerator getEventGenerator() {
		return generator;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.CompositeEventType#getCurrentState()
	 */
	@Override
	public synchronized Map<EventType, Event> getCurrentState() {
		Map<EventType, Event> copy = new LinkedHashMap<EventType,Event>(cache);
		return Collections.unmodifiableMap(copy);
	}

	@Override
	public synchronized void onEvent(Event event) {
		LinkedHashMap<EventType, Event> state = null;
		EventType type = event.getType();
		if ( ! cache.containsKey(type) ) {
			return; // Unknown event type
		}
		state = new LinkedHashMap<EventType, Event>(cache);
		if ( ! rule.testNewEvent(event, state) ) {
			return;
		}
		cache.put(type, event);
		state.put(type, event);
		if ( ! rule.testNewState(state) ) {
			return;
		}
		for ( EventType t : cache.keySet() ) {
			cache.put(t, null);
		}
		getEventDispatcher()
			.dispatch(generator.generateEvent(this, state, event));
	}
	
	@Override
	public synchronized void addListener(EventListener listener) {
		for ( EventType type : cache.keySet() ) {
			type.addListener(this);
		}
		super.addListener(listener);
	}
	
	@Override
	public synchronized void removeListener(EventListener listener) {
		super.removeListener(listener);
		for ( EventType type : cache.keySet() ) {
			type.removeListener(this);
		}
	}

}
