package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
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
	private SchedulerStub schedulerStub;
	private EditableTerminal terminal;
	private PositionImpl position;
	private PositionController controller;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ObservableStateContainerImplTest.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
		controller = new PositionController();
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
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.withTerminalID("foobar")
				.withEventQueue(queue)
				.withScheduler(schedulerStub)
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
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return position.getUsedMargin();
			}			
		};
		testGetter(PositionField.USED_MARGIN,
				CDecimalBD.ofRUB2("415.34"), CDecimalBD.ofUSD2("280.34"));
	}
	
	@Test
	public void testGetCurrentVolume() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return position.getCurrentVolume();
			}
		};
		testGetter(PositionField.CURRENT_VOLUME,
				CDecimalBD.of(4000L), CDecimalBD.of(8270L));
	}
	
	@Test
	public void testGetCurrentPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return position.getCurrentPrice();
			}
		};
		testGetter(PositionField.CURRENT_PRICE,
				CDecimalBD.of("2014.00"), CDecimalBD.of("882.15"));
	}
	
	@Test
	public void testGetOpenPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return position.getOpenPrice();
			}
		};
		testGetter(PositionField.OPEN_PRICE,
				CDecimalBD.of("551.13"), CDecimalBD.of("902.08"));
	}
	
	@Test
	public void testGetProfitAndLoss() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return position.getProfitAndLoss();
			}
		};
		testGetter(PositionField.PROFIT_AND_LOSS,
				CDecimalBD.ofUSD2("421.19"), CDecimalBD.ofRUB2("534.25"));
	}

	@Test
	public void testPositionController_HasMinimalData() {
		Instant time = T("2017-08-04T17:49:00Z");
		
		assertFalse(controller.hasMinimalData(position, time));
		
		Map<Integer, Object> minimal = new HashMap<Integer, Object>();
		minimal.put(PositionField.CURRENT_VOLUME, CDecimalBD.of(1000L));
		minimal.put(PositionField.CURRENT_PRICE, CDecimalBD.of("2000.00"));
		minimal.put(PositionField.OPEN_PRICE, CDecimalBD.of("1800.00"));
		minimal.put(PositionField.USED_MARGIN, CDecimalBD.ofUSD2("200.00"));
		minimal.put(PositionField.PROFIT_AND_LOSS, CDecimalBD.ofUSD2("0.00"));
		for ( Map.Entry<Integer, Object> entry : minimal.entrySet() ) {
			data.put(entry.getKey(), entry.getValue());
			position.update(data);
			assertEquals(data.size() == minimal.size(), controller.hasMinimalData(position, time));
		}
		
		assertTrue(controller.hasMinimalData(position, time));
	}

	@Test
	public void testPositionController_ProcessUpdate_PositionChange() {
		Instant time = T("2017-08-04T17:50:00Z");
		position.update(PositionField.CURRENT_VOLUME, CDecimalBD.of(200L));
		PositionController controller = new PositionController();
		EventListenerStub listener = new EventListenerStub();
		position.onPositionChange().addListener(listener);
		position.onCurrentPriceChange().addListener(listener);
		
		controller.processUpdate(position, time);
		
		assertEquals(1, listener.getEventCount());
		assertContainerEvent(listener.getEvent(0), position.onPositionChange(),
				position, time, PositionField.CURRENT_VOLUME);
	}
	
	@Test
	public void testPositionController_ProcessUpdate_CurrentPriceChange() {
		Instant time = T("2017-08-04T17:54:00Z");
		position.update(PositionField.CURRENT_PRICE, CDecimalBD.of("4518.96"));
		PositionController controller = new PositionController();
		EventListenerStub listener = new EventListenerStub();
		position.onPositionChange().addListener(listener);
		position.onCurrentPriceChange().addListener(listener);
		
		controller.processUpdate(position, time);
		
		assertEquals(1, listener.getEventCount());
		assertContainerEvent(listener.getEvent(0), position.onCurrentPriceChange(),
				position, time, PositionField.CURRENT_PRICE);
	}

	@Test
	public void testPositionController_ProcessAvailable() {
		data.put(PositionField.CURRENT_VOLUME, CDecimalBD.of(200L));
		data.put(PositionField.CURRENT_PRICE, CDecimalBD.of("4582.13"));
		position.update(data);
		PositionController controller = new PositionController();
		EventListenerStub listener = new EventListenerStub();
		position.onPositionChange().addListener(listener);
		position.onCurrentPriceChange().addListener(listener);
		
		controller.processAvailable(position, T("2017-08-04T17:48:00Z"));
		
		// Shouldn't fire anything
		assertEquals(0, listener.getEventCount());
	}
	
	@Test
	public void testPositionController_GetCurrentTime_IfNotClosed() {
		schedulerStub.setFixedTime("2017-08-04T02:35:00Z");
		
		assertEquals(T("2017-08-04T02:35:00Z"), controller.getCurrentTime(position));
	}

	@Test
	public void testPositionController_GetCurrentTime_IfClosed() {
		position.close();
		
		assertNull(controller.getCurrentTime(position));
	}
	
	@Test
	public void testClose() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T18:18:00Z");
		position.onClose().addListener(listenerStub);
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
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0), position.onClose(), position, T("2017-08-04T18:18:00Z"));
	}
	
	@Test
	public void testUpdate_OnAvailable() throws Exception {
		Instant time = T("2017-08-04T17:57:00Z");
		container = produceContainer(controllerMock);
		container.onAvailable().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(position)).andReturn(time);
		controllerMock.processUpdate(position, time);
		expect(controllerMock.hasMinimalData(position, time)).andReturn(true);
		controllerMock.processAvailable(position, time);
		getMocksControl().replay();

		position.update(12345, 415); // any value
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEvent(listenerStub.getEvent(0), position.onAvailable(), position, time, 12345);
	}
	
	@Test
	public void testUpdate_OnUpdateEvent() throws Exception {
		Instant time1 = T("2017-08-04T17:58:00Z"), time2 = T("2017-08-04T17:59:00Z");
		container = produceContainer(controllerMock);
		container.onUpdate().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(position)).andReturn(time1);
		controllerMock.processUpdate(position, time1);
		expect(controllerMock.hasMinimalData(position, time1)).andReturn(true);
		controllerMock.processAvailable(position, time1);
		expect(controllerMock.getCurrentTime(position)).andReturn(time2);
		controllerMock.processUpdate(position, time2);
		getMocksControl().replay();
		
		position.update(12345, 415);
		position.update(12345, 450);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertContainerEvent(listenerStub.getEvent(0), position.onUpdate(), position, time1, 12345);
		assertContainerEvent(listenerStub.getEvent(1), position.onUpdate(), position, time2, 12345);
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
