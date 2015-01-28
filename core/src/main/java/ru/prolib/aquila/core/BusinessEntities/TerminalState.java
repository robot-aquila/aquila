package ru.prolib.aquila.core.BusinessEntities;

/**
 * Статус терминала.
 * <p>
 * 2013-02-10<br>
 * $Id$
 */
public class TerminalState {
	/**
	 * Терминал остановлен.
	 */
	public static final TerminalState STOPPED;
	
	/**
	 * Терминал в процессе запуска.
	 */
	public static final TerminalState STARTING;
	
	/**
	 * Терминал в рабочем режиме (запущен).
	 */
	public static final TerminalState STARTED;
	
	/**
	 * Терминал подключен к удаленной системе.
	 */
	public static final TerminalState CONNECTED;
	
	/**
	 * Терминал в процессе останова.
	 */
	public static final TerminalState STOPPING;

	static {
		STOPPED = new TerminalState("STOPPED");
		STARTING = new TerminalState("STARTING");
		STARTED = new TerminalState("STARTED");
		CONNECTED = new TerminalState("CONNECTED");
		STOPPING = new TerminalState("STOPPING");
	}
	
	
	private final String code;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param code код статуса
	 */
	private TerminalState(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
