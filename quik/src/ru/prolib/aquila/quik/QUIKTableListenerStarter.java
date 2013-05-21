package ru.prolib.aquila.quik;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.DependencyRule;
import ru.prolib.aquila.dde.utils.DDEObservableService;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.subsys.*;

/**
 * Пускач обозревателя таблиц DDE.
 * <p>
 * TODO: выпилить, после полного перехода на DDE-кэш
 * <p>
 * 2013-02-27<br>
 * $Id: QUIKTableListenerStarter.java 548 2013-02-27 03:01:50Z whirlwind $
 */
@Deprecated
public class QUIKTableListenerStarter implements Starter {
	private final QUIKServiceLocator locator;
	private final DDEObservableService service;
	private final List<DDETableListener> orderListeners;
	private DDETableOrder order;
	
	public QUIKTableListenerStarter(QUIKServiceLocator locator,
			DDEObservableService service)
	{
		super();
		this.locator = locator;
		this.service = service;
		orderListeners = new Vector<DDETableListener>();
	}
	

	@Override
	public synchronized void start() {
		QUIKListenerFactory lf = new QUIKListenerFactoryImpl(locator); 
		
		Map<String,DependencyRule> rules = new HashMap<String,DependencyRule>();
		//rules.put(locator.getConfig().getAllDeals(), DependencyRule.DROP);
		order = new DDETableOrder(
				locator.getEventSystem().createEventDispatcher(),
				lf.createDependencies(), rules);
		service.OnTable().addListener(order);
		
		//orderListeners.add(lf.listenSecurities());
		orderListeners.add(lf.listenAllDeals());
		//orderListeners.add(lf.listenPortfoliosSTK());
		//orderListeners.add(lf.listenPositionsSTK());
		//orderListeners.add(lf.listenPortfoliosFUT());
		//orderListeners.add(lf.listenPositionsFUT());
		//orderListeners.add(lf.listenOrders());
		//orderListeners.add(lf.listenStopOrders());
 
		for ( DDETableListener listener : orderListeners ) {
			order.addListener(listener);
		}
	}

	@Override
	public synchronized void stop() {
		for ( DDETableListener listener : orderListeners ) {
			order.removeListener(listener);
		}
		
		service.OnTable().removeListener(order);
	}

}
