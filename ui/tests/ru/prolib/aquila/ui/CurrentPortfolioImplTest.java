package ru.prolib.aquila.ui;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;
import ru.prolib.aquila.core.BusinessEntities.Portfolios;

/**
 * $Id$
 */
public class CurrentPortfolioImplTest {

	private static IMocksControl control;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	
	private Portfolios portfolios;
	private EventDispatcher dispatcher;
	private EventType portfolioChanged;
	
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
		portfolios = control.createMock(Portfolios.class);
		portfolioChanged = eventSystem.createGenericType(dispatcher);
		
		prt = new CurrentPortfolioImpl(portfolios, portfolioChanged, dispatcher);
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
		onAvailable.addListener(prt);
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
		EventType onPortfolioAvailable = eventSystem.createGenericType(dispatcher);
		final Portfolio portfolio = control.createMock(Portfolio.class);
		final CountDownLatch finished = new CountDownLatch(1);
		
		expect(portfolios.OnPortfolioAvailable()).andReturn(onPortfolioAvailable);
		
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
	}
	
	@Test
	public void testOnEvent_HandledCurrPortfolioSet() {
		EventType onPortfolioAvailable = eventSystem.createGenericType(dispatcher);
		final Portfolio portfolio = control.createMock(Portfolio.class);
		expect(portfolios.OnPortfolioAvailable()).andReturn(onPortfolioAvailable);
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
	}
	
	@Test
	public void testOnEvent_Unhandled() {
		EventType onPortfolioAvailable = eventSystem.createGenericType(dispatcher);
		expect(portfolios.OnPortfolioAvailable()).andReturn(onPortfolioAvailable);
		
		prt.OnCurrentPortfolioChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				fail("Unexpected method setCurrentPortfolio call!");
			}
			
		});
		control.replay();
		
		prt.onEvent(new EventImpl(eventSystem.createGenericType(dispatcher)));
		
		control.verify();
	}
	
	@Test
	public void testSetCurrentPortfolio() throws Exception {
		final Portfolio portfolio = control.createMock(Portfolio.class);		
		final CountDownLatch finished = new CountDownLatch(1);
		final PortfolioEvent expected = new PortfolioEvent(portfolioChanged, portfolio);
		
		prt.OnCurrentPortfolioChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertEquals(expected, e);
				finished.countDown();
			}			
		});
		prt.setCurrentPortfolio(portfolio);
		assertEquals(portfolio, prt.getCurrentPortfolio());
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testConstructor() {
		assertEquals(portfolios, prt.getPortfolios());
		assertEquals(dispatcher, prt.getDispatcher());
		assertEquals(portfolioChanged, prt.OnCurrentPortfolioChanged());
	}

}
