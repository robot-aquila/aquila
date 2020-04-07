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
	private Class<?> incomingDataType = Void.class, resultDataType = Void.class;
	private Object incomingData, resultData;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param enterAction входное действие
	 * @param exitAction выходное действие
	 * @param incoming_data_type тип данных, ожидаемых при входе в состояние
	 * @param result_data_type тип данных, ожидаемых на выходе из состояния
	 */
	public SMStateHandler(SMEnterAction enterAction,
			SMExitAction exitAction,
			Class<?> incoming_data_type,
			Class<?> result_data_type)
	{
		super();
		inputs = new Vector<SMInput>();
		exits = new LinkedHashMap<String, SMExit>();
		this.enterAction = enterAction;
		this.exitAction = exitAction;
		this.incomingDataType = incoming_data_type;
		this.resultDataType = result_data_type;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает обработчик состояния не подразумевающий подачи данных на вход и формирование результата.
	 * <p>
	 * @param enter_action входное действие
	 * @param exit_action выходное действие
	 */
	public SMStateHandler(SMEnterAction enter_action, SMExitAction exit_action) {
		this(enter_action, exit_action, Void.class, Void.class);
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
	 * Set incoming data type.
	 * <p> 
	 * @param type - data type
	 */
	protected synchronized void setIncomingDataType(Class<?> type) {
		this.incomingDataType = type;
	}

	/**
	 * Set result data type.
	 * <p>
	 * @param type - data type
	 */
	protected synchronized void setResultDataType(Class<?> type) {
		this.resultDataType = type;
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
	
	/**
	 * Get incoming data type.
	 * <p>
	 * @return data type
	 */
	public synchronized Class<?> getIncomingDataType() {
		return incomingDataType;
	}
	
	/**
	 * Get result data type.
	 * <p>
	 * @return data type
	 */
	public synchronized Class<?> getResultDataType() {
		return resultDataType;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Set incoming data before enter the state.
	 * <p>
	 * @param data - data. Null values permitted.
	 * @throws IllegalArgumentException - the type of data mismatch with declared incoming data type
	 */
	public synchronized void setIncomingData(Object data) {
		if ( data == null ) {
			incomingData = data;
		} else if ( ! incomingDataType.isInstance(data) ) {
			throw new IllegalArgumentException("Unexpected data type: " + data.getClass());
		} else {
			incomingData = data;
		}
	}
	
	/**
	 * Get incoming data.
	 * <p>
	 * This method is publicly visible to make the data accessible from other classes
	 * that represent special actions like an enter action, input action or exit action.
	 * <p>
	 * @return data or null if no data
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T getIncomingData() {
		return (T) incomingData;
	}

	/**
	 * Set result data before exit the state.
	 * <p>
	 * This method is publicly visible to make possible a result definition from other classes
	 * that represent special actions like an enter action, input action or exit action.
	 * <p>
	 * @param data - data. Null values permitted.
	 */
	public synchronized void setResultData(Object data) {
		if ( data == null ) {
			resultData = null;
		} else if ( ! resultDataType.isInstance(data) ) {
			throw new IllegalArgumentException("Unexpected data type: " + data.getClass());
		} else {
			resultData = data;
		}
	}
	
	/**
	 * Get result data.
	 * <p>
	 * @return data or null if no data
	 * @throws IllegalArgumentException - the type of data mismatch with declared result data type
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T getResultData() {
		return (T) resultData;
	}
	
}
