package ru.prolib.aquila.core.data.filler;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Timer;
import ru.prolib.aquila.core.utils.AlignTime;

/**
 * Служебный класс: сервис закрытия свечи по времени.
 */
class CandleFlusher implements Starter {
	private final CandleAggregator aggregator;
	private final java.util.Timer scheduler;
	private final Timer source;
	private TimerTask task;
	
	public CandleFlusher(CandleAggregator aggregator, Timer source) {
		this(aggregator, source, new java.util.Timer(true));
	}
	
	public CandleFlusher(CandleAggregator aggregator,
			Timer source, java.util.Timer scheduler)
	{
		super();
		this.aggregator = aggregator;
		this.scheduler = scheduler;
		this.source = source;
	}
	
	CandleAggregator getAggregator() {
		return aggregator;
	}
	
	java.util.Timer getScheduler() {
		return scheduler;
	}
	
	Timer getTimeSource() {
		return source;
	}
	
	/**
	 * Установить экземпляр текущей задачи.
	 * <p>
	 * <b>Прим.</b> служебный метод для тестирования запуска/останова сервиса.
	 * <p>
	 * @param task задача
	 */
	void setTask(TimerTask task) {
		this.task = task;
	}
	
	/**
	 * Получить экземпляр текущей задачи.
	 * <p>
	 * <b>Прим.</b> служебный метод для тестирования запуска/останова сервиса.
	 * <p>
	 * @return текущая задача
	 */
	TimerTask getTask() {
		return task;
	}

	@Override
	public synchronized void start() throws StarterException {
		if ( task == null ) {
			task = new CandleFlusherTask(aggregator, source);
			scheduler.scheduleAtFixedRate(task, getFirstTime(), getPeriod());
		}
	}

	@Override
	public synchronized void stop() throws StarterException {
		if ( task != null ) {
			task.cancel();
			task = null;
		}
	}
	
	private Date getFirstTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(aligner().align(source.getCurrentTime()));
		c.add(Calendar.MINUTE, aligner().getPeriod());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	private long getPeriod() {
		return aligner().getPeriod() * 60000;
	}

	private AlignTime aligner() {
		return aggregator.getTimeAligner();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleFlusher.class ) {
			return false;
		}
		CandleFlusher o = (CandleFlusher) other;
		return new EqualsBuilder()
			.appendSuper(o.source == source)
			.append(o.aggregator, aggregator)
			.isEquals();
	}

}
