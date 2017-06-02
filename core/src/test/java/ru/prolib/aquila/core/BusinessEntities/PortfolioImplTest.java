package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.PortfolioImpl.PortfolioController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParamsBuilder;

/**
 * 2012-09-06
 */
public class PortfolioImplTest extends ObservableStateContainerImplTest {
	private static Account account = new Account("ZUMBA");
	private IMocksControl control;
	private EditableTerminal terminal;
	private PortfolioImpl portfolio;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ObservableStateContainerImplTest.setUpBeforeClass();
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
	protected ObservableStateContainerImpl produceContainer() {
		prepareTerminal();
		portfolio = new PortfolioImpl(terminal, account);
		return portfolio;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(OSCController controller) {
		prepareTerminal();
		portfolio = new PortfolioImpl(terminal, account, controller);
		return portfolio;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(EventDispatcher eventDispatcher,
			OSCController controller)
	{
		prepareTerminal();
		portfolio = new PortfolioImpl(new PortfolioParamsBuilder()
				.withID(getID())
				.withTerminal(terminal)
				.withAccount(account)
				.withEventDispatcher(eventDispatcher)
				.withController(controller)
				.buildParams());
		return portfolio;
	}
	
	@Test
	public void testCtor_DefaultController() throws Exception {
		portfolio = new PortfolioImpl(terminal, account);
		assertEquals(PortfolioController.class, portfolio.getController().getClass());
		assertNotNull(portfolio.getTerminal());
		assertNotNull(portfolio.getEventDispatcher());
		assertSame(terminal, portfolio.getTerminal());
		assertSame(queue, ((EventDispatcherImpl)portfolio.getEventDispatcher()).getEventQueue());
		assertEquals(account, portfolio.getAccount());
		String prefix = String.format("%s.ZUMBA.PORTFOLIO", terminal.getTerminalID());
		assertEquals(prefix, portfolio.getContainerID());
		assertEquals(prefix + ".AVAILABLE", portfolio.onAvailable().getId());
		assertEquals(prefix + ".UPDATE", portfolio.onUpdate().getId());
		assertEquals(prefix + ".POSITION_AVAILABLE", portfolio.onPositionAvailable().getId());
		assertEquals(prefix + ".POSITION_CHANGE", portfolio.onPositionChange().getId());
		assertEquals(prefix + ".POSITION_PRICE_CHANGE", portfolio.onPositionCurrentPriceChange().getId());
		assertEquals(prefix + ".POSITION_UPDATE", portfolio.onPositionUpdate().getId());
		assertEquals(prefix + ".POSITION_CLOSE", portfolio.onPositionClose().getId());
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
		portfolio.onClose().addSyncListener(listenerStub);
		portfolio.onClose().addAlternateType(type);
		
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
		assertTrue(portfolio.onClose().isAlternateType(type));
		assertTrue(portfolio.onClose().isListener(listenerStub));
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(portfolio.onClose()));
		assertSame(portfolio, ((PortfolioEvent) listenerStub.getEvent(0)).getPortfolio());
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
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getBalance();
			}
		};
		testGetter(PortfolioField.BALANCE,
				FMoney.ofUSD2(40560.28), FMoney.ofUSD2(80340.95));
	}

	@Test
	public void testGetEquity() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getEquity();
			}
		};
		testGetter(PortfolioField.EQUITY,
				FMoney.ofUSD2(812.76), FMoney.ofUSD2(324.10));
	}
	
	@Test
	public void testGetProfitAndLoss() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getProfitAndLoss();
			}
		};
		testGetter(PortfolioField.PROFIT_AND_LOSS,
				FMoney.ofEUR3(100000.0), FMoney.ofEUR3(80000.0));
	}

	@Test
	public void testGetUsedMargin() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getUsedMargin();
			}
		};
		testGetter(PortfolioField.USED_MARGIN,
				FMoney.ofRUB2(96283.15), FMoney.ofRUB2(94518.22));
	}
	
	@Test
	public void testGetFreeMargin() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getFreeMargin();
			}
		};
		testGetter(PortfolioField.FREE_MARGIN,
				FMoney.ofUSD2(4519.72), FMoney.ofUSD2(5425.12));
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
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getAssets();
			}
		};
		testGetter(PortfolioField.ASSETS,
				FMoney.ofEUR4(12.34), FMoney.ofEUR4(56.78));
	}

	@Test
	public void testGetLiabilities() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return portfolio.getLiabilities();
			}
		};
		testGetter(PortfolioField.LIABILITIES,
				FMoney.ofUSD2(632.88), FMoney.ofUSD2(640.19));
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
	public void testGetLeverage() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return portfolio.getLeverage();
			}
		};
		testGetter(PortfolioField.LEVERAGE, 4.0d, 2.0d);
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
		assertTrue(position.onClose().isAlternateType(portfolio.onPositionClose()));
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
		assertTrue(position.onClose().isAlternateType(portfolio.onPositionClose()));
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
		
		portfolio.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.CURRENCY, "USD")
			.withToken(PortfolioField.BALANCE, FMoney.ofUSD2(415.08))
			.withToken(PortfolioField.EQUITY, FMoney.ofUSD2(213.34))
			.withToken(PortfolioField.PROFIT_AND_LOSS, FMoney.ofUSD2(1.18))
			.withToken(PortfolioField.USED_MARGIN, FMoney.ofUSD2(50.72))
			.withToken(PortfolioField.FREE_MARGIN, FMoney.ofUSD2(0.52))
			.buildUpdate());
		
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

	@Test
	public void testUpdate_OnAvailable() throws Exception {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		controllerMock.processUpdate(container);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		getMocksControl().replay();

		data.put(12345, 415); // any value
		portfolio.update(data);
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		PortfolioEvent event = (PortfolioEvent) listenerStub.getEvent(0);
		assertTrue(event.isType(portfolio.onAvailable()));
		assertSame(portfolio, event.getPortfolio());
	}
	
	@Test
	public void testUpdate_OnUpdateEvent() throws Exception {
		container = produceContainer(controllerMock);
		container.onUpdate().addSyncListener(listenerStub);
		controllerMock.processUpdate(container);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		controllerMock.processUpdate(container);
		getMocksControl().replay();
		
		data.put(12345, 415);
		portfolio.update(data);
		data.put(12345, 450);
		portfolio.update(data);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(portfolio.onUpdate()));
		assertSame(portfolio, ((PortfolioEvent) listenerStub.getEvent(0)).getPortfolio());
		assertTrue(listenerStub.getEvent(1).isType(portfolio.onUpdate()));
		assertSame(portfolio, ((PortfolioEvent) listenerStub.getEvent(1)).getPortfolio());
	}
	
	@Test
	public void testIsPositionExists() throws Exception {
		Symbol symbol1 = new Symbol("SBER"),
				symbol2 = new Symbol("GAZP");
		
		assertFalse(portfolio.isPositionExists(symbol1));
		assertFalse(portfolio.isPositionExists(symbol2));
		
		portfolio.getPosition(symbol1);
		
		assertTrue(portfolio.isPositionExists(symbol1));
		assertFalse(portfolio.isPositionExists(symbol2));
		
		portfolio.getPosition(symbol2);
		
		assertTrue(portfolio.isPositionExists(symbol1));
		assertTrue(portfolio.isPositionExists(symbol2));
	}

}
