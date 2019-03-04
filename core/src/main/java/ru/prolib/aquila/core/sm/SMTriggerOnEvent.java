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
public class SMTriggerOnEvent extends SMAbstractTrigger implements EventListener {
	private final EventType eventType;
	
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
		super(input);
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	@Override
	public void onEvent(Event event) {
		dispatch(event);
	}

	@Override
	public void activate(SMTriggerRegistry registry) {
		if ( tryActivate(registry) ) {
			eventType.addListener(this);
		}
	}

	@Override
	public void deactivate() {
		if ( tryDeactivate() ) {
			eventType.removeListener(this);
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
