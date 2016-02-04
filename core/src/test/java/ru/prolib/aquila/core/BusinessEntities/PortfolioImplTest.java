package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.PortfolioImpl.PortfolioController;

/**
 * 2012-09-06
 */
public class PortfolioImplTest extends ContainerImplTest {
	private static Account account = new Account("ZUMBA");
	private IMocksControl control;
	private EditableTerminal terminal;
	private PortfolioImpl portfolio;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ContainerImplTest.setUpBeforeClass();
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	protected String getID() {
		return portfolio.getContainerID();
	}
	
	private void prepareTerminal() {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getTerminalID()).andStubReturn("Terminal#1");
		expect(terminal.getEventQueue()).andStubReturn(queue);
		control.replay();		
	}
	
	@Override
	protected ContainerImpl produceContainer() {
		prepareTerminal();
		portfolio = new PortfolioImpl(terminal, account);
		return portfolio;
	}
	
	@Override
	protected ContainerImpl produceContainer(ContainerImpl.Controller controller) {
		prepareTerminal();
		portfolio = new PortfolioImpl(terminal, account, controller);
		return portfolio;
	}
	
	@Test
	public void testCtor_DefaultController() throws Exception {
		portfolio = new PortfolioImpl(terminal, account);
		assertEquals(PortfolioController.class, portfolio.getController().getClass());
		assertNotNull(portfolio.getTerminal());
		assertNotNull(portfolio.getEventQueue());
		assertSame(terminal, portfolio.getTerminal());
		assertSame(queue, portfolio.getEventQueue());
		assertEquals(account, portfolio.getAccount());
		String prefix = String.format("%s.ZUMBA.PORTFOLIO", terminal.getTerminalID());
		assertEquals(prefix, portfolio.getContainerID());
		assertEquals(prefix + ".AVAILABLE", portfolio.onAvailable().getId());
		assertEquals(prefix + ".UPDATE", portfolio.onUpdate().getId());
		assertEquals(prefix + ".POSITION_AVAILABLE", portfolio.onPositionAvailable().getId());
		assertEquals(prefix + ".POSITION_CHANGE", portfolio.onPositionChange().getId());
		assertEquals(prefix + ".POSITION_PRICE_CHANGE", portfolio.onPositionCurrentPriceChange().getId());
		assertEquals(prefix + ".POSITION_UPDATE", portfolio.onPositionUpdate().getId());
	}
	
	@Test
	public void testClose() throws Exception {
		EventListenerStub listener = new EventListenerStub();
		EventType type = new EventTypeImpl();
		portfolio.onAvailable().addListener(listener);
		portfolio.onAvailable().addAlternateType(type);
		portfolio.onPositionAvailable().addListener(listener);
		portfolio.onPositionAvailable().addAlternateType(type);
		portfolio.onPositionChange().addListener(listener);
		portfolio.onPositionChange().addAlternateType(type);
		portfolio.onPositionCurrentPriceChange().addListener(listener);
		portfolio.onPositionCurrentPriceChange().addAlternateType(type);
		portfolio.onPositionUpdate().addListener(listener);
		portfolio.onPositionUpdate().addAlternateType(type);
		portfolio.onUpdate().addListener(listener);
		portfolio.onUpdate().addAlternateType(type);
		
		portfolio.close();
		
		assertNull(portfolio.getTerminal());
		assertFalse(portfolio.onAvailable().hasListeners());
		assertFalse(portfolio.onAvailable().hasAlternates());
		assertFalse(portfolio.onPositionAvailable().hasListeners());
		assertFalse(portfolio.onPositionAvailable().hasAlternates());
		assertFalse(portfolio.onPositionChange().hasListeners());
		assertFalse(portfolio.onPositionChange().hasAlternates());
		assertFalse(portfolio.onPositionCurrentPriceChange().hasListeners());
		assertFalse(portfolio.onPositionCurrentPriceChange().hasAlternates());
		assertFalse(portfolio.onPositionUpdate().hasListeners());
		assertFalse(portfolio.onPositionUpdate().hasAlternates());
		assertFalse(portfolio.onUpdate().hasListeners());
		assertFalse(portfolio.onUpdate().hasAlternates());
	}
	
	@Test
	public void testClose_ClosesAndRemovesAllPositions() throws Exception {
		Position p1 = portfolio.getPosition(new Symbol("MSFT"));
		Position p2 = portfolio.getPosition(new Symbol("AAPL"));
		
		portfolio.close();
		
		assertEquals(0, portfolio.getPositionCount());
		assertTrue(p1.isClosed());
		assertTrue(p2.isClosed());
	}
	
	@Test
	public void testGetBalance() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getBalance();
			}
		};
		testGetter(PortfolioField.BALANCE, 40560.28d, 80340.95d);
	}

	@Test
	public void testGetEquity() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getEquity();
			}
		};
		testGetter(PortfolioField.EQUITY, 812.76d, 324.10d);
	}
	
	@Test
	public void testGetProfitAndLoss() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getProfitAndLoss();
			}
		};
		testGetter(PortfolioField.PROFIT_AND_LOSS, 100000.00d, 80000.00d);
	}

	@Test
	public void testGetUsedMargin() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getUsedMargin();
			}
		};
		testGetter(PortfolioField.USED_MARGIN, 96283.15d, 94518.22d);
	}
	
	@Test
	public void testGetFreeMargin() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getFreeMargin();
			}
		};
		testGetter(PortfolioField.FREE_MARGIN, 4519.72d, 5425.12d);
	}

	@Test
	public void testGetMarginCallLevel() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getMarginCallLevel();
			}
		};
		testGetter(PortfolioField.MARGIN_CALL_AT, 0.30d, 0.25d);
	}
	
	@Test
	public void testGetMarginStopOutLevel() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getMarginStopOutLevel();
			}
		};
		testGetter(PortfolioField.MARGIN_STOP_OUT_AT, 0.50d, 0.75d);
	}

	@Test
	public void testGetAssets() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getAssets();
			}
		};
		testGetter(PortfolioField.ASSETS, 12.34d, 56.78d);
	}

	@Test
	public void testGetLiabilities() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getLiabilities();
			}
		};
		testGetter(PortfolioField.LIABILITIES, 632.88d, 640.19d);
	}

	@Test
	public void testGetCurrency() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return portfolio.getCurrency();
			}
		};
		testGetter(PortfolioField.CURRENCY, "RUB", "USD");
	}
	
	@Test
	public void testGetPositionCount() throws Exception {
		assertEquals(0, portfolio.getPositionCount());
		
		portfolio.getPosition(new Symbol("GAZP"));
		portfolio.getPosition(new Symbol("SBER"));
		
		assertEquals(2, portfolio.getPositionCount());
		
		portfolio.getPosition(new Symbol("AAPL"));
		
		assertEquals(3, portfolio.getPositionCount());
	}
	
	@Test
	public void testGetPositions() throws Exception {
		Set<Position> expected = new HashSet<Position>();
		expected.add(portfolio.getPosition(new Symbol("MSFT")));
		expected.add(portfolio.getPosition(new Symbol("AAPL")));
		expected.add(portfolio.getPosition(new Symbol("SPY")));
		
		assertEquals(expected, portfolio.getPositions());
	}

	@Test
	public void testGetPosition() throws Exception {
		Position position = portfolio.getPosition(new Symbol("MSFT"));
		
		assertNotNull(position);
		assertEquals(new Symbol("MSFT"), position.getSymbol());
		assertEquals(account, position.getAccount());
		assertSame(terminal, position.getTerminal());
		assertTrue(position.onAvailable().isAlternateType(portfolio.onPositionAvailable()));
		assertTrue(position.onCurrentPriceChange().isAlternateType(portfolio.onPositionCurrentPriceChange()));
		assertTrue(position.onPositionChange().isAlternateType(portfolio.onPositionChange()));
		assertTrue(position.onUpdate().isAlternateType(portfolio.onPositionUpdate()));		
		assertSame(position, portfolio.getPosition(new Symbol("MSFT")));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetPosition_ThrowsIfClosed() throws Exception {
		portfolio.close();
		
		portfolio.getPosition(new Symbol("SBER"));
	}

	@Test
	public void testGetEditablePosition() throws Exception {
		EditablePosition position = portfolio.getEditablePosition(new Symbol("MSFT"));
		
		assertNotNull(position);
		assertEquals(new Symbol("MSFT"), position.getSymbol());
		assertEquals(account, position.getAccount());
		assertSame(terminal, position.getTerminal());
		assertTrue(position.onAvailable().isAlternateType(portfolio.onPositionAvailable()));
		assertTrue(position.onCurrentPriceChange().isAlternateType(portfolio.onPositionCurrentPriceChange()));
		assertTrue(position.onPositionChange().isAlternateType(portfolio.onPositionChange()));
		assertTrue(position.onUpdate().isAlternateType(portfolio.onPositionUpdate()));		
		assertSame(position, portfolio.getEditablePosition(new Symbol("MSFT")));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEditablePosition_ThrowsIfClosed() throws Exception {
		portfolio.close();
		
		portfolio.getEditablePosition(new Symbol("AAPL"));
	}

	@Test
	public void testPortfolioController_HasMinimalData() {
		PortfolioController controller = new PortfolioController();
		
		assertFalse(controller.hasMinimalData(portfolio));
		
		data.put(PortfolioField.CURRENCY, "USD");
		data.put(PortfolioField.BALANCE, 415.08d);
		data.put(PortfolioField.EQUITY, 213.34d);
		data.put(PortfolioField.PROFIT_AND_LOSS, 1.18d);
		data.put(PortfolioField.USED_MARGIN, 50.72d);
		data.put(PortfolioField.FREE_MARGIN, 0.52d);
		portfolio.update(data);
		
		assertTrue(controller.hasMinimalData(portfolio));
	}
	
	@Test
	public void testPortfolioController_ProcessAvailable() {
		// No additional event types. Nothing to do.
	}
	
	@Test
	public void testPortfolioController_ProcessUpdate() {
		// No additional event types. Nothing to do.
	}

}
