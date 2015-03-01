package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.KW;

/**
 * Диспетчер событий терминала.
 * <p>
 * Абстрагирует терминал от набора типов событий.
 */
public class TerminalEventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventTypeSI onConnected, onDisconnected, onStarted,
		onStopped, onPanic, onRequestSecurityError;
	private final List<KW<TerminalObserver>> observers;
	
	public TerminalEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Terminal");
		onConnected = dispatcher.createType("Connected");
		onDisconnected = dispatcher.createType("Disconnected");
		onStarted = dispatcher.createType("Started");
		onStopped = dispatcher.createType("Stopped");
		onPanic = dispatcher.createType("Panic");
		onRequestSecurityError = dispatcher.createType("RequestSecurityError");
		observers = new ArrayList<KW<TerminalObserver>>();
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
	 * Генератор события: установлено соединение с удаленной системой.
	 * <p>
	 * Этот метод помещает в очередь событие типа {@link #OnConnected()}, а так
	 * же выполняет оповещение обозревателей синхронных событий посредством
	 * вызова метода {@link TerminalObserver#OnTerminalReady(Terminal)} для
	 * каждого зарегистрированного обозревателя.
	 * <p>
	 * @param terminal терминал
	 */
	public synchronized void fireConnected(Terminal terminal) {
		dispatcher.dispatch(new EventImpl(onConnected));
		for ( KW<TerminalObserver> o : observers ) {
			o.instance().OnTerminalReady(terminal);
		}
	}

	/**
	 * Генератор события: соединение с удаленной системой разорвано.
	 * <p>
	 * Этот метод помещает в очередь событие типа {@link #OnDisconnected()}, а
	 * так же выполняет оповещение обозревателей синхронных событий посредством
	 * вызова метода {@link TerminalObserver#OnTerminalUnready(Terminal)} для
	 * каждого зарегистрированного обозревателя.
	 * <p>
	 * @param terminal терминал
	 */
	public synchronized void fireDisconnected(Terminal terminal) {
		dispatcher.dispatch(new EventImpl(onDisconnected));
		for ( KW<TerminalObserver> o : observers ) {
			o.instance().OnTerminalUnready(terminal);
		}
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
	 * @param descr дескриптор инструмента
	 * @param errorCode код ошибки
	 * @param errorMsg расшифровка
	 */
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg)
	{
		dispatcher.dispatch(new RequestSecurityEvent(onRequestSecurityError,
				descr, errorCode, errorMsg));
	}
	
	/**
	 * Подписать обозревателя на синхронные события.
	 * <p>
	 * @param observer обозреватель
	 */
	public synchronized void subscribe(TerminalObserver observer) {
		KW<TerminalObserver> o = new KW<TerminalObserver>(observer);
		if ( ! observers.contains(o) ) {
			observers.add(o);
		}
	}
	
	/**
	 * Отписать обозревателя от синхронных событий.
	 * <p>
	 * @param observer обозреватель
	 */
	public synchronized void unsubscribe(TerminalObserver observer) {
		observers.remove(new KW<TerminalObserver>(observer));
	}

}
