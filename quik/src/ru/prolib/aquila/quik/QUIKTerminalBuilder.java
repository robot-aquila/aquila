package ru.prolib.aquila.quik;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.*;

/**
 * Конструктор терминала QUIK.
 */
public class QUIKTerminalBuilder extends TerminalBuilder {

	public QUIKTerminalBuilder() {
		super();
	}
	
	@Override
	public QUIKEditableTerminal createTerminal(String queueId) {
		return (QUIKEditableTerminal) super.createTerminal(queueId);
	}
	
	@Override
	protected EditableTerminal createTerminalInstance(EventSystem es,
			StarterQueue starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			EventDispatcher dispatcher,
			EventType onConnected, EventType onDisconnected,
			EventType onStarted, EventType onStopped, EventType onPanic,
			EventType onReqSecurityError)
	{
		return new QUIKTerminalImpl(es, starter,
				securities, portfolios, orders, dispatcher,
				onConnected, onDisconnected, onStarted, onStopped, onPanic,
				onReqSecurityError,
				new CacheBuilder().createCache(es), new QUIKClient());
	}

}
