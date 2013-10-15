package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.*;

/**
 * Основа индикатора.
 * <p>
 * @param <T> - тип значения индикатора
 */
public abstract class CommonIndicator<T> implements Indicator<T> {
	private final IndicatorEventDispatcher dispatcher;
	private boolean started = false;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher
	 */
	public CommonIndicator(IndicatorEventDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @return диспетчер событий
	 */
	IndicatorEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public EventType OnAdded() {
		return dispatcher.OnAdded();
	}

	@Override
	public EventType OnUpdated() {
		return dispatcher.OnUpdated();
	}

	@Override
	public synchronized void start() throws StarterException {
		if ( started ) {
			throw new StarterException("Indicator already started: " + getId());
		}
		onStart();
		started = true;
	}

	@Override
	public synchronized void stop() throws StarterException {
		if ( started ) {
			onStop();
			started = false;
		}
	}

	@Override
	public synchronized boolean started() {
		return started;
	}

	@Override
	public EventType OnStarted() {
		return dispatcher.OnStarted();
	}

	@Override
	public EventType OnStopped() {
		return dispatcher.OnStopped();
	}
	
	/**
	 * Запустить индикатор в работу.
	 * <p>
	 * @throws StarterException
	 */
	abstract protected void onStart() throws StarterException;
	
	/**
	 * Остановить индикатор.
	 * <p>
	 * @throws StarterException
	 */
	abstract protected void onStop() throws StarterException;

}
