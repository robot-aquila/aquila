package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.aquila.qforts.impl.QFOrderExecutionTriggerMode.*;

import org.apache.log4j.BasicConfigurator;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueFactory;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandlerStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DataSource;

public class QFBuilderTest {
	static Account account = new Account("TEST-ACCOUNT");
	static Symbol symbol = new Symbol("TEST-SYMBOL");
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	IMocksControl control;
	DataSource dsMock;
	QFSymbolDataService sdsMock;
	EventQueue queue;
	QFReactor reactor;
	EditableTerminal terminal;
	QFBuilder service;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueFactory().createDefault();
		reactor = null;
		terminal = null;
		control = createStrictControl();
		dsMock = control.createMock(DataSource.class);
		sdsMock = control.createMock(QFSymbolDataService.class);
		service = new QFBuilder();
	}

	@Test
	public void testBuildDataProvider_OrderExecTriggerMode_AsL1UpdateConsumer() throws Exception {
		reactor = (QFReactor) service.withEventQueue(queue)
			.withDataSource(dsMock)
			.withOrderExecutionTriggerMode(USE_L1UPDATES_WHEN_ORDER_APPEARS)
			.buildDataProvider();
		terminal = new BasicTerminalBuilder()
			.withDataProvider(reactor)
			.withEventQueue(queue)
			.buildTerminal();
		terminal.getEditableSecurity(symbol);
		terminal.getEditablePortfolio(account);
		Order order = terminal.createOrder(account, symbol, OrderAction.BUY, of(1L), of(200000L));
		dsMock.subscribeL1(symbol, reactor);
		control.replay();
		
		terminal.placeOrder(order);
		
		control.verify();
		assertEquals(USE_L1UPDATES_WHEN_ORDER_APPEARS, reactor.getOrderExecutionTriggerMode());
	}

	@Test
	public void testBuildDataProvider_OrderExecTriggerMode_UseLastTradeEventOfSecurity() throws Exception {
		service.withEventQueue(queue)
			.withDataSource(dsMock)
			.withOrderExecutionTriggerMode(USE_LAST_TRADE_EVENT_OF_SECURITY);
		AnnotationConfigApplicationContext c = (AnnotationConfigApplicationContext) service.getContext();
		c.registerBean("symbolDataService", QFSymbolDataService.class, () -> sdsMock);
		//c.refresh();
		reactor = (QFReactor) service.buildDataProvider();
		terminal = new BasicTerminalBuilder()
			.withDataProvider(reactor)
			.withEventQueue(queue)
			.buildTerminal();
		terminal.getEditableSecurity(symbol);
		terminal.getEditablePortfolio(account);
		Order order = terminal.createOrder(account, symbol, OrderAction.BUY, of(1L), of(200000L));
		expect(sdsMock.onSubscribe(symbol, MDLevel.L1)).andReturn(new SubscrHandlerStub());
		control.replay();
		
		terminal.placeOrder(order);
		
		control.verify();
		assertEquals(USE_LAST_TRADE_EVENT_OF_SECURITY, reactor.getOrderExecutionTriggerMode());
	}

}
