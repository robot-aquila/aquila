package ru.prolib.aquila.quik.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.*;
import ru.prolib.aquila.core.data.row.RowHandler;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.subsys.row.*;

/**
 * 2012-09-08<br>
 * $Id$
 */
public class QUIKListenerFactoryImplTest {
	private IMocksControl control;
	private QUIKListenerFactoryImpl factory;
	private QUIKConfigImpl config;
	private QUIKCompFactory fcomp;
	private EditablePortfolios portfolios;
	private EditableOrders orders;
	private EditableOrders stopOrders;
	private QUIKServiceLocator locator;
	private EditableTerminal terminal;
	private Handlers rowHandlers;
	private RowSetBuilderFactory rowSetBuilders;
	private DDETableRowSetBuilder rowSetBuilder;
	private RowHandler rowHandler;

	/**
	 * Сбросить конфигурацию таблиц до нормального состояния.
	 * <p>
	 * Для таблиц устанавливаются валидные ненулевые имена.
	 */
	private void resetTableConfig() {
		config.allDeals = "deals";
		config.orders = "orders";
		config.portfoliosFUT = "port-fut";
		config.portfoliosSTK = "port-stk";
		config.positionsFUT = "pos-fut";
		config.positionsSTK = "pos-stk";
		config.securities = "securities";
		config.stopOrders = "stop-orders";
	}
	
	@Before
	public void setUp() throws Exception {
		config = new QUIKConfigImpl();
		resetTableConfig();
		control = createStrictControl();
		rowHandlers = control.createMock(Handlers.class);
		rowHandler = control.createMock(RowHandler.class);
		rowSetBuilders = control.createMock(RowSetBuilderFactory.class);
		rowSetBuilder = control.createMock(DDETableRowSetBuilder.class);
		
		fcomp = control.createMock(QUIKCompFactory.class);
		portfolios = control.createMock(EditablePortfolios.class);
		orders = control.createMock(EditableOrders.class);
		stopOrders = control.createMock(EditableOrders.class);
		locator = control.createMock(QUIKServiceLocator.class);
		terminal = control.createMock(EditableTerminal.class);
		expect(locator.getCompFactory()).andStubReturn(fcomp);
		expect(locator.getTerminal()).andStubReturn(terminal);
		expect(locator.getConfig()).andStubReturn(config);
		expect(terminal.getPortfoliosInstance()).andStubReturn(portfolios);
		expect(terminal.getOrdersInstance()).andStubReturn(orders);
		expect(terminal.getStopOrdersInstance()).andStubReturn(stopOrders);
		
		factory = new QUIKListenerFactoryImpl(locator,
				rowHandlers, rowSetBuilders);
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertEquals(0x04, QUIKListenerFactoryImpl.VERSION);
		assertSame(locator, factory.getServiceLocator());
		assertSame(rowHandlers, factory.getRowHandlers());
		assertSame(rowSetBuilders, factory.getRowSetBuilders());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		EventSystem es = control.createMock(EventSystem.class);
		expect(locator.getEventSystem()).andStubReturn(es);
		QUIKListenerFactory expected = new QUIKListenerFactoryImpl(locator,
			new Handlers(es, terminal, new Modifiers(terminal)),
			new RowSetBuilderFactory(locator, new RowAdapters(locator)));
		control.replay();
		
		assertEquals(expected, new QUIKListenerFactoryImpl(locator));
		
		control.verify();
	}
	
