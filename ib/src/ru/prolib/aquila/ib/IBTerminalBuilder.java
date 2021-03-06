package ru.prolib.aquila.ib;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalEventDispatcher;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.Cache;

/**
 * Конструктор терминала IB.
 */
public class IBTerminalBuilder extends TerminalBuilder {
	
	public IBTerminalBuilder() {
		super();
	}
	
	@Override
	public IBEditableTerminal createTerminal(String queueId) {
		return (IBEditableTerminal) super.createTerminal(queueId);
	}
	
	@Override
	protected EditableTerminal createTerminalInstance(EventSystem es,
			StarterQueue starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			TerminalEventDispatcher dispatcher)
	{
		EventDispatcher cacheDisp = es.createEventDispatcher("Cache");
		Cache cache = new Cache(cacheDisp, cacheDisp.createType("Contract"),
			cacheDisp.createType("Order"), cacheDisp.createType("OrderStatus"),
			cacheDisp.createType("Position"), cacheDisp.createType("Exec"));
		IBClient client = new IBClient();
		IBEditableTerminal terminal = new IBTerminalImpl(es, starter,
				securities, portfolios, orders, dispatcher, cache, client);
		return terminal;
	}

}
