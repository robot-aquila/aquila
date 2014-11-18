package ru.prolib.aquila.probe.internal;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.PROBETerminal;

/**
 * Типовой поставщик данных.
 * <p>
 * TODO: 
 */
public class DefaultDataProvider implements DataProvider {
	private final PROBETerminal terminal;
	
	public DefaultDataProvider(PROBETerminal terminal) {
		super();
		this.terminal = terminal;
	}

	@Override
	public void startSupply(SecurityDescriptor descr, DateTime startTime)
			throws DataException
	{
		PROBEServiceLocator locator = terminal.getServiceLocator();
		PROBEDataStorage ds = locator.getDataStorage();
		Aqiterator<Tick> it = ds.getIterator(descr, startTime);
		SecurityProperties props = ds.getSecurityProperties(descr); 
		EditableSecurity security = terminal.getEditableSecurity(descr);
		locator.getTimeline().registerSource(new TickDataDispatcher(it,
				new SecurityHandlerFORTS(terminal, security, props)));
	}

}
