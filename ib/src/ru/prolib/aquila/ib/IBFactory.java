package ru.prolib.aquila.ib;

import java.util.Properties;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.subsys.*;
import ru.prolib.aquila.ib.subsys.api.*;
import ru.prolib.aquila.ib.subsys.order.IBOrderProcessor;
import ru.prolib.aquila.ib.subsys.security.*;

/**
 * Фабрика IB-терминала.
 * <p>
 * 2012-11-24<br>
 * $Id: IBFactory.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBFactory implements TerminalFactory {
	public static long TIMEOUT = 3000;

	public IBFactory() {
		super();
	}
	
	public Terminal createTerminal(Properties cfg) {
		return createTerminal(createConfig(cfg));
	}
	
	public Terminal createTerminal() {
		return createTerminal(new IBConfig());
	}
	
	public Terminal createTerminal(IBConfig config) {
		TerminalDecorator termDecorator = new TerminalDecorator();
		IBServiceLocator locator = new IBServiceLocatorImpl(termDecorator);
		
		IBCompFactory fcomp = locator.getCompFactory();
		

		EditablePortfolios portfolios = fcomp.createPortfolios();
		EditableOrders orders = fcomp.createOrders();
		EditableOrders stopOrders = fcomp.createOrders();
		
		IBClient client = locator.getApiClient();
		IBHandler handler = new IBHandler(locator);

		EditableSecurities secStorage =
			fcomp.createSecurities("USD", SecurityType.STK);
		IBSecurities securities = new IBSecurities(secStorage,
			new IBSecurityHandlerFactoryImpl(locator, TIMEOUT));

		Counter transId = locator.getTransactionNumerator();
		OrderFactory orderFactory = fcomp.createOrderFactory();
		OrderBuilder orderBuilder =
			fcomp.createOrderBuilder(transId, orderFactory); 
		IBOrderProcessor orderProcessor = new IBOrderProcessor(client, transId);
		handler.start();
		StarterQueue starter = new StarterQueue()
			.add(new IBConnectionKeeper(locator,
					new IBClientStarter(client, config)))
			.add(new EventQueueStarter(locator.getEventSystem()
					.getEventQueue(), 1000))
			.add(new EventQueueStarter(locator.getApiEventSystem()
					.getEventQueue(), 1000));
		EventSystem es = locator.getEventSystem();
		EventDispatcher dispatcher = es.createEventDispatcher("IB");
		EditableTerminal terminal = new TerminalImpl(starter, securities,
				portfolios, orders, stopOrders,
				orderBuilder,
				orderProcessor,
				dispatcher,
				es.createGenericType(dispatcher, "OnConnected"),
				es.createGenericType(dispatcher, "OnDisconnected"),
				es.createGenericType(dispatcher, "OnStarted"),
				es.createGenericType(dispatcher, "OnStopped"),
				es.createGenericType(dispatcher, "OnPanic"));
		termDecorator.setTerminal(terminal);
		return termDecorator;
	}
	
	private IBConfig createConfig(Properties cfg) {
		return new IBConfig(cfg.getProperty("host"),
				Integer.parseInt(cfg.getProperty("port", "4001")),
				Integer.parseInt(cfg.getProperty("clientId", "0")));
	}

}
