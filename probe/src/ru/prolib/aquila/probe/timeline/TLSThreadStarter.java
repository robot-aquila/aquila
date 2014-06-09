package ru.prolib.aquila.probe.timeline;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Пускач потока исполнения хронологии событий.
 * <p>
 */
public class TLSThreadStarter {
	private boolean done = false;
	private final CountDownLatch started;
	private final TLSThread thread;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param started индикатор запуска
	 * @param thread объект потока
	 */
	TLSThreadStarter(CountDownLatch started, TLSThread thread) {
		super();
		this.started = started;
		this.thread = thread;
	}
	
	/**
	 * Запустить поток исполненияю
	 * <p>
	 * Если поток был запущен ранее, то ничего не происходит. То есть,
	 * последовательный вызов данного метода полностью безопасен.
	 */
	public void start() {
		if ( ! done ) {
			done = true;
			thread.start();
			try {
				if ( ! started.await(5, TimeUnit.SECONDS) ) {
					throw new RuntimeException("Failed to start worker thread");
				}
			} catch ( InterruptedException e ) {
				throw new TLInterruptionsNotAllowedException(e);
			}
		}
	}
	
	/**
	 * Включить отладочные сообщения.
	 * <p>
	 * @param enabled true - включить, false - отключить
	 */
	public void setDebug(boolean enabled) {
		thread.setDebug(enabled);
	}

}
