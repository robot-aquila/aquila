package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Процедура запуска терминала.
 */
public class TerminalStartSequence implements Runnable {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final CountDownLatch started;
	
	static {
		logger = LoggerFactory.getLogger(TerminalStartSequence.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal целевой терминал
	 * @param started индикатор запуска
	 */
	public TerminalStartSequence(EditableTerminal terminal,
								 CountDownLatch started)
	{
		super();
		this.terminal = terminal;
		this.started = started;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить индикатор запуска.
	 * <p>
	 * @return счетчик
	 */
	public CountDownLatch getStartedIndicator() {
		return started;
	}

	@Override
	public void run() {
		started.countDown();
		try {
			terminal.getStarter().start();
			terminal.setTerminalState(TerminalState.STARTED);
			terminal.fireTerminalStartedEvent();
		} catch ( Exception e ) {
			logger.error("Couldn't start terminal: ", e);
			terminal.setTerminalState(TerminalState.STOPPED);
		}
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == TerminalStartSequence.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		TerminalStartSequence o = (TerminalStartSequence) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(started, o.started)
			.isEquals();
	}


}
