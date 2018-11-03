package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
	 * Данный конструктор позволяет указать конкретный дескриптор входа,
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
	
	public EventType getEventType() {
		return eventType;
	}
	
	public SMInput getInput() {
		return input;
	}
	
	public synchronized SMTriggerRegistry getProxy() {
		return proxy;
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
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(113257, 891)
				.append(eventType)
				.append(input)
				.append(proxy)
				.build();
	}
	
	@Override
	public synchronized String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SMTriggerOnEvent.class ) {
			return false;
		}
		SMTriggerOnEvent o = (SMTriggerOnEvent) other;
		EventType oEventType = null, tEventType = null;
		SMInput oInput = null, tInput = null;
		SMTriggerRegistry oProxy = null, tProxy = null;
		synchronized ( o ) {
			oEventType = o.eventType;
			oInput = o.input;
			oProxy = o.proxy;
		}
		synchronized ( this ) {
			tEventType = this.eventType;
			tInput = this.input;
			tProxy = this.proxy;
		}
		return new EqualsBuilder()
				.append(oEventType, tEventType)
				.append(oInput, tInput)
				.append(oProxy, tProxy)
				.build();
	}

}
