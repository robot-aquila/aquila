package ru.prolib.aquila.probe.internal;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

/**
 * Поставщик данных.
 * <p>
 * Данный класс представляет собой фасад к сервисам поставки данных.
 */
public class DataProvider {
	private final XFactory x;
	private final PROBETerminal terminal;
	
	public DataProvider(PROBETerminal terminal, XFactory x) {
		super();
		this.terminal = terminal;
		this.x = x;
	}

	/**
	 * Запустить эмуляцию инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param startTime время начала данных
	 * @throws DataException ошибка инициализации поставщика
	 */
	public void startSupply(SecurityDescriptor descr, DateTime startTime)
			throws DataException
	{
		PROBEServiceLocator locator = terminal.getServiceLocator();
		PROBEDataStorage ds = locator.getDataStorage();
		Aqiterator<Tick> it = ds.getIterator(descr, startTime);
		SecurityProperties props = ds.getSecurityProperties(descr); 
		EditableSecurity security = terminal.getEditableSecurity(descr);
		TickHandler th = x.newSecurityHandlerFORTS(terminal, security, props);
		locator.getTimeline().registerSource(x.newTickDataDispatcher(it, th));
	}

}
