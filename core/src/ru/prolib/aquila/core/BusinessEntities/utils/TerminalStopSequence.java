package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Процедура останова терминала.
 */
public class TerminalStopSequence implements Runnable {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final CountDownLatch started;
	
	static {
		logger = LoggerFactory.getLogger(TerminalStopSequence.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal целевой терминал
	 * @param started индикатор запуска
	 */
	public TerminalStopSequence(EditableTerminal terminal,
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
			// В любом случае соединение будет разорвано. Событие об этом
			// должно быть сгенерировано в первую очередь, иначе оно
			// будет отфильтровано, не дойдет до потребителя или очередь
			// событий станет недоступна.
			terminal.fireTerminalDisconnectedEvent();
			// Терминал будет остановлен в любом случае и событие об этом
			// должно быть сгенерировано, что бы потребители могли получить
			// его.
			terminal.fireTerminalStoppedEvent();
			// Теперь необходимо остановить все работающие подсистемы. Если
			// во время процедуры останова возникнет ошибка, она не будет
			// иметь значения. Терминал будет переведен в состояние останова
			// а ошибка будет выведена в журнал.
			terminal.getStarter().stop();
		} catch ( Exception e ) {
			logger.error("Exception during stop terminal: ", e);
		}
		// Независимо от наличия ошибок в процессе останова,
		// терминал будет переведен в состояние останова.
		// Делать это выше нельзя, так как этот статус
		// исключает последующую генерацию события отключения. 
		terminal.setTerminalState(TerminalState.STOPPED);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == TerminalStopSequence.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		TerminalStopSequence o = (TerminalStopSequence) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(started, o.started)
			.isEquals();
	}

}
