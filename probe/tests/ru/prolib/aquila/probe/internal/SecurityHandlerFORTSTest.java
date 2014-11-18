package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.PROBETerminal;

public class SecurityHandlerFORTSTest {
	private static SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("RTS-12.14", "FORTS", "USD", SecurityType.FUT);
	}
	
	private IMocksControl control;
	private EditableSecurity security;
	private PROBETerminal terminal;
	private SecurityProperties props;
	private SecurityHandlerFORTS handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = new PROBETerminal("foo");
		security = terminal.getEditableSecurity(descr);
		props = new SecurityProperties();
		props.setDisplayName("RTS-future-12.14");
		props.setLotSize(1);
		props.setPricePrecision(0);
		props.setMinStepSize(10d);
		props.setInitialMarginCalculationBase(0.15d);
		props.setStepPriceCalculationBase(0.2d);
		handler = new SecurityHandlerFORTS(terminal, security, props);
	}
	
	@Test
	public void testSchemeVersions() throws Exception {
		assertEquals(1, Security.VERSION);
	}
	
	@Test
	public void testDoInitialTask() throws Exception {
		handler.doInitialTask(new Tick(DateTime.now(), 142912d));
		
		assertNull(security.getAskPrice());
		assertNull(security.getAskSize());
		assertNull(security.getBidPrice());
		assertNull(security.getBidSize());
		assertNull(security.getClosePrice());
		assertEquals("RTS-future-12.14", security.getDisplayName());
		assertNull(security.getHighPrice());
		assertEquals(142912d, security.getInitialMargin(), 0.01d);
		assertEquals(142912d, security.getInitialPrice(), 0.01d);
		assertNull(security.getLastPrice());
		assertNull(security.getLastTrade());
		assertEquals(1, security.getLotSize());
		assertNull(security.getLowPrice());
		assertNull(security.getMaxPrice());
		assertNull(security.getMinPrice());
		assertEquals(1d, security.getMinStepPrice(), 0.1d);
		assertEquals(10d, security.getMinStepSize(), 0.1d);
		assertNull(security.getOpenPrice());
		assertEquals(0, security.getPrecision());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() {
		fail("TODO: incomplete");
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
