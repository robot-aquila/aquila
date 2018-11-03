package ru.prolib.aquila.core.sm;

import java.util.*;

/**
 * A state handler class.
 */
public class SMStateHandler {
	/**
	 * Финальное состояние.
	 */
	public static final SMStateHandler FINAL = new SMStateHandler() {
		@Override public String toString() { return "[FinalState]"; }
	};
	private final Map<String, SMExit> exits;
	private final List<SMInput> inputs;
	private SMEnterAction enterAction;
	private SMExitAction exitAction;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param enterAction входное действие
	 * @param exitAction выходное действие
	 */
	public SMStateHandler(SMEnterAction enterAction, SMExitAction exitAction) {
		super();
		inputs = new Vector<SMInput>();
		exits = new LinkedHashMap<String, SMExit>();
		this.enterAction = enterAction;
		this.exitAction = exitAction;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инициации состояния, не
	 * предусматривающего входное и выходное действия.
	 */
	public SMStateHandler() {
		this(null, null);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инициации состояния, для которого
	 * предусмотрено входное действие, но не предусмотрено выходного.
	 * @param enterAction входное действие
	 */
	public SMStateHandler(SMEnterAction enterAction) {
		this(enterAction, null);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инициации состояния, для которого
	 * не предусмотрено входного действия, но предусмотрено выходное.
	 * @param exitAction выходное действие
	 */
	public SMStateHandler(SMExitAction exitAction) {
		this(null, exitAction);
	}
	
	/**
	 * Зарегистрировать вход.
	 * <p>
	 * @param action функция входа
	 * @return дескриптор зарегистрированного входа
	 */
	protected synchronized SMInput registerInput(SMInputAction action) {
		SMInput input = new SMInput(this, action);
		inputs.add(input);
		return input;
	}
	
	/**
	 * Зарегистрировать выход.
	 * <p>
	 * @param exitId символьный идентификатор выхода
	 * @return дескриптор выхода
	 */
	protected synchronized SMExit registerExit(String exitId) {
		if ( exits.containsKey(exitId) ) {
			return exits.get(exitId);
		} else if ( exitId == null ){
			throw new NullPointerException("ID cannot be null");
		} else {
			SMExit exit = new SMExit(this, exitId);
			exits.put(exitId, exit);
			return exit;
		}
	}
	
	/**
	 * Установить входное действие.
	 * <p>
	 * @param action входное действие
	 */
	protected synchronized void setEnterAction(SMEnterAction action) {
		this.enterAction = action;
	}
	
	/**
	 * Установить выходное действие.
	 * <p>
	 * @param action выходное действие
	 */
	protected synchronized void setExitAction(SMExitAction action) {
		this.exitAction = action;
	}
	
	/**
	 * Получить список выходов.
	 * <p>
	 * @return список выходов из состояния
	 */
	public synchronized List<SMExit> getExits() {
		return new Vector<SMExit>(exits.values());
	}
	
	/**
	 * Получить дескриптор выхода.
	 * <p>
	 * @param exitId идентификатор выхода
	 * @return дескриптор
	 * @throws IllegalArgumentException выхода с указанным идентификатором
	 * не существует
	 */
	public synchronized SMExit getExit(String exitId) {
		if ( exitId == null ) {
			return null;
		}
		SMExit exit = exits.get(exitId);
		if ( exit == null ) {
			throw new IllegalArgumentException("ID not exists: " + exitId);
		} else {
			return exit;
		}
	}

	/**
	 * Получить список входов.
	 * <p>
	 * @return список входов
	 */
	public synchronized List<SMInput> getInputs() {
		return inputs;
	}
	
	/**
	 * Получить входное действие.
	 * <p>
	 * @return действие или null, если входное действие не определено
	 */
	public synchronized SMEnterAction getEnterAction() {
		return enterAction;
	}
	
	/**
	 * Получить выходное действие.
	 * <p>
	 * @return действие или null, если выходное действие не определено
	 */
	public synchronized SMExitAction getExitAction() {
		return exitAction;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
}
