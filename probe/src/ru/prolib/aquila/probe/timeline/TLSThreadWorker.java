package ru.prolib.aquila.probe.timeline;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.sm.*;

/**
 * Исполнитель хронологии событий. 
 */
public class TLSThreadWorker implements Runnable {
	static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TLSThreadWorker.class);
	}
	
	private final CountDownLatch started;
	private final TLSTimeline timeline;
	private final SMStateMachine automat;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param started индикатор старта
	 * @param timeline фасад хронологии
	 * @param automat автомат
	 */
	public TLSThreadWorker(CountDownLatch started, TLSTimeline timeline,
			SMStateMachine automat)
	{
		super();
		this.started = started;
		this.timeline = timeline;
		this.automat = automat;
	}

	@Override
	public void run() {
		try {
			automat.start();
		} catch ( SMException e ) {
			logger.error("Error starting automat: ", e);
			return;
		}
		started.countDown();
		try {
			while ( ! timeline.finished() ) {
				automat.input(timeline.pullCommand());
			}
		} catch ( SMException e ) {
			logger.error("Unexpected automat exception: ", e);
		}
	}

}
