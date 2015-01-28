package ru.prolib.aquila.core.sm;

import ru.prolib.aquila.core.*;

/**
 * Триггер по событию.
 * <p>
 * Данный триггер ретранслирует события определенного типа на указанный вход
 * или вход по-умолчанию (в зависимости от сигнатуры инстанцирования). События
 * будут перенаправляться на соответствующий вход, если они получены в
 * промежутке между активацией и деактивацией триггера.
 */
public class SMTriggerOnEvent implements SMTrigger, EventListener {
	private final EventType eventType;
	private final SMInput input;
	private SMTriggerRegistry proxy;
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор подразумевает перенаправление на вход по-умолчанию.
	 * <p>
	 * @param eventType тип события
	 */
	public SMTriggerOnEvent(EventType eventType) {
		this(eventType, null);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор позволяет указать конерктный дескриптор входа,
	 * на который будут перенаправляться события. 
	 * <p>
	 * @param eventType тип события
	 * @param input дескриптор входа
	 */
	public SMTriggerOnEvent(EventType eventType, SMInput input) {
		super();
		this.eventType = eventType;
		this.input = input;
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( proxy != null ) {
			if ( input == null ) {
				proxy.input(event);
			} else {
				proxy.input(input, event);
			}
		}
	}

	@Override
	public synchronized void activate(SMTriggerRegistry registry) {
		if ( proxy == null ) {
			proxy = registry;
			eventType.addListener(this);
		}
	}

	@Override
	public synchronized void deactivate() {
		if ( proxy != null ) {
			eventType.removeListener(this);
			proxy = null;
		}
	}

}