	@Test
	public void testListenAllDeals() throws Exception {
		expect(rowSetBuilders.createAllDealsRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createTradeHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("deals",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
		
		assertEquals(expected, factory.listenAllDeals());
		
		control.verify();
	}
	
	@Test
	public void testListenSecurities() throws Exception {
		expect(rowSetBuilders.createSecurityRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createSecurityHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("securities",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
		
		assertEquals(expected, factory.listenSecurities());
		
		control.verify();
	}
	
	@Test
	public void testListenPortfolioSTK() throws Exception {
		expect(rowSetBuilders.createPortfolioStkRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createPortfolioHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("port-stk",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
	
		assertEquals(expected, factory.listenPortfoliosSTK());
	
		control.verify();
	}

	@Test
	public void testListenPortfolioFUT() throws Exception {
		expect(rowSetBuilders.createPortfolioFutRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createPortfolioHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("port-fut",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();

		assertEquals(expected, factory.listenPortfoliosFUT());

		control.verify();
	}
	
	@Test
	public void testListenPositionSTK() throws Exception {
		expect(rowSetBuilders.createPositionStkRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createPositionHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("pos-stk",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
		
		assertEquals(expected, factory.listenPositionsSTK());
		
		control.verify();
	}

	@Test
	public void testListenPositionFUT() throws Exception {
		expect(rowSetBuilders.createPositionFutRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createPositionHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("pos-fut",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
	
		assertEquals(expected, factory.listenPositionsFUT());
	
		control.verify();
	}
	
	@Test
	public void testListenOrders() throws Exception {
		expect(rowSetBuilders.createOrderRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createOrderHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("orders",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
		
		assertEquals(expected, factory.listenOrders());
		
		control.verify();
	}

	@Test
	public void testListenStopOrders() throws Exception {
		expect(rowSetBuilders.createStopOrderRowSetBuilder())
			.andReturn(rowSetBuilder);
		expect(rowHandlers.createStopOrderHandler()).andReturn(rowHandler);
		DDETableListener expected = new DDETableListener("stop-orders",
				new DDETableHandlerImpl(rowSetBuilder, rowHandler));
		control.replay();
		
		assertEquals(expected, factory.listenStopOrders());
		
		control.verify();
	}
	
	@Test
	public void testCreateDependencies() throws Exception {
		control.replay();
		Dependencies<String> expected = new Deps<String>()
			.setDependency("deals", "securities")
			.setDependency("pos-fut", "securities")
			.setDependency("pos-stk", "securities")
			.setDependency("pos-fut", "port-fut")
			.setDependency("pos-stk", "port-stk")
			.setDependency("orders", "securities")
			.setDependency("orders", "pos-fut")
			.setDependency("orders", "pos-stk")
			.setDependency("stop-orders", "securities")
			.setDependency("stop-orders", "pos-fut")
			.setDependency("stop-orders", "pos-stk");
		assertEquals(expected, factory.createDependencies());
	}
	
	@Test
	public void testCreateDependecies_ThrowsIfNullTable() throws Exception {
		control.replay();
		Runnable action[] = {
			new Runnable(){public void run() { config.allDeals = null;}},
			new Runnable(){public void run() { config.orders = null;}},
			new Runnable(){public void run() { config.portfoliosFUT = null;}},
			new Runnable(){public void run() { config.portfoliosSTK = null;}},
			new Runnable(){public void run() { config.positionsFUT = null;}},
			new Runnable(){public void run() { config.positionsSTK = null;}},
			new Runnable(){public void run() { config.securities = null;}},
			new Runnable(){public void run() { config.stopOrders = null;}},
		};
		for ( int i = 0; i < action.length; i ++ ) {
			resetTableConfig();
			action[i].run();
			Exception occ = null;
			try {
				factory.createDependencies();
			} catch ( NullPointerException e ) {
				occ = e;
			}
			assertNotNull("At #" + i + ": expected NullPointerException", occ);
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<Handlers> vRhf = new Variant<Handlers>(vLoc)
			.add(control.createMock(Handlers.class))
			.add(rowHandlers);
		Variant<RowSetBuilderFactory> vRsb =
				new Variant<RowSetBuilderFactory>(vRhf)
			.add(control.createMock(RowSetBuilderFactory.class))
			.add(rowSetBuilders);
		Variant<?> iterator = vRsb;
		int foundCnt = 0;
		QUIKListenerFactoryImpl found = null, x = null;
		do {
			x = new QUIKListenerFactoryImpl(vLoc.get(), vRhf.get(), vRsb.get());
			if ( factory.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(rowHandlers, found.getRowHandlers());
		assertSame(rowSetBuilders, found.getRowSetBuilders());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121111, 110347)
			.append(locator)
			.append(rowHandlers)
			.append(rowSetBuilders)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}

}
