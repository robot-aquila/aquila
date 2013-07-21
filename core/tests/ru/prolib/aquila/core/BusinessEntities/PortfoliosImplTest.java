package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfolioFactory;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-16<br>
 * $Id: PortfoliosImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PortfoliosImplTest {
	private static Account account1, account2, account3;
	private static EventSystem es;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EventDispatcher dispatcher;
	private EventType onAvailable, onChanged, onPosAvailable, onPosChanged;
	private PortfoliosImpl portfolios;
	private EditablePortfolio p1, p2, p3;
	private PortfolioFactory factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		es = new EventSystemImpl();
		account1 = new Account("LX-001");
		account2 = new Account("ZX-008");
		account3 = new Account("MM-112");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		dispatcher = control.createMock(EventDispatcher.class);
		p1 = control.createMock(EditablePortfolio.class);
		p2 = control.createMock(EditablePortfolio.class);
		p3 = control.createMock(EditablePortfolio.class);
		factory = control.createMock(PortfolioFactory.class);
		
		onAvailable = control.createMock(EventType.class);
		onChanged = control.createMock(EventType.class);
		onPosAvailable = control.createMock(EventType.class);
		onPosChanged = control.createMock(EventType.class);
		portfolios = new PortfoliosImpl(dispatcher, onAvailable, onChanged,
				onPosAvailable, onPosChanged, factory);
		expect(terminal.getEventSystem()).andStubReturn(es);
		expect(p1.getAccount()).andStubReturn(account1);
		expect(p2.getAccount()).andStubReturn(account2);
		expect(p3.getAccount()).andStubReturn(account3);
	}
	
	@Test
	public void testIsPortfoioAvailable() throws Exception {
		portfolios.setPortfolio(account1, p1);
		portfolios.setPortfolio(account3, p3);
		
		assertTrue(portfolios.isPortfolioAvailable(account1));
		assertFalse(portfolios.isPortfolioAvailable(account2));
	}
	
	@Test
	public void testOnPortfolioAvailable() throws Exception {
		assertSame(onAvailable, portfolios.OnPortfolioAvailable());
	}
	
	@Test
	public void testGetPortfolios() throws Exception {
		portfolios.setPortfolio(account1, p1);
		portfolios.setPortfolio(account2, p2);
		
		List<Portfolio> expected = new Vector<Portfolio>();
		expected.add(p1);
		expected.add(p2);
		
		assertEquals(expected, portfolios.getPortfolios());
	}
	
	@Test (expected=PortfolioNotExistsException.class)
	public void testGetPortfolio_ThrowsIfNotExists() throws Exception {
		portfolios.getPortfolio(account1);
	}
	
	@Test
	public void testGetPortfolio() throws Exception {
		portfolios.setPortfolio(account1, p1);
		
		assertSame(p1, portfolios.getPortfolio(account1));
	}

	@Test (expected=PortfolioNotExistsException.class)
	public void testGetDefaultPortfolio_ThrowsIfNotExists() throws Exception {
		portfolios.getDefaultPortfolio();
	}
	
	@Test
	public void testGetDefaultPortfolio() throws Exception {
		portfolios.setDefaultPortfolio(p1);
		assertSame(p1, portfolios.getDefaultPortfolio());
	}
	
	@Test
	public void testFireEvents_Available() throws Exception {
		expect(p1.isAvailable()).andReturn(false);
		p1.setAvailable(eq(true));
		dispatcher.dispatch(eq(new PortfolioEvent(onAvailable, p1)));
		p1.resetChanges();
		control.replay();
		
		portfolios.fireEvents(p1);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Changed() throws Exception {
		expect(p2.isAvailable()).andReturn(true);
		p2.fireChangedEvent();
		p2.resetChanges();
		control.replay();
		
		portfolios.fireEvents(p2);
		
		control.verify();
	}
	
	@Test
	public void testGetEditablePortfolio_CreateIfNotExists() throws Exception {
		portfolios.setDefaultPortfolio(p2);
		EventType onChng = control.createMock(EventType.class),
			onPosAvl = control.createMock(EventType.class),
			onPosChng = control.createMock(EventType.class);
		expect(p1.OnChanged()).andStubReturn(onChng);
		expect(p1.OnPositionAvailable()).andStubReturn(onPosAvl);
		expect(p1.OnPositionChanged()).andStubReturn(onPosChng);
		
		expect(factory.createInstance(terminal, account1)).andReturn(p1);
		onChng.addListener(portfolios);
		onPosAvl.addListener(portfolios);
		onPosChng.addListener(portfolios);
		control.replay();
		
		EditablePortfolio actual =
			portfolios.getEditablePortfolio(terminal, account1);
		
		control.verify();
		assertSame(p1, actual);
		assertSame(p1, portfolios.getPortfolio(account1));
		assertSame(p2, portfolios.getDefaultPortfolio());
	}
	
	@Test
	public void testGetEditablePortfolio_SetDefault() throws Exception {
		EventType onChng = control.createMock(EventType.class),
			onPosAvl = control.createMock(EventType.class),
			onPosChng = control.createMock(EventType.class);
		expect(p1.OnChanged()).andStubReturn(onChng);
		expect(p1.OnPositionAvailable()).andStubReturn(onPosAvl);
		expect(p1.OnPositionChanged()).andStubReturn(onPosChng);
		
		expect(factory.createInstance(terminal, account1)).andReturn(p1);
		onChng.addListener(portfolios);
		onPosAvl.addListener(portfolios);
		onPosChng.addListener(portfolios);
		control.replay();
		
		EditablePortfolio actual =
			portfolios.getEditablePortfolio(terminal, account1);
		
		control.verify();
		assertSame(p1, actual);
		assertSame(p1, portfolios.getPortfolio(account1));
		assertSame(p1, portfolios.getDefaultPortfolio());
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		portfolios.setPortfolio(account2, p2);
		assertSame(p2, portfolios.getEditablePortfolio(terminal, account2));
	}
	
	@Test
	public void testOnPortfolioChanged() throws Exception {
		assertSame(onChanged, portfolios.OnPortfolioChanged());
	}
	
	@Test
	public void testOnPositionAvailable() throws Exception {
		assertSame(onPosAvailable, portfolios.OnPositionAvailable());
	}

	@Test
	public void testOnPositionChanged() throws Exception {
		assertSame(onPosChanged, portfolios.OnPositionChanged());
	}
	
	@Test
	public void testOnEvent_DispatchPortfolioChangedEvent() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(p1.OnChanged()).andStubReturn(type);
		dispatcher.dispatch(new PortfolioEvent(onChanged, p1));
		control.replay();
		
		portfolios.onEvent(new PortfolioEvent(type, p1));
		
		control.verify();
	}

	@Test
	public void testOnEvent_DispatchPositionAvailableEvent() throws Exception {
		Position pos = control.createMock(Position.class);
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(pos.getPortfolio()).andStubReturn(p2);
		expect(p2.OnPositionAvailable()).andStubReturn(type1);
		expect(p2.OnPositionChanged()).andStubReturn(type2);
		dispatcher.dispatch(new PositionEvent(onPosAvailable, pos));
		control.replay();
		
		portfolios.onEvent(new PositionEvent(type1, pos));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnPositionChanged() throws Exception {
		Position pos = control.createMock(Position.class);
		EventType type1 = control.createMock(EventType.class);
		EventType type2 = control.createMock(EventType.class);
		expect(pos.getPortfolio()).andStubReturn(p3);
		expect(p3.OnPositionChanged()).andStubReturn(type1);
		expect(p3.OnPositionAvailable()).andStubReturn(type2);
		dispatcher.dispatch(new PositionEvent(onPosChanged, pos));
		control.replay();
		
		portfolios.onEvent(new PositionEvent(type1, pos));
		
		control.verify();
	}

	@Test
	public void testGetPortfoliosCount() throws Exception {
		assertEquals(0, portfolios.getPortfoliosCount());
		portfolios.setPortfolio(account1, p1);
		assertEquals(1, portfolios.getPortfoliosCount());
		portfolios.setPortfolio(account2, p2);
		assertEquals(2, portfolios.getPortfoliosCount());
		portfolios.setPortfolio(account3, p3);
		assertEquals(3, portfolios.getPortfoliosCount());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(portfolios.equals(portfolios));
		assertFalse(portfolios.equals(null));
		assertFalse(portfolios.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<EditablePortfolio> list1 = new Vector<EditablePortfolio>();
		list1.add(p1);
		list1.add(p2);
		List<EditablePortfolio> list2 = new Vector<EditablePortfolio>();
		list2.add(p1);
		list2.add(p2);
		list2.add(p3);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vAvl = new Variant<EventType>(vDisp)
			.add(onAvailable)
			.add(control.createMock(EventType.class));
		Variant<EventType> vChng = new Variant<EventType>(vAvl)
			.add(onChanged)
			.add(control.createMock(EventType.class));
		Variant<EventType> vPosAvl = new Variant<EventType>(vChng)
			.add(onPosAvailable)
			.add(control.createMock(EventType.class));
		Variant<EventType> vPosChng = new Variant<EventType>(vPosAvl)
			.add(onPosChanged)
			.add(control.createMock(EventType.class));
		Variant<List<EditablePortfolio>> vList =
				new Variant<List<EditablePortfolio>>(vPosChng)
			.add(list1)
			.add(list2);
		Variant<EditablePortfolio> vDef = new Variant<EditablePortfolio>(vList)
			.add(p1)
			.add(null);
		Variant<PortfolioFactory> vFact = new Variant<PortfolioFactory>(vDef)
			.add(factory)
			.add(control.createMock(PortfolioFactory.class));
		Variant<?> iterator = vFact;
		int foundCnt = 0;
		PortfoliosImpl x = null, found = null;
		control.replay();
		for ( EditablePortfolio p : list1 ) {
			portfolios.setPortfolio(p.getAccount(), p);
		}
		portfolios.setDefaultPortfolio(p1);
		do {
			x = new PortfoliosImpl(vDisp.get(), vAvl.get(), vChng.get(),
					vPosAvl.get(), vPosChng.get(), vFact.get());
			for ( EditablePortfolio p : vList.get() ) {
				x.setPortfolio(p.getAccount(), p);
			}
			if ( vDef.get() != null ) {
				x.setDefaultPortfolio(vDef.get());
			}
			if ( portfolios.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onAvailable, found.OnPortfolioAvailable());
		assertEquals(onChanged, found.OnPortfolioChanged());
		assertEquals(onPosAvailable, found.OnPositionAvailable());
		assertEquals(onPosChanged, found.OnPositionChanged());
		assertEquals(list1, found.getPortfolios());
		assertSame(p1, found.getDefaultPortfolio());
		assertSame(factory, found.getFactory());
	}
	
	@Test
	public void testConstruct_DefaultFactory() throws Exception {
		PortfoliosImpl expected = new PortfoliosImpl(dispatcher, onAvailable,
			onChanged, onPosAvailable, onPosChanged, new PortfolioFactory());
		assertEquals(expected, new PortfoliosImpl(dispatcher, onAvailable,
			onChanged, onPosAvailable, onPosChanged));
	}

}
