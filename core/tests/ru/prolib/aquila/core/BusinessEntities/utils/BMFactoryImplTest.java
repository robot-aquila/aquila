package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.utils.*;

/**
 * 2012-08-17<br>
 * $Id: BMFactoryImplTest.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class BMFactoryImplTest {
	private IMocksControl control;
	private EditableTerminal term;
	private EventSystem eventSystem;
	private BMFactoryImpl factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		term = control.createMock(EditableTerminal.class);
		eventSystem = control.createMock(EventSystem.class);
		factory = new BMFactoryImpl(eventSystem, term);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Object fixture[][] = {
				// event sys, terminal, exception?
				{ eventSystem, term, false },
				{ null,		   term, true  },
				{ eventSystem, null,	 true  },
				{ null,		   null,	 true  }
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			eventSystem = (EventSystem) fixture[i][0];
			term = (EditableTerminal) fixture[i][1];
			boolean exception = false;
			try {
				factory = new BMFactoryImpl(eventSystem, term);
			} catch ( NullPointerException e ) {
				exception = true;
			}
			assertEquals(msg, (Boolean) fixture[i][2], exception);
			if ( ! exception ) {
				assertSame(msg, eventSystem, factory.getEventSystem());
				assertSame(msg, term, factory.getTerminal());
			}
		}
	}
	
	@Test
	public void testCreateSecurities() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onAvail = control.createMock(EventType.class);
		EventType onChanged = control.createMock(EventType.class);
		EventType onTrade = control.createMock(EventType.class);
		expect(eventSystem.createEventDispatcher("Securities"))
			.andReturn(dispatcher);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnAvailable")))
				.andReturn(onAvail);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnChanged")))
				.andReturn(onChanged);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnTrade")))
				.andReturn(onTrade);
		control.replay();
		
		SecuritiesImpl actual = (SecuritiesImpl) factory.createSecurities();
		
		control.verify();
		assertSame(dispatcher, actual.getEventDispatcher());
		assertSame(onAvail, actual.OnSecurityAvailable());
		assertSame(onChanged, actual.OnSecurityChanged());
		assertSame(onTrade, actual.OnSecurityTrade());
		assertEquals(0, actual.getSecuritiesCount());
		assertEquals(new Vector<Security>(), actual.getSecurities());
	}
	
	@Test
	public void testCreatePortfolios() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onAvail = control.createMock(EventType.class);
		EventType onChanged = control.createMock(EventType.class);
		EventType onPosAvail = control.createMock(EventType.class);
		EventType onPosChanged = control.createMock(EventType.class);
		expect(eventSystem.createEventDispatcher(eq("Portfolios")))
			.andReturn(dispatcher);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnAvailable")))
				.andReturn(onAvail);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnChanged")))
				.andReturn(onChanged);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnPositionAvailable")))
				.andReturn(onPosAvail);
		expect(eventSystem.createGenericType(same(dispatcher),
				eq("OnPositionChanged")))
				.andReturn(onPosChanged);
		control.replay();
		
		PortfoliosImpl ports = (PortfoliosImpl) factory.createPortfolios();
		
		control.verify();
		assertNotNull(ports);
		assertSame(dispatcher, ports.getEventDispatcher());
		assertSame(onAvail, ports.OnPortfolioAvailable());
		assertSame(onPosAvail, ports.OnPositionAvailable());
		assertSame(onPosChanged, ports.OnPositionChanged());
	}
	
	@Test
	public void testCreateOrders() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onAvail = control.createMock(EventType.class);
		EventType onCancelFailed = control.createMock(EventType.class);
		EventType onCancelled = control.createMock(EventType.class);
		EventType onChanged = control.createMock(EventType.class);
		EventType onDone = control.createMock(EventType.class);
		EventType onFailed = control.createMock(EventType.class);
		EventType onFilled = control.createMock(EventType.class);
		EventType onPartiallyFilled = control.createMock(EventType.class);
		EventType onRegistered = control.createMock(EventType.class);
		EventType onRegisterFailed = control.createMock(EventType.class);
		
		expect(eventSystem.createEventDispatcher("Orders"))
			.andReturn(dispatcher);
		expect(eventSystem.createGenericType(dispatcher, "OnAvailable"))
			.andReturn(onAvail);
		expect(eventSystem.createGenericType(dispatcher, "OnCancelFailed"))
			.andReturn(onCancelFailed);
		expect(eventSystem.createGenericType(dispatcher, "OnCancelled"))
			.andReturn(onCancelled);
		expect(eventSystem.createGenericType(dispatcher, "OnChanged"))
			.andReturn(onChanged);
		expect(eventSystem.createGenericType(dispatcher, "OnDone"))
			.andReturn(onDone);
		expect(eventSystem.createGenericType(dispatcher, "OnFailed"))
			.andReturn(onFailed);
		expect(eventSystem.createGenericType(dispatcher, "OnFilled"))
			.andReturn(onFilled);
		expect(eventSystem.createGenericType(dispatcher, "OnPartiallyFilled"))
			.andReturn(onPartiallyFilled);
		expect(eventSystem.createGenericType(dispatcher, "OnRegistered"))
				.andReturn(onRegistered);
		expect(eventSystem.createGenericType(dispatcher, "OnRegisterFailed"))
				.andReturn(onRegisterFailed);
		control.replay();
		
		OrdersImpl orders = (OrdersImpl) factory.createOrders();
		
		control.verify();
		assertNotNull(orders);
		assertSame(dispatcher, orders.getEventDispatcher());
		assertSame(onAvail, orders.OnOrderAvailable());
		assertSame(onCancelFailed, orders.OnOrderCancelFailed());
		assertSame(onCancelled, orders.OnOrderCancelled());
		assertSame(onChanged, orders.OnOrderChanged());
		assertSame(onDone, orders.OnOrderDone());
		assertSame(onFailed, orders.OnOrderFailed());
		assertSame(onFilled, orders.OnOrderFilled());
		assertSame(onPartiallyFilled, orders.OnOrderPartiallyFilled());
		assertSame(onRegistered, orders.OnOrderRegistered());
		assertSame(onRegisterFailed, orders.OnOrderRegisterFailed());
	}
	
	@Test
	public void testCreateOrderFactory() throws Exception {
		OrderFactory expected = new OrderFactoryImpl(eventSystem, term);
		assertEquals(expected, factory.createOrderFactory());
	}

	@Test
	public void testCreatePortfolioFactory() throws Exception {
		assertEquals(new PortfolioFactoryImpl(eventSystem, term),
					 factory.createPortfolioFactory());
	}

	@Test
	public void testCreatePositionFactory() throws Exception {
		assertEquals(
				new PositionFactoryImpl(eventSystem, new Account("ZULU"), term),
				factory.createPositionFactory(new Account("ZULU")));
	}

	@Test
	public void testCreateTradeFactory() throws Exception {
		TradeFactory expected = new TradeFactoryImpl(term);
		assertEquals(expected, factory.createTradeFactory());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(eventSystem)
			.add(control.createMock(EventSystem.class));
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vEs)
			.add(term)
			.add(control.createMock(EditableTerminal.class));
		int foundCnt = 0;
		BMFactoryImpl found = null, x = null;
		do {
			x = new BMFactoryImpl(vEs.get(), vTerm.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vTerm.next() );
		assertEquals(1, foundCnt);
		assertSame(eventSystem, found.getEventSystem());
		assertSame(term, found.getTerminal());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(this));
		assertFalse(factory.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 161555)
			.append(eventSystem)
			.append(term)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}
	
	@Test
	public void testCreateOrderBuilder0() throws Exception {
		OrderBuilder expected = new OrderBuilderImpl(
				new OrderFactoryImpl(eventSystem, term), term);
		assertEquals(expected, factory.createOrderBuilder());
	}
	
	@Test
	public void testCreateOrderBuilder1() throws Exception {
		Counter transId = control.createMock(Counter.class);
		OrderBuilder expected = new OrderBuilderImpl(
				new OrderFactoryImpl(eventSystem, term), term, transId);
		assertEquals(expected, factory.createOrderBuilder(transId));
	}
	
	@Test
	public void testCreateOrderBuilder2() throws Exception {
		Counter transId = control.createMock(Counter.class);
		OrderFactory ofactory = control.createMock(OrderFactory.class);
		OrderBuilder expected = new OrderBuilderImpl(ofactory, term, transId);
		assertEquals(expected, factory.createOrderBuilder(transId, ofactory));
	}
	
	@Test
	public void testCreateOrderEG0() throws Exception {
		EditableOrders orders = control.createMock(EditableOrders.class);
		expect(term.getOrdersInstance()).andReturn(orders);
		EditableEventGenerator<EditableOrder> expected =
			new EditableEventGenerator<EditableOrder>(
					new FireOrderAvailable(orders));
		control.replay();
		assertEquals(expected, factory.createOrderEG());
		control.verify();
	}
	
	@Test
	public void testCreateOrderEG1() throws Exception {
		EditableOrders orders = control.createMock(EditableOrders.class);
		Validator isAvailable = control.createMock(Validator.class); 
		expect(term.getOrdersInstance()).andReturn(orders);
		EditableEventGenerator<EditableOrder> expected =
			new EditableEventGenerator<EditableOrder>(isAvailable,
					new FireOrderAvailable(orders));
		control.replay();
		assertEquals(expected, factory.createOrderEG(isAvailable));
		control.verify();
	}

	@Test
	public void testCreateStopOrderEG0() throws Exception {
		EditableOrders stopOrders = control.createMock(EditableOrders.class);
		expect(term.getStopOrdersInstance()).andReturn(stopOrders);
		EditableEventGenerator<EditableOrder> expected =
			new EditableEventGenerator<EditableOrder>(
					new FireOrderAvailable(stopOrders));
		control.replay();
		assertEquals(expected, factory.createStopOrderEG());
		control.verify();
	}
	
	@Test
	public void testCreateStopOrderEG1() throws Exception {
		EditableOrders stopOrders = control.createMock(EditableOrders.class);
		Validator isAvailable = control.createMock(Validator.class); 
		expect(term.getStopOrdersInstance()).andReturn(stopOrders);
		EditableEventGenerator<EditableOrder> expected =
			new EditableEventGenerator<EditableOrder>(isAvailable,
					new FireOrderAvailable(stopOrders));
		control.replay();
		assertEquals(expected, factory.createStopOrderEG(isAvailable));
		control.verify();
	}
	
	@Test
	public void testCreatePortfolioEG0() throws Exception {
		EditablePortfolios ports = control.createMock(EditablePortfolios.class);
		expect(term.getPortfoliosInstance()).andReturn(ports);
		EditableEventGenerator<EditablePortfolio> expected =
			new EditableEventGenerator<EditablePortfolio>(
					new FirePortfolioAvailable(ports));
		control.replay();
		assertEquals(expected, factory.createPortfolioEG());
		control.verify();
	}
	
	@Test
	public void testCreatePortfolioEG1() throws Exception {
		EditablePortfolios ports = control.createMock(EditablePortfolios.class);
		Validator isAvailable = control.createMock(Validator.class); 
		expect(term.getPortfoliosInstance()).andReturn(ports);
		EditableEventGenerator<EditablePortfolio> expected =
			new EditableEventGenerator<EditablePortfolio>(isAvailable,
					new FirePortfolioAvailable(ports));
		control.replay();
		assertEquals(expected, factory.createPortfolioEG(isAvailable));
		control.verify();
	}

	@Test
	public void testCreatePositionEG0() throws Exception {
		EditablePortfolios ports = control.createMock(EditablePortfolios.class);
		expect(term.getPortfoliosInstance()).andReturn(ports);
		EditableEventGenerator<EditablePosition> expected =
			new EditableEventGenerator<EditablePosition>(
					new FirePositionAvailableAuto(ports));
		control.replay();
		assertEquals(expected, factory.createPositionEG());
		control.verify();
	}
	
	@Test
	public void testCreatePositionEG1() throws Exception {
		EditablePortfolios ports = control.createMock(EditablePortfolios.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(term.getPortfoliosInstance()).andReturn(ports);
		EditableEventGenerator<EditablePosition> expected =
			new EditableEventGenerator<EditablePosition>(isAvailable, 
					new FirePositionAvailableAuto(ports));
		control.replay();
		assertEquals(expected, factory.createPositionEG(isAvailable));
		control.verify();
	}

	@Test
	public void testCreateSecurityEG0() throws Exception {
		EditableSecurities secs = control.createMock(EditableSecurities.class);
		expect(term.getSecuritiesInstance()).andReturn(secs);
		EditableEventGenerator<EditableSecurity> expected =
			new EditableEventGenerator<EditableSecurity>(
					new FireSecurityAvailable(secs));
		control.replay();
		assertEquals(expected, factory.createSecurityEG());
		control.verify();
	}
	
	@Test
	public void testCreateSecurityEG1() throws Exception {
		EditableSecurities secs = control.createMock(EditableSecurities.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(term.getSecuritiesInstance()).andReturn(secs);
		EditableEventGenerator<EditableSecurity> expected =
			new EditableEventGenerator<EditableSecurity>(isAvailable,
					new FireSecurityAvailable(secs));
		control.replay();
		assertEquals(expected, factory.createSecurityEG(isAvailable));
		control.verify();
	}

}
