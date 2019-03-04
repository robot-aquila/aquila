package ru.prolib.aquila.core.sm;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(10086521, 11)
				.append(scheduler)
				.append(time)
				.append(input)
				.append(proxy)
				.append(handler)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SMTriggerOnTimer.class ) {
			return false;
		}
		SMTriggerOnTimer o = (SMTriggerOnTimer) other;
		Scheduler oScheduler = null, tScheduler = null;
		Instant oTime = null, tTime = null;
		SMInput oInput = null, tInput = null;
		SMTriggerRegistry oProxy = null, tProxy = null;
		TaskHandler oHandler = null, tHandler = null;
		synchronized ( o ) {
			oScheduler = o.scheduler;
			oTime = o.time;
			oInput = o.input;
			oProxy = o.proxy;
			oHandler = o.handler;
		}
		synchronized ( this ) {
			tScheduler = this.scheduler;
			tTime = this.time;
			tInput = this.input;
			tProxy = this.proxy;
			tHandler = this.handler;
		}
		return new EqualsBuilder()
				.append(oScheduler, tScheduler)
				.append(oTime, tTime)
				.append(oInput, tInput)
				.append(oProxy, tProxy)
				.append(oHandler, tHandler)
				.build();
	}
	
	@Override
	public synchronized String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
