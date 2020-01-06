package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
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
 * <p>
 * Overriding hashCode and equals methods of the class may cause performance loss.
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
	public synchronized String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * Method to compare internal structure.
	 * For testing purposes only!
	 * Not a thread-safe!
	 * <p>
	 * @param other - other trigger instance to compare
	 * @return true if both triggers are in same state
	 */
	public boolean isEqualTo(SMTriggerOnEvent other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		return new EqualsBuilder()
				.append(eventType, other.eventType)
				.append(input, other.input)
				.append(proxy, other.proxy)
				.build();
	}

}
