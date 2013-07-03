package ru.prolib.aquila.core.data.filler;

import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Timer;

/**
 * Служебный класс: задача закрытия свечи агрегатора по времени.
 */
class CandleFlusherTask extends TimerTask {
	private final CandleAggregator aggregator;
	private final Timer timer;
	
	CandleFlusherTask(CandleAggregator aggregator, Timer timer) {
		super();
		this.aggregator = aggregator;
		this.timer = timer;
	}
	
	CandleAggregator getAggregator() {
		return aggregator;
	}
	
	Timer getTimer() {
		return timer;
	}

	@Override
	public void run() {
		aggregator.add(timer.getCurrentTime());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleFlusherTask.class ) {
			return false;
		}
		CandleFlusherTask o = (CandleFlusherTask) other;
		return new EqualsBuilder()
			.appendSuper(o.timer == timer)
			.append(o.aggregator, aggregator)
			.isEquals();
	}

}
