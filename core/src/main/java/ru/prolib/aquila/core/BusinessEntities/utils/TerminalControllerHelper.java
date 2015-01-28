package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.concurrent.CountDownLatch;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;

/**
 * Помощник контроллера терминала.
 * <p>
 * Фабрика, инстанцирующая объекты, необходимые для работы контроллера.
 * Класс введен исключетельно для тестирования контроллера терминала.
 */
public class TerminalControllerHelper {
	
	/**
	 * Конструктор.
	 */
	public TerminalControllerHelper() {
		super();
	}
	
	/**
	 * Создать индикатор запуска.
	 * <p>
	 * @return счетчик
	 */
	public CountDownLatch createStartedSignal() {
		return new CountDownLatch(1);
	}
	
	/**
	 * Создать поток.
	 * <p>
	 * @param runnable процедура
	 * @return объект потока
	 */
	public Thread createThread(Runnable runnable) {
		return new Thread(runnable);
	}
	
	/**
	 * Создать процедуру запуска терминала.
	 * <p>
	 * @param terminal терминал
	 * @param started индикатор запуска процедуры
	 * @return процедура
	 */
	public Runnable createStartSequence(EditableTerminal terminal,
			CountDownLatch started)
	{
		return new TerminalStartSequence(terminal, started);
	}
	
	/**
	 * Создать процедуру останова терминала.
	 * <p>
	 * @param terminal терминал
	 * @param started индикатор запуска процедуры
	 * @return процедура
	 */
	public Runnable createStopSequence(EditableTerminal terminal,
			CountDownLatch started)
	{
		return new TerminalStopSequence(terminal, started);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == TerminalControllerHelper.class;
	}

}
