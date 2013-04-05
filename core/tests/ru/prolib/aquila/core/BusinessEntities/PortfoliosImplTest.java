package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BMFactory;
import ru.prolib.aquila.core.BusinessEntities.utils.BMFactoryImpl;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-16<br>
 * $Id: PortfoliosImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class PortfoliosImplTest {
	private static SecurityDescriptor descr;
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static Account account = new Account("LX-001");
	private static EventSystem eventSystem;
	private static EventQueue queue;
	private static BMFactory bfact;
	private EventDispatcher dispatcher;
	private EventType onAvailable,onChanged,onPosAvailable,onPosChanged;
	private PortfoliosImpl portfolios;
	private EditablePortfolio p1,p2,p3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		bfact = new BMFactoryImpl(eventSystem, terminal);
		descr = new SecurityDescriptor("AAPL","SMART", "USD", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		dispatcher = eventSystem.createEventDispatcher();
		onAvailable = eventSystem.createGenericType(dispatcher);
		onChanged = eventSystem.createGenericType(dispatcher);
		onPosAvailable = eventSystem.createGenericType(dispatcher);
		onPosChanged = eventSystem.createGenericType(dispatcher);
		portfolios = new PortfoliosImpl(dispatcher, onAvailable, onChanged,
				onPosAvailable, onPosChanged);
		p1 = createPort(account);
		p2 = createPort(new Account("BB02"));
		p3 = createPort(new Account("CC03"));
		expect(terminal.getPortfolio(account)).andStubReturn(p1);
		expect(terminal.getPortfolio(new Account("BB02"))).andStubReturn(p2);
		expect(terminal.getPortfolio(new Account("CC03"))).andStubReturn(p3);
		queue.start();
	}

	/**
	 * Создать тестовый портфель.
	 * <p>
	 * @param account счет портфеля
	 * @return портфель
	 */
	private EditablePortfolio createPort(Account account) {
		return bfact.createPortfolioFactory().createPortfolio(account);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(null)
			.add(dispatcher);
		Variant<EventType> vAvail = new Variant<EventType>(vDisp)
			.add(null)
			.add(onAvailable);
		Variant<EventType> vChang = new Variant<EventType>(vAvail)
			.add(onChanged)
			.add(null);
		Variant<EventType> vPosAvail = new Variant<EventType>(vChang)
			.add(onPosAvailable)
			.add(null);
		Variant<EventType> vPosChang = new Variant<EventType>(vPosAvail)
			.add(null)
			.add(onPosChanged);
		Variant<?> iterator = vPosChang;
		int exceptionCnt = 0;
		PortfoliosImpl found = null, x = null;
		do {
			try {
				x = new PortfoliosImpl(vDisp.get(), vAvail.get(), vChang.get(),
						vPosAvail.get(), vPosChang.get());
				found = x;
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onAvailable, found.OnPortfolioAvailable());
		assertSame(onChanged, found.OnPortfolioChanged());
		assertSame(onPosAvailable, found.OnPositionAvailable());
		assertSame(onPosChanged, found.OnPositionChanged());
	}
	
	@Test
	public void testFirePortfolioAvailableEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final PortfolioEvent expected = new PortfolioEvent(onAvailable, p1);
		portfolios.OnPortfolioAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		portfolios.firePortfolioAvailableEvent(p1);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test (expected=PortfolioNotExistsException.class)
	public void testGetDefaultPortfolio_ThrowsIfNotExists() throws Exception {
		control.replay();
		portfolios.getDefaultPortfolio();
	}
	
	@Test
	public void testGetDefaultPortfolio_Ok() throws Exception {
		portfolios.setDefaultPortfolio(p1);
		assertSame(p1, portfolios.getDefaultPortfolio());
	}
	
	@Test
	public void testGetEditablePortfolio_Ok() throws Exception {
		portfolios.registerPortfolio(p1);
		assertSame(p1,portfolios.getEditablePortfolio(account));
	}

	@Test (expected=PortfolioNotExistsException.class)
	public void testGetEditablePortfolio_ThrowsIfNotExists() throws Exception {
		portfolios.getEditablePortfolio(account);
	}

	@Test
	public void testGetPortfolio_Ok() throws Exception {
		portfolios.registerPortfolio(p1);
		
		assertSame(p1, portfolios.getPortfolio(account));
	}

	@Test (expected=PortfolioNotExistsException.class)
	public void testGetPortfolio_ThrowsIfNotExists() throws Exception {
		portfolios.getPortfolio(account);
	}
	
	@Test
	public void testGetPortfolios() throws Exception {
		portfolios.registerPortfolio(p1);
		portfolios.registerPortfolio(p2);
		portfolios.registerPortfolio(p3);
		
		List<Portfolio> list = portfolios.getPortfolios();
		assertNotNull(list);
		assertEquals(3, list.size());
		assertSame(p1, list.get(0));
		assertSame(p2, list.get(1));
		assertSame(p3, list.get(2));
	}
	
	@Test
	public void testIsPortfoioAvailable() throws Exception {
		portfolios.registerPortfolio(p1);
		
		assertTrue(portfolios.isPortfolioAvailable(new Account("LX-001")));
		assertFalse(portfolios.isPortfolioAvailable(new Account("LX-002")));
	}
	
	@Test
	public void testRegisterPortfolio_Ok() throws Exception {
		portfolios.registerPortfolio(p1);
		
		assertSame(p1, portfolios.getPortfolio(new Account("LX-001")));
		assertTrue(p1.OnChanged().isListener(portfolios));
		assertTrue(p1.OnPositionAvailable().isListener(portfolios));
		assertTrue(p1.OnPositionChanged().isListener(portfolios));
	}
	
	@Test
	public void testRegisterPortfolio_ThrowsIfAlreadyExists() throws Exception {
		portfolios.registerPortfolio(p1);
		EditablePortfolio px = createPort(account);
		try {
			portfolios.registerPortfolio(px);
			fail("Expected exception: "
					+ PortfolioException.class.getSimpleName());
		} catch ( Exception e ) { }
		// Не подписывается, если не добавлен
		assertFalse(px.OnChanged().isListener(portfolios));
		assertFalse(px.OnPositionAvailable().isListener(portfolios));
		assertFalse(px.OnPositionChanged().isListener(portfolios));
	}
	
	@Test
	public void testRegisterPortfolio_SetFirstPortfolioAsDefault()
			throws Exception
	{
		portfolios.registerPortfolio(p3);
		portfolios.registerPortfolio(p2);
		portfolios.registerPortfolio(p1);
		
		assertNotNull(portfolios.getDefaultPortfolio());
		assertSame(p3, portfolios.getDefaultPortfolio());
	}
	
	@Test
	public void testGetPortfoliosCount() throws Exception {
		assertEquals(0, portfolios.getPortfoliosCount());
		portfolios.registerPortfolio(p1);
		assertEquals(1, portfolios.getPortfoliosCount());
		portfolios.registerPortfolio(p2);
		assertEquals(2, portfolios.getPortfoliosCount());
		portfolios.registerPortfolio(p3);
		assertEquals(3, portfolios.getPortfoliosCount());
	}
	
	@Test
	public void testOnEvent_OnPortfolioChanged() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final PortfolioEvent expected = new PortfolioEvent(onChanged, p1);
		portfolios.OnPortfolioChanged().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		portfolios.onEvent(new PortfolioEvent(p1.OnChanged(), p1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testOnEvent_OnPositionAvailable() throws Exception {
		Position pos = p1.getPosition(descr);
		final CountDownLatch finished = new CountDownLatch(1);
		final PositionEvent expected = new PositionEvent(onPosAvailable, pos);
		portfolios.OnPositionAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		control.replay();
		portfolios.onEvent(new PositionEvent(p1.OnPositionAvailable(), pos));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		control.verify();
	}

	@Test
	public void testOnEvent_OnPositionChanged() throws Exception {
		Position pos = p1.getPosition(descr);
		final CountDownLatch finished = new CountDownLatch(1);
		final PositionEvent expected = new PositionEvent(onPosChanged, pos);
		portfolios.OnPositionChanged().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		control.replay();
		portfolios.onEvent(new PositionEvent(p1.OnPositionChanged(), pos));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		control.verify();
	}

}
