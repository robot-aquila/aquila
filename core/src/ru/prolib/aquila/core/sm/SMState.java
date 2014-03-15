package ru.prolib.aquila.core.sm;

import java.util.List;
import java.util.Vector;

/**
 * Заготовка состояния.
 * <p>
 */
public class SMState {
	/**
	 * Финальное состояние.
	 */
	public static final SMState FINAL = new SMState();
	private final List<SMExit> exits = new Vector<SMExit>();
	private final List<SMInput> inputs = new Vector<SMInput>();
	private SMEnterAction enterAction;
	private SMExitAction exitAction;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param enterAction входное действие
	 * @param exitAction выходное действие
	 */
	public SMState(SMEnterAction enterAction, SMExitAction exitAction) {
		super();
		this.enterAction = enterAction;
		this.exitAction = exitAction;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инициации состояния, не
	 * предусматривающего входное и выходное действия.
	 */
	public SMState() {
		this(null, null);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инициации состояния, для которого
	 * предусмотрено входное действие, но не предусмотрено выходного.
	 * @param enterAction входное действие
	 */
	public SMState(SMEnterAction enterAction) {
		this(enterAction, null);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор используется для инициации состояния, для которого
	 * не предусмотрено входного действия, но предусмотрено выходное.
	 * @param exitAction выходное действие
	 */
	public SMState(SMExitAction exitAction) {
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
	 * @return дескриптор выхода
	 */
	protected synchronized SMExit registerExit() {
		SMExit exit = new SMExit(this);
		exits.add(exit);
		return exit;
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
		return exits;
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
	
}
