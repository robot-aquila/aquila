package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
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
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onAvailable, onChanged, onPosAvailable, onPosChanged;
	private PortfoliosImpl portfolios;
	private EditablePortfolio p1, p2, p3;
	
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
		dispatcherMock = control.createMock(EventDispatcher.class);
		p1 = control.createMock(EditablePortfolio.class);
		p2 = control.createMock(EditablePortfolio.class);
		p3 = control.createMock(EditablePortfolio.class);
		
		dispatcher = es.createEventDispatcher("Portfolios");
		onAvailable = dispatcher.createType("OnAvailable");
		onChanged = dispatcher.createType("OnChanged");
		onPosAvailable = dispatcher.createType("OnPosAvailable");
		onPosChanged = dispatcher.createType("OnPosChanged");
		portfolios = new PortfoliosImpl(dispatcher, onAvailable, onChanged,
				onPosAvailable, onPosChanged);
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
		List<Portfolio> expected = new Vector<Portfolio>();
		assertEquals(expected, portfolios.getPortfolios());
		expected.add(p1);
		expected.add(p2);
		portfolios.setPortfolio(account1, p1);
		portfolios.setPortfolio(account2, p2);
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
	public void testFirePortfolioAvailableEvent() throws Exception {
		portfolios = new PortfoliosImpl(dispatcherMock, onAvailable, onChanged,
				onPosAvailable, onPosChanged);
		dispatcherMock.dispatch(new PortfolioEvent(onAvailable, p2));
		control.replay();
		
		portfolios.firePortfolioAvailableEvent(p2);
		
		control.verify();
	}

	@Test (expected=PortfolioNotExistsException.class)
	public void testGetEditablePortfolio_ThrowsIfNotExists() throws Exception {
		portfolios.getEditablePortfolio(account1);
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		portfolios.setPortfolio(account2, p2);
		assertSame(p2, portfolios.getEditablePortfolio(account2));
	}
	
	@Test (expected=PortfolioAlreadyExistsException.class)
	public void testCreatePortfolio_ThrowsIfAlreadyExists() throws Exception {
		portfolios.setPortfolio(account1, p1);
		portfolios.createPortfolio(terminal, account1);
	}

	@Test
	public void testCreatePortfolio() throws Exception {
		EventDispatcher d = es.createEventDispatcher("Portfolio[LX-001]");
		PortfolioImpl expected = new PortfolioImpl(terminal, account1, d,
				d.createType("OnChanged"));
		EventDispatcher pd = es.createEventDispatcher("Portfolio[LX-001]");
		expected.setPositionsInstance(new PositionsImpl(expected, pd,
				pd.createType("OnPosAvailable"),
				pd.createType("OnPosChanged")));
		expected.OnChanged().addListener(portfolios);
		expected.OnPositionAvailable().addListener(portfolios);
		expected.OnPositionChanged().addListener(portfolios);
		control.replay();
		
		Portfolio actual = portfolios.createPortfolio(terminal, account1);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
		assertSame(actual, portfolios.getEditablePortfolio(account1));
		assertSame(actual, portfolios.getPortfolio(account1));
		assertSame(actual, portfolios.getDefaultPortfolio());
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
		Variant<String> vDispId = new Variant<String>()
			.add("Portfolios")
			.add("AnotherId");
		Variant<String> vAvlId = new Variant<String>(vDispId)
			.add("OnAvailable")
			.add("OnUnknown");
		Variant<String> vChngId = new Variant<String>(vAvlId)
			.add("OnChanged")
			.add("OnSomething");
		Variant<String> vPosAvlId = new Variant<String>(vChngId)
			.add("OnPosAvailable")
			.add("OnPosUnknown");
		Variant<String> vPosChngId = new Variant<String>(vPosAvlId)
			.add("OnPosChanged")
			.add("OnPosSomething");
		Variant<List<EditablePortfolio>> vList =
				new Variant<List<EditablePortfolio>>(vPosChngId)
			.add(list1)
			.add(list2);
		Variant<EditablePortfolio> vDef = new Variant<EditablePortfolio>(vList)
			.add(p1)
			.add(null);
		Variant<?> iterator = vDef;
		int foundCnt = 0;
		PortfoliosImpl x = null, found = null;
		control.replay();
		for ( EditablePortfolio p : list1 ) {
			portfolios.setPortfolio(p.getAccount(), p);
		}
		portfolios.setDefaultPortfolio(p1);
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new PortfoliosImpl(d, d.createType(vAvlId.get()),
					d.createType(vChngId.get()),
					d.createType(vPosAvlId.get()),
					d.createType(vPosChngId.get()));
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
	}

}
