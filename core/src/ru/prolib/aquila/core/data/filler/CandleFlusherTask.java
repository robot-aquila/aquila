package ru.prolib.aquila.core.data.filler;

import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.EditableCandleSeries;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Служебный класс: задача закрытия свечи агрегатора по времени.
 */
class CandleFlusherTask extends TimerTask {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CandleFlusherTask.class);
	}
	
	private final EditableCandleSeries candles;
	private final Scheduler timer;
	
	CandleFlusherTask(EditableCandleSeries candles, Scheduler timer) {
		super();
		this.candles = candles;
		this.timer = timer;
	}
	
	EditableCandleSeries getCandles() {
		return candles;
	}
	
	Scheduler getTimer() {
		return timer;
	}

	@Override
	public void run() {
		try {
			candles.aggregate(timer.getCurrentTime(), true);
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
		}
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
			.append(o.candles, candles)
			.isEquals();
	}

}
