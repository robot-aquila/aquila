package ru.prolib.aquila.core.sm;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;

/**
 * Триггер по таймеру.
 * <p>
 * Данный триггер транслирует срабатывание таймера на указанный вход или вход
 * по-умолчанию (в зависимости от сигнатуры инстанцирования). Срабатывание будет
 * перенаправляться на соответствующий вход, если оно получено в промежутке
 * между активацией и деактивацией триггера.
 * <p>
 * Overriding hashCode and equals methods of the class may cause performance loss.
 */
public class SMTriggerOnTimer extends SMAbstractTrigger implements Runnable {
	private final Scheduler scheduler;
	private final Instant time;
	private TaskHandler handler;
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор позволяет указать конкретный дескриптор входа,
	 * на который будут перенаправляться срабатывание таймера. 
	 * <p>
	 * @param scheduler планировщик задач
	 * @param time расчетное время срабатывания 
	 * @param in дескриптор входа
	 */
	public SMTriggerOnTimer(Scheduler scheduler, Instant time, SMInput in) {
		super(in);
		this.scheduler = scheduler;
		this.time = time;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор подразумевает перенаправление на вход по-умолчанию.
	 * <p>
	 * @param scheduler планировщик задач
	 * @param time расчетное время срабатывания
	 */
	public SMTriggerOnTimer(Scheduler scheduler, Instant time) {
		this(scheduler, time, null);
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public synchronized TaskHandler getTaskHandler() {
		return handler;
	}

	@Override
	public void activate(SMTriggerRegistry registry) {
		if ( tryActivate(registry) ) {
			handler = scheduler.schedule(this, time);
		}
	}

	@Override
	public void deactivate() {
		if ( tryDeactivate() ) {
			handler.cancel();
		}
	}

	@Override
	public void run() {
		dispatch(time);
	}
	
	/**
	 * Method to compare internal structure.
	 * For testing purposes only!
	 * Not a thread-safe!
	 * <p>
	 * @param other - other trigger instance to compare
	 * @return true if both triggers are in same state
	 */
	public boolean isEqualTo(SMTriggerOnTimer other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		return new EqualsBuilder()
				.append(scheduler, other.scheduler)
				.append(time, other.time)
				.append(input, other.input)
				.append(proxy, other.proxy)
				.append(handler, other.handler)
				.build();
	}
	
	@Override
	public synchronized String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
