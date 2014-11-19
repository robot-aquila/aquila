package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Direction;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.TerminalImpl;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.Tick;

public class BMUtilsTest {
	private static final SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("foo", "bar", "USD");
	}
	
	@SuppressWarnings("rawtypes")
	private EditableTerminal terminal;
	private EditableSecurity security; 
	private BMUtils utils;

	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		utils = new BMUtils();
		terminal = new TerminalImpl("foobar");
		security = terminal.getEditableSecurity(descr);
	}

	@Test
	public void testTradeFromTick() {
		security.setMinStepSize(0.01d);
		security.setMinStepPrice(0.02d);
		
		Trade expected = new Trade(terminal);
		expected.setDirection(Direction.BUY);
		expected.setPrice(212.45d);
		expected.setQty(824L);
		expected.setSecurityDescriptor(descr);
		expected.setTime(new DateTime(2014, 11, 19, 4, 37, 39, 500));
		expected.setVolume(350117.6);
		
		assertEquals(expected, utils.tradeFromTick(
			new Tick(new DateTime(2014, 11, 19, 4, 37, 39, 500), 212.45d, 824d),
			security));
	}

}
