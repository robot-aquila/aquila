package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityImpl.SecurityController;
import ru.prolib.aquila.core.data.*;

/**
 * 2012-05-30<br>
 * $Id: SecurityImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class SecurityImplTest extends ContainerImplTest {
	private static Symbol symbol1 = new Symbol("S:GAZP@EQBR:RUB");
	private IMocksControl control;
	private EditableTerminal terminal;
	private SecurityImpl security;
	
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
		return security.getContainerID();
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
		security = new SecurityImpl(terminal, symbol1);
		return security;
	}
	
	@Override
	protected ContainerImpl produceContainer(ContainerImpl.Controller controller) {
		prepareTerminal();
		security = new SecurityImpl(terminal, symbol1, controller);
		return security;
	}
	
	@Test
	public void testCtor_DefaultController() throws Exception {
		security = new SecurityImpl(terminal, symbol1);
		assertEquals(SecurityController.class, security.getController().getClass());
		assertNotNull(security.getTerminal());
		assertNotNull(security.getEventQueue());
		assertSame(terminal, security.getTerminal());
		assertSame(queue, security.getEventQueue());
		assertEquals(symbol1, security.getSymbol());
		String prefix = String.format("%s.S:GAZP@EQBR:RUB.", "foobar");
		assertEquals(prefix + "SECURITY", security.getContainerID());
		assertEquals(prefix + "SECURITY.SESSION_UPDATE", security.onSessionUpdate().getId());
		assertFalse(security.isAvailable());
	}

	@Test
	public void testGetScale() throws Exception {
		getter = new Getter<Integer>() {
			@Override public Integer get() {
				return security.getScale();
			}
		};
		testGetter(SecurityField.SCALE, 2, 4);
	}
	
	@Test
	public void testGetLotSize() throws Exception {
		getter = new Getter<Integer>() {
			@Override public Integer get() {
				return security.getLotSize();
			}	
		};
		testGetter(SecurityField.LOT_SIZE, 10, 1);
	}
	
	@Test
	public void testGetUpperPriceLimit() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getUpperPriceLimit();
			}
		};
		testGetter(SecurityField.UPPER_PRICE_LIMIT, 137.15d, 158.12d);
	}
	
	@Test
	public void testGetLowerPriceLimit() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getLowerPriceLimit();
			}
		};
		testGetter(SecurityField.LOWER_PRICE_LIMIT, 119.02d, 118.16d);
	}
	
	@Test
	public void testGetTickValue() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getTickValue();
			}
		};
		testGetter(SecurityField.TICK_VALUE, 440.09d, 482.15d);
	}
	
	@Test
	public void testGetTickSize() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getTickSize();
			}
		};
		testGetter(SecurityField.TICK_SIZE, 10.0d, 0.05d);
	}
	
	@Test
	public void testGetDisplayName() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return security.getDisplayName();
			}
		};
		testGetter(SecurityField.DISPLAY_NAME, "foo", "bar");
	}	
	
	@Test
	public void testGetOpenPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getOpenPrice();
			}
		};
		testGetter(SecurityField.OPEN_PRICE, 321.19d, 280.04d);
	}
	
	@Test
	public void testGetClosePrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getClosePrice();
			}
		};
		testGetter(SecurityField.CLOSE_PRICE, 10.03d, 12.34d);
	}
	
	@Test
	public void testGetLowPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getLowPrice();
			}
		};
		testGetter(SecurityField.LOW_PRICE, 8.02d, 15.87d);
	}

	@Test
	public void testGetHighPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getHighPrice();
			}
		};
		testGetter(SecurityField.HIGH_PRICE, 4586.13d, 7002.17d);
	}

	@Test
	public void testGetInitialPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getInitialPrice();
			}
		};
		testGetter(SecurityField.INITIAL_PRICE, 215.86d, 114.12d);
	}

	@Test
	public void testGetInitialMargin() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return security.getInitialMargin();
			}
		};
		testGetter(SecurityField.INITIAL_MARGIN, 118.99d, 120.01d);
	}
	
	@Test
	public void testClose() {
		EventType type = new EventTypeImpl();
		EventListener listener = new EventListenerStub();
		security.onAvailable().addListener(listener);
		security.onAvailable().addAlternateType(type);
		security.onSessionUpdate().addListener(listener);
		security.onSessionUpdate().addAlternateType(type);
		security.onUpdate().addListener(listener);
		security.onUpdate().addAlternateType(type);
		
		security.close();
		
		assertFalse(security.onAvailable().hasListeners());
		assertFalse(security.onAvailable().hasAlternates());
		assertFalse(security.onSessionUpdate().hasListeners());
		assertFalse(security.onSessionUpdate().hasAlternates());
		assertFalse(security.onUpdate().hasListeners());
		assertFalse(security.onUpdate().hasAlternates());
		assertNull(security.getTerminal());
		assertFalse(security.isAvailable());
		assertTrue(security.isClosed());
	}
	
	@Test
	public void testSecurityController_HasMinimalData() throws Exception {
		SecurityController controller = new SecurityController();
		
		assertFalse(controller.hasMinimalData(security));
		
		data.put(SecurityField.DISPLAY_NAME, "GAZP");
		data.put(SecurityField.SCALE, 2);
		data.put(SecurityField.LOT_SIZE, 100);
		data.put(SecurityField.TICK_SIZE, 5d);
		data.put(SecurityField.TICK_VALUE, 2.37d);
		security.update(data);
		
		assertTrue(controller.hasMinimalData(security));
	}
	
	@Test
	public void testSecurityController_ProcessUpdate_SessionUpdate() {
		data.put(SecurityField.SCALE, 10);
		data.put(SecurityField.LOT_SIZE, 1);
		data.put(SecurityField.TICK_SIZE, 0.05d);
		data.put(SecurityField.TICK_VALUE, 0.01d);
		data.put(SecurityField.INITIAL_MARGIN, 2034.17d);
		data.put(SecurityField.INITIAL_PRICE, 80.93d);
		data.put(SecurityField.OPEN_PRICE, 79.19d);
		data.put(SecurityField.HIGH_PRICE, 83.64d);
		data.put(SecurityField.LOW_PRICE, 79.19d);
		data.put(SecurityField.CLOSE_PRICE, 79.18d);
		security.update(data);
		SecurityController controller = new SecurityController();
		EventListenerStub listener = new EventListenerStub();
		security.onSessionUpdate().addSyncListener(listener);
		
		controller.processUpdate(security);
		
		assertEquals(1, listener.getEventCount());
		SecurityEvent e = (SecurityEvent) listener.getEvent(0);
		assertTrue(e.isType(security.onSessionUpdate()));
		assertSame(security, e.getSecurity());
	}

	@Test
	public void testSecurityController_ProcessAvailable() {
		data.put(SecurityField.SCALE, 10);
		data.put(SecurityField.LOT_SIZE, 1);
		data.put(SecurityField.TICK_SIZE, 0.05d);
		data.put(SecurityField.TICK_VALUE, 0.01d);
		data.put(SecurityField.INITIAL_MARGIN, 2034.17d);
		data.put(SecurityField.INITIAL_PRICE, 80.93d);
		data.put(SecurityField.OPEN_PRICE, 79.19d);
		data.put(SecurityField.HIGH_PRICE, 83.64d);
		data.put(SecurityField.LOW_PRICE, 79.19d);
		data.put(SecurityField.CLOSE_PRICE, 79.18d);
		security.update(data);
		SecurityController controller = new SecurityController();
		EventListenerStub listener = new EventListenerStub();
		security.onSessionUpdate().addSyncListener(listener);
		
		controller.processAvailable(security);
		
		assertEquals(0, listener.getEventCount());
	}

}
