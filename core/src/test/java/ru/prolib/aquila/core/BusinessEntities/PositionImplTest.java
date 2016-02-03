package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.PositionImpl.PositionController;
import ru.prolib.aquila.core.data.ContainerImpl;
import ru.prolib.aquila.core.data.ContainerImplTest;
import ru.prolib.aquila.core.data.PositionField;

/**
 * 2012-08-03<br>
 * $Id: PositionImplTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImplTest extends ContainerImplTest {
	private static Symbol symbol = new Symbol("S:GAZP@EQBR:RUB");
	private static Account account = new Account("TST01");
	private IMocksControl control;
	private EditableTerminal terminal;
	private PositionImpl position;

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
		return position.getContainerID();
	}
	
	private void prepareTerminal() {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getTerminalID()).andStubReturn("foobar");
		expect(terminal.getEventQueue()).andStubReturn(queue);
		control.replay();		
	}
	
	@Override
	protected ContainerImpl produceContainer() {
		prepareTerminal();
		position = new PositionImpl(terminal, account, symbol);
		return position;
	}
	
	@Override
	protected ContainerImpl produceContainer(ContainerImpl.Controller controller) {
		prepareTerminal();
		position = new PositionImpl(terminal, account, symbol, controller);
		return position;
	}
	
	@Test
	public void testCtor_DefaultController() {
		position = new PositionImpl(terminal, account, symbol);
		assertEquals(PositionController.class, position.getController().getClass());
		assertNotNull(position.getTerminal());
		assertNotNull(position.getEventQueue());
		assertSame(terminal, position.getTerminal());
		assertSame(queue, position.getEventQueue());
		assertEquals(account, position.getAccount());
		assertEquals(symbol, position.getSymbol());
		String prefix = String.format("%s.TST01[S:GAZP@EQBR:RUB].", terminal.getTerminalID());
		assertEquals(prefix + "POSITION", position.getContainerID());
		assertEquals(prefix + "POSITION.AVAILABLE", position.onAvailable().getId());
		assertEquals(prefix + "POSITION.POSITION_CHANGE", position.onPositionChange().getId());
		assertEquals(prefix + "POSITION.CURRENT_PRICE_CHANGE", position.onCurrentPriceChange().getId());
	}
	
	@Test
	public void testGetVariationMargin() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return position.getVariationMargin();
			}			
		};
		testGetter(PositionField.VARIATION_MARGIN, 415.345d, 280.34d);
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
		getter = new Getter<Double>() {
			@Override public Double get() {
				return position.getCurrentPrice();
			}
		};
		testGetter(PositionField.CURRENT_PRICE, 2014d, 882.15d);
	}
	
	@Test
	public void testGetOpenVolume() throws Exception {
		getter = new Getter<Long>() {
			@Override public Long get() {
				return position.getOpenVolume();
			}
		};
		testGetter(PositionField.OPEN_VOLUME, 580L, 240L);
	}

	@Test
	public void testGetOpenPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return position.getOpenPrice();
			}
		};
		testGetter(PositionField.OPEN_PRICE, 551.13d, 902.08d);
	}

	@Test
	public void testPositionController_HasMinimalData() {
		PositionController controller = new PositionController();
		
		assertFalse(controller.hasMinimalData(position));
		
		data.put(PositionField.CURRENT_VOLUME, 1000L);
		position.update(data);
		
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
		data.put(PositionField.CURRENT_PRICE, 4518.96d);
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
		data.put(PositionField.CURRENT_PRICE, 4582.13d);
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

}
