package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.PositionImpl.PositionController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PositionParamsBuilder;
import ru.prolib.aquila.core.data.DataProviderStub;

/**
 * 2012-08-03<br>
 * $Id: PositionImplTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImplTest extends ObservableStateContainerImplTest {
	private static Symbol symbol = new Symbol("S:GAZP@EQBR:RUB");
	private static Account account = new Account("TST01");
	private EditableTerminal terminal;
	private PositionImpl position;

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
		return position.getContainerID();
	}
	
	private void prepareTerminal() {
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.withTerminalID("foobar")
				.withEventQueue(queue)
				.buildTerminal();
		terminal.getEditableSecurity(symbol);
		terminal.getEditablePortfolio(account);	
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer() {
		prepareTerminal();
		position = new PositionImpl(new PositionParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.buildParams());
		return position;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(OSCController controller) {
		prepareTerminal();
		position = new PositionImpl(new PositionParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withController(controller)
				.buildParams());
		return position;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(EventDispatcher eventDispatcher,
			OSCController controller)
	{
		prepareTerminal();
		position = new PositionImpl(new PositionParamsBuilder()
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withEventDispatcher(eventDispatcher)
				.withController(controller)
				.buildParams());
		return position;
	}
	
	@Test
	public void testCtor_DefaultController() {
		produceContainer();
		assertEquals(PositionController.class, position.getController().getClass());
		assertNotNull(position.getTerminal());
		assertNotNull(position.getEventDispatcher());
		assertSame(terminal, position.getTerminal());
		assertSame(queue, ((EventDispatcherImpl)position.getEventDispatcher()).getEventQueue());
		assertEquals(account, position.getAccount());
		assertEquals(symbol, position.getSymbol());
		String prefix = String.format("%s.TST01[S:GAZP@EQBR:RUB].", terminal.getTerminalID());
		assertEquals(prefix + "POSITION", position.getContainerID());
		assertEquals(prefix + "POSITION.AVAILABLE", position.onAvailable().getId());
		assertEquals(prefix + "POSITION.POSITION_CHANGE", position.onPositionChange().getId());
		assertEquals(prefix + "POSITION.CURRENT_PRICE_CHANGE", position.onCurrentPriceChange().getId());
	}
	
	@Test
	public void testGetUsedMargin() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return position.getUsedMargin();
			}			
		};
		testGetter(PositionField.USED_MARGIN,
				new FMoney("415.345", "RUB"), new FMoney("280.34", "USD"));
	}
	
	@Test
	public void testGetCurrentVolume() throws Exception {
		getter = new Getter<Long>() {
			@Override public Long get() {
				return position.getCurrentVolume();
			}
		};
		testGetter(PositionField.CURRENT_VOLUME, 4000L, 8270L);
	}
	
	@Test
	public void testGetCurrentPrice() throws Exception {
		getter = new Getter<FDecimal>() {
			@Override public FDecimal get() {
				return position.getCurrentPrice();
			}
		};
		testGetter(PositionField.CURRENT_PRICE,
				new FDecimal("2014"), new FDecimal("882.15"));
	}
	
	@Test
	public void testGetOpenPrice() throws Exception {
		getter = new Getter<FDecimal>() {
			@Override public FDecimal get() {
				return position.getOpenPrice();
			}
		};
		testGetter(PositionField.OPEN_PRICE,
				new FDecimal("551.13"), new FDecimal("902.08"));
	}
	
	@Test
	public void testGetProfitAndLoss() throws Exception {
		getter = new Getter<FMoney>() {
			@Override public FMoney get() {
				return position.getProfitAndLoss();
			}
		};
		testGetter(PositionField.PROFIT_AND_LOSS,
				new FMoney("421.19", "CAD"), new FMoney("534.25", "EUR"));
	}

	@Test
	public void testPositionController_HasMinimalData() {
		PositionController controller = new PositionController();
		
		assertFalse(controller.hasMinimalData(position));
		
		Map<Integer, Object> minimal = new HashMap<Integer, Object>();
		minimal.put(PositionField.CURRENT_VOLUME, 1000L);
		minimal.put(PositionField.CURRENT_PRICE, new FDecimal("2000", 2));
		minimal.put(PositionField.OPEN_PRICE, new FDecimal("1800", 2));
		minimal.put(PositionField.USED_MARGIN, new FMoney("200", 2, "USD"));
		minimal.put(PositionField.PROFIT_AND_LOSS, new FMoney("0", 2, "USD"));
		for ( Map.Entry<Integer, Object> entry : minimal.entrySet() ) {
			data.put(entry.getKey(), entry.getValue());
			position.update(data);
			assertEquals(data.size() == minimal.size(), controller.hasMinimalData(position));
		}
		
		assertTrue(controller.hasMinimalData(position));
	}

	@Test
	public void testPositionController_ProcessUpdate_PositionChange() {
		data.put(PositionField.CURRENT_VOLUME, 200L);
		position.update(data);
		PositionController controller = new PositionController();
		EventListenerStub listener = new EventListenerStub();
		position.onPositionChange().addSyncListener(listener);
		position.onCurrentPriceChange().addSyncListener(listener);
		
		controller.processUpdate(position);
		
		assertEquals(1, listener.getEventCount());
		PositionEvent e = (PositionEvent) listener.getEvent(0);
		assertTrue(e.isType(position.onPositionChange()));
		assertSame(position, e.getPosition());
	}
	
	@Test
	public void testPositionController_ProcessUpdate_CurrentPriceChange() {
		data.put(PositionField.CURRENT_PRICE, new FDecimal("4518.96"));
		position.update(data);
		PositionController controller = new PositionController();
		EventListenerStub listener = new EventListenerStub();
		position.onPositionChange().addSyncListener(listener);
		position.onCurrentPriceChange().addSyncListener(listener);
		
		controller.processUpdate(position);
		
		assertEquals(1, listener.getEventCount());
		PositionEvent e = (PositionEvent) listener.getEvent(0);
		assertTrue(e.isType(position.onCurrentPriceChange()));
		assertSame(position, e.getPosition());
	}

	@Test
	public void testPositionController_ProcessAvailable() {
		data.put(PositionField.CURRENT_VOLUME, 200L);
		data.put(PositionField.CURRENT_PRICE, new FDecimal("4582.13"));
		position.update(data);
		PositionController controller = new PositionController();
		EventListenerStub listener = new EventListenerStub();
		position.onPositionChange().addSyncListener(listener);
		position.onCurrentPriceChange().addSyncListener(listener);
		
		controller.processAvailable(position);
		
		// Shouldn't fire anything
		assertEquals(0, listener.getEventCount());
	}
	
	@Test
	public void testClose() {
		EventType type = new EventTypeImpl();
		position.onAvailable().addListener(new EventListenerStub());
		position.onAvailable().addAlternateType(type);
		position.onCurrentPriceChange().addListener(new EventListenerStub());
		position.onCurrentPriceChange().addAlternateType(type);
		position.onPositionChange().addListener(new EventListenerStub());
		position.onPositionChange().addAlternateType(type);
		position.onUpdate().addListener(new EventListenerStub());
		position.onUpdate().addAlternateType(type);
		
		position.close();
		
		assertNull(position.getTerminal());
		assertFalse(position.onAvailable().hasListeners());
		assertFalse(position.onAvailable().hasAlternates());
		assertFalse(position.onCurrentPriceChange().hasListeners());
		assertFalse(position.onCurrentPriceChange().hasAlternates());
		assertFalse(position.onPositionChange().hasListeners());
		assertFalse(position.onPositionChange().hasAlternates());
		assertFalse(position.onUpdate().hasListeners());
		assertFalse(position.onUpdate().hasAlternates());
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
		position.update(data);
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		PositionEvent event = (PositionEvent) listenerStub.getEvent(0);
		assertTrue(event.isType(position.onAvailable()));
		assertSame(position, event.getPosition());
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
		position.update(data);
		data.put(12345, 450);
		position.update(data);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(position.onUpdate()));
		assertSame(position, ((PositionEvent) listenerStub.getEvent(0)).getPosition());
		assertTrue(listenerStub.getEvent(1).isType(position.onUpdate()));
		assertSame(position, ((PositionEvent) listenerStub.getEvent(1)).getPosition());

	}
		
	@Test
	public void testGettersOfRelatedObjects() throws Exception {
		Terminal terminalMock = control.createMock(Terminal.class);
		control.replay();
		Security expectedSecurity = terminal.getSecurity(symbol);
		Portfolio expectedPortfolio = terminal.getPortfolio(account);
		
		position = new PositionImpl(new PositionParamsBuilder(queue)
				.withTerminal(terminalMock)
				.withAccount(account)
				.withSymbol(symbol)
				.withSecurity(expectedSecurity)
				.withPortfolio(expectedPortfolio)
				.withID("zxy")
				.buildParams());
		assertSame(expectedSecurity, position.getSecurity());
		assertSame(expectedPortfolio, position.getPortfolio());

		control.verify();
	}

}
