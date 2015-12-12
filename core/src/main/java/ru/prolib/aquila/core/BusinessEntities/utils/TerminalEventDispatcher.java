package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий терминала.
 * <p>
 * Абстрагирует терминал от набора типов событий.
 */
public class TerminalEventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventTypeSI onConnected, onDisconnected, onStarted,
		onStopped, onPanic, onRequestSecurityError, onReady, onUnready;
	
	public TerminalEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Terminal");
		onConnected = dispatcher.createType("Connected");
		onDisconnected = dispatcher.createType("Disconnected");
		onStarted = dispatcher.createType("Started");
		onStopped = dispatcher.createType("Stopped");
		onPanic = dispatcher.createType("Panic");
		onReady = dispatcher.createSyncType("Ready");
		onUnready = dispatcher.createSyncType("Unready");
		onRequestSecurityError = dispatcher.createType("RequestSecurityError");
	}
	
	/**
	 * Получить подчиненный диспетчер событий.
	 * <p>
	 * Служебный метод. Только для тестов.
	 * <p>
	 * @return диспетчер событий
	 */
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить тип события: установлено соединение с удаленной системой.
	 * <p>
	 * @return тип события
	 */
	public EventType OnConnected() {
		return onConnected;
	}
	
	/**
	 * Получить тип события: соединение с удаленной системой разорвано.
	 * <p>
	 * @return тип события
	 */
	public EventType OnDisconnected() {
		return onDisconnected;
	}
	
	/**
	 * Получить тип события: терминал запущен в работу.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStarted() {
		return onStarted;
	}
	
	/**
	 * Получить тип события: терминал остановлен.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopped() {
		return onStopped;
	}
	
	/**
	 * Получить тип события: критическая ошибка.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPanic() {
		return onPanic;
	}
	
	/**
	 * Получить тип события: запрос инструмента отклонен.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRequestSecurityError() {
		return onRequestSecurityError;
	}
	
	/**
	 * Получить тип события: терминал готов к приему запросов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnReady() {
		return onReady;
	}
	
	/**
	 * Получить тип события: терминал не готов к приему запросов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUnready() {
		return onUnready;
	}
	
	/**
	 * Генератор события: установлено соединение с удаленной системой.
	 * <p>
	 * Этот метод помещает в очередь событие типа {@link #OnConnected()}
	 * <p>
	 * @param terminal терминал
	 */
	public synchronized void fireConnected(Terminal terminal) {
		dispatcher.dispatch(new EventImpl(onConnected));
	}

	/**
	 * Генератор события: соединение с удаленной системой разорвано.
	 * <p>
	 * Этот метод помещает в очередь событие типа {@link #OnDisconnected()}
	 * <p>
	 * @param terminal терминал
	 */
	public synchronized void fireDisconnected(Terminal terminal) {
		dispatcher.dispatch(new EventImpl(onDisconnected));
	}
	
	/**
	 * Генератор события: терминал запущен.
	 */
	public void fireStarted() {
		dispatcher.dispatch(new EventImpl(onStarted));
	}
	
	/**
	 * Генератор события: терминал остановлен.
	 */
	public void fireStopped() {
		dispatcher.dispatch(new EventImpl(onStopped));
	}
	
	/**
	 * Генератор события: критическая ошибка.
	 * <p>
	 * @param code код состояния
	 * @param msgId расшифровка
	 */
	public void firePanic(int code, String msgId) {
		firePanic(code, msgId, new Object[] { });
	}
	
	/**
	 * Генератор события: критическая ошибка.
	 * <p>
	 * @param code код состояния
	 * @param msgId шаблон-расшифровка
	 * @param args аргументы для подстановки
	 */
	public void firePanic(int code, String msgId, Object[] args) {
		dispatcher.dispatch(new PanicEvent(onPanic, code, msgId, args));
	}
	
	/**
	 * Генератор события: запрос инструмента отклонен.
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @param errorCode код ошибки
	 * @param errorMsg расшифровка
	 */
	public void fireSecurityRequestError(Symbol symbol, int errorCode, String errorMsg) {
		dispatcher.dispatch(new RequestSecurityEvent(onRequestSecurityError, symbol, errorCode, errorMsg));
	}
	
	/**
	 * Генератор события: терминал готов к приему запросов.
	 */
	public void fireReady() {
		dispatcher.dispatch(new EventImpl(onReady));
	}
	
	/**
	 * Генератор события: терминал не готов к приему запросов.
	 */
	public void fireUnready() {
		dispatcher.dispatch(new EventImpl(onUnready));
	}

}
