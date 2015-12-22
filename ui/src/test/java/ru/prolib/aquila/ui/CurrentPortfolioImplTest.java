package ru.prolib.aquila.ui;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ui.wrapper.Menu;
import ru.prolib.aquila.ui.wrapper.MenuItem;

/**
 * $Id$
 */
public class CurrentPortfolioImplTest {

	private static IMocksControl control;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	
	private Account acc;
	private Terminal portfolios;
	private EventDispatcher dispatcher;
	private EventType portfolioChanged;
	private Menu menu;
	
	private CurrentPortfolioImpl prt;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();		
		
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		dispatcher = eventSystem.createEventDispatcher();
		portfolios = control.createMock(Terminal.class);
		portfolioChanged = dispatcher.createType();
		acc = new Account("Foo", "Bar", "Trulala");
		
		menu = new Menu(new JMenu(), eventSystem);
		prt = new CurrentPortfolioImpl(portfolios, portfolioChanged, dispatcher, menu);
		queue.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testStart() throws Exception {
		EventType onAvailable = control.createMock(EventType.class);
		expect(portfolios.OnPortfolioAvailable()).andStubReturn(onAvailable);
		onAvailable.addListener((EventListener) prt);
		control.replay();
		prt.start();
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		EventType onAvailable = control.createMock(EventType.class);
		expect(portfolios.OnPortfolioAvailable()).andStubReturn(onAvailable);
		onAvailable.removeListener(prt);
		control.replay();
		prt.stop();
		control.verify();
	}
	
	@Test
	public void testOnEvent_HandledCurrPortfolioNotSet() throws Exception {
		EventType onPortfolioAvailable = dispatcher.createType();
		final Portfolio portfolio = control.createMock(Portfolio.class);
		
		final CountDownLatch finished = new CountDownLatch(1);
		
		expect(portfolios.OnPortfolioAvailable()).andReturn(onPortfolioAvailable);
		expect(portfolio.getAccount()).andReturn(acc);
		expectLastCall().times(2);
		prt.OnCurrentPortfolioChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				PortfolioEvent e = (PortfolioEvent) event;
				assertEquals(portfolio, e.getPortfolio());
				finished.countDown();
			}
			
		});
		control.replay();
		
		prt.onEvent(new PortfolioEvent(onPortfolioAvailable, portfolio));
		
		control.verify();
		assertEquals(portfolio, prt.getCurrentPortfolio());
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		
		assertTrue(prt.getMenu().isItemExists(acc.toString()));
		MenuItem menuItem = prt.getMenu().getItem(acc.toString());
		IsInstanceOf.instanceOf(JRadioButtonMenuItem.class).matches(menuItem.getUnderlyingObject());
		
		Map<EventType, Portfolio> prtList = prt.getPrtList();
		assertTrue(prtList.containsKey(menuItem.OnCommand()));
		assertEquals(portfolio, prtList.get(menuItem.OnCommand()));
		assertTrue(menuItem.OnCommand().isListener(prt));
		assertEquals(1, prt.getButtons().getButtonCount());
	}
	
	@Test
	public void testOnEvent_HandledCurrPortfolioSet() throws Exception {
		EventType onPortfolioAvailable = dispatcher.createType();
		final Portfolio portfolio = control.createMock(Portfolio.class);
		
		expect(portfolios.OnPortfolioAvailable()).andReturn(onPortfolioAvailable);
		expect(portfolio.getAccount()).andReturn(acc);
		
		prt.setPortfolio(portfolio);
		prt.OnCurrentPortfolioChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				fail("Unexpected method setCurrentPortfolio call!");
			}
			
		});
		control.replay();
		
		prt.onEvent(new PortfolioEvent(onPortfolioAvailable, portfolio));
		
		control.verify();
		
		assertTrue(prt.getMenu().isItemExists(acc.toString()));
		MenuItem menuItem = prt.getMenu().getItem(acc.toString());
		IsInstanceOf.instanceOf(JRadioButtonMenuItem.class).matches(menuItem.getUnderlyingObject());
		
		Map<EventType, Portfolio> prtList = prt.getPrtList();
		assertTrue(prtList.containsKey(menuItem.OnCommand()));
		assertEquals(portfolio, prtList.get(menuItem.OnCommand()));
		assertTrue(menuItem.OnCommand().isListener(prt));
		assertEquals(1, prt.getButtons().getButtonCount());
	}
	
	@Test
	public void testOnEvent_Unhandled() {
		EventType onPortfolioAvailable = dispatcher.createType();
		expect(portfolios.OnPortfolioAvailable()).andReturn(onPortfolioAvailable);
		
		prt.OnCurrentPortfolioChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				fail("Unexpected method setCurrentPortfolio call!");
			}
			
		});
		control.replay();
		
		prt.onEvent(new EventImpl(dispatcher.createType()));
		
		control.verify();
	}
	
	@Test
	public void testSetCurrentPortfolio() throws Exception {
		final Portfolio portfolio = control.createMock(Portfolio.class);		
		final CountDownLatch finished = new CountDownLatch(1);
		final PortfolioEvent expected = new PortfolioEvent(portfolioChanged, portfolio);
		expect(portfolio.getAccount()).andReturn(acc);
		prt.OnCurrentPortfolioChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertEquals(expected, e);
				finished.countDown();
			}			
		});
		
		control.replay();
		prt.setCurrentPortfolio(portfolio);
		control.verify();
		assertEquals(portfolio, prt.getCurrentPortfolio());
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testConstructor() {
		assertEquals(portfolios, prt.getPortfolios());
		assertEquals(dispatcher, prt.getDispatcher());
		assertEquals(portfolioChanged, prt.OnCurrentPortfolioChanged());
		assertEquals(menu, prt.getMenu());
	}

}
