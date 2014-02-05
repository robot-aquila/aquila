package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Positions;

public class PortfoliosEventDispatcherTest {
	private static Account account;
	private IMocksControl control;
	private Terminal terminal;
	private PortfolioImpl portfolio;
	private Position position;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private PortfoliosEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("foo", "bar");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		es = new EventSystemImpl(queue);
		terminal = control.createMock(Terminal.class);
		portfolio = new PortfolioImpl(terminal, account,
				new PortfolioEventDispatcher(es, account));
		portfolio.setPositionsInstance(new Positions(portfolio,
				new PositionsEventDispatcher(es, account)));
		position = control.createMock(Position.class);
		listener = control.createMock(EventListener.class);
		dispatcher = new PortfoliosEventDispatcher(es);
		
		expect(position.getPortfolio()).andStubReturn(portfolio);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = es.createEventDispatcher("Portfolios");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnPortfolioChanged(), ed.createType("Changed"));
		assertEquals(dispatcher.OnPortfolioAvailable(), ed.createType("Available"));
		assertEquals(dispatcher.OnPositionAvailable(), ed.createType("PositionAvailable"));
		assertEquals(dispatcher.OnPositionChanged(), ed.createType("PositionChanged"));
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		dispatcher.OnPortfolioAvailable().addListener(listener);
		queue.enqueue(eq(new PortfolioEvent(dispatcher.OnPortfolioAvailable(),
				portfolio)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireAvailable(portfolio);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_PositionChanged() throws Exception {
		dispatcher.OnPositionChanged().addListener(listener);
		queue.enqueue(eq(new PositionEvent(dispatcher.OnPositionChanged(),
				position)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new PositionEvent(portfolio.OnPositionChanged(),
				position));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_PositionAvailable() throws Exception {
		dispatcher.OnPositionAvailable().addListener(listener);
		queue.enqueue(eq(new PositionEvent(dispatcher.OnPositionAvailable(),
				position)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new PositionEvent(portfolio.OnPositionAvailable(),
				position));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_PortfolioChanged() throws Exception {
		dispatcher.OnPortfolioChanged().addListener(listener);
		queue.enqueue(eq(new PortfolioEvent(dispatcher.OnPortfolioChanged(),
				portfolio)), eq(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new PortfolioEvent(portfolio.OnChanged(), portfolio));
		
		control.verify();
	}
	
	@Test
	public void testStartRelayFor() throws Exception {
		control.replay();
		
		dispatcher.startRelayFor(portfolio);
		
		control.verify();
		assertTrue(portfolio.OnChanged().isListener(dispatcher));
		assertTrue(portfolio.OnPositionAvailable().isListener(dispatcher));
		assertTrue(portfolio.OnPositionChanged().isListener(dispatcher));
	}


}
