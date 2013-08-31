package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfolioFactory;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfoliosEventDispatcher;
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
	private PortfoliosEventDispatcher dispatcher;
	private EventType type;
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
		type = control.createMock(EventType.class);
		terminal = control.createMock(EditableTerminal.class);
		dispatcher = control.createMock(PortfoliosEventDispatcher.class);
		p1 = control.createMock(EditablePortfolio.class);
		p2 = control.createMock(EditablePortfolio.class);
		p3 = control.createMock(EditablePortfolio.class);
		factory = control.createMock(PortfolioFactory.class);
		
		portfolios = new PortfoliosImpl(dispatcher, factory);
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
		expect(dispatcher.OnPortfolioAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, portfolios.OnPortfolioAvailable());
		
		control.verify();
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
		dispatcher.fireAvailable(p1);
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
		expect(factory.createInstance(terminal, account1)).andReturn(p1);
		dispatcher.startRelayFor(same(p1));
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
		expect(factory.createInstance(terminal, account1)).andReturn(p1);
		dispatcher.startRelayFor(same(p1));
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
		expect(dispatcher.OnPortfolioChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, portfolios.OnPortfolioChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnPositionAvailable() throws Exception {
		expect(dispatcher.OnPositionAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, portfolios.OnPositionAvailable());
		
		control.verify();
	}

	@Test
	public void testOnPositionChanged() throws Exception {
		expect(dispatcher.OnPositionChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, portfolios.OnPositionChanged());
		
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
		Variant<List<EditablePortfolio>> vList =
			new Variant<List<EditablePortfolio>>()
			.add(list1)
			.add(list2);
		Variant<EditablePortfolio> vDflt = new Variant<EditablePortfolio>(vList)
			.add(p1)
			.add(null);
		Variant<?> iterator = vDflt;
		int foundCnt = 0;
		PortfoliosImpl x = null, found = null;
		control.replay();
		for ( EditablePortfolio p : list1 ) {
			portfolios.setPortfolio(p.getAccount(), p);
		}
		portfolios.setDefaultPortfolio(p1);
		do {
			x = new PortfoliosImpl(dispatcher);
			for ( EditablePortfolio p : vList.get() ) {
				x.setPortfolio(p.getAccount(), p);
			}
			if ( vDflt.get() != null ) {
				x.setDefaultPortfolio(vDflt.get());
			}
			if ( portfolios.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(list1, found.getPortfolios());
		assertSame(p1, found.getDefaultPortfolio());
	}
	
	@Test
	public void testConstruct_DefaultFactory() throws Exception {
		PortfoliosImpl expected =
			new PortfoliosImpl(dispatcher, new PortfolioFactory());
		assertEquals(expected, new PortfoliosImpl(dispatcher));
	}

}
