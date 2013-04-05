package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-01-11<br>
 * $Id: SetupPositionsEventTest.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionsEventTest {
	private static IMocksControl control;
	private static EventType type;
	private static SetupPositions setup;
	private static SetupPositionsEvent event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		setup = control.createMock(SetupPositions.class);
		event = new SetupPositionsEvent(type, setup);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<SetupPositions> vSetup = new Variant<SetupPositions>(vType)
			.add(setup)
			.add(control.createMock(SetupPositions.class));
		Variant<?> iterator = vSetup;
		int foundCnt = 0;
		SetupPositionsEvent x = null, found = null;
		do {
			x = new SetupPositionsEvent(vType.get(), vSetup.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertSame(setup, found.getPositions());
	}

}
