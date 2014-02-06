package ru.prolib.aquila.probe;

import java.util.Properties;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;

/**
 * Фабрика эмулятора терминала.
 */
public class PROBEFactory implements TerminalFactory {
	private static final String ID_PREFIX = "Probe";
	private static final Counter id = new SimpleCounter();
	
	/**
	 * Конструктор.
	 */
	public PROBEFactory() {
		super();
	}
	
	/**
	 * Получить следующий идентификатор.
	 * <p>
	 * Формирует идентификатор очередного экземпляра терминала.
	 * <p>
	 * @return идентификатор
	 */
	private static final String getNextId() {
		return ID_PREFIX + id.incrementAndGet();
	}

	@Override
	public Terminal createTerminal(Properties config) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
