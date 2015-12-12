package ru.prolib.aquila.probe.internal;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.PROBETerminal;

/**
 * Поставщик данных.
 * <p>
 * Данный класс представляет собой фасад к сервисам поставки данных.
 */
public class DataProvider {
	
	public DataProvider() {
		super();
	}

	/**
	 * Запустить эмуляцию инструмента.
	 * <p>
	 * @param terminal - the terminal
	 * @param symbol дескриптор инструмента
	 * @param startTime время начала данных
	 * @throws DataException ошибка инициализации поставщика
	 */
	public void startSupply(PROBETerminal terminal, Symbol symbol,
			DateTime startTime) throws DataException
	{
		throw new RuntimeException("Not implemented");
		/*
		PROBEServiceLocator locator = terminal.getServiceLocator();
		PROBEDataStorage ds = locator.getDataStorage();
		Aqiterator<Tick> it = ds.getIterator(symbol, startTime);
		locator.getTimeline().registerSource(new TickDataDispatcher(it,
				new FORTSSecurityCtrl(terminal,
						terminal.getEditableSecurity(symbol),
						ds.getSecurityProperties(symbol))));
		*/
	}

}
