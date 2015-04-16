package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.Tick;

public class BMUtilsTest {
	private static final SecurityDescriptor descr;
	private static final Tick tick;
	
	static {
		descr = new SecurityDescriptor("foo", "bar", "USD");
		tick = new Tick(new DateTime(2014, 11, 19, 4, 37, 39, 50), 21.45d, 84d);
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security; 
	private BMUtils utils;

	@Before
	public void setUp() throws Exception {
		utils = new BMUtils();
		terminal = new TerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(descr);
	}

	@Test
	public void testTradeFromTick() {
		security.setMinStepSize(0.01d);
		security.setMinStepPrice(0.02d);
		
		Trade expected = new Trade(terminal);
		expected.setDirection(Direction.BUY);
		expected.setPrice(21.45d);
		expected.setQty(84L);
		expected.setSecurityDescriptor(descr);
		expected.setTime(new DateTime(2014, 11, 19, 4, 37, 39, 50));
		expected.setVolume(3603.6);
		
		assertEquals(expected, utils.tradeFromTick(tick, security));
	}
	
	@Test (expected=NullPointerException.class)
	public void testTradeFromTick_ThrowsIfMinStepSizeNotDefined()
			throws Exception
	{
		security.setMinStepPrice(0.02d);
		
		utils.tradeFromTick(tick, security);
	}
	
}
