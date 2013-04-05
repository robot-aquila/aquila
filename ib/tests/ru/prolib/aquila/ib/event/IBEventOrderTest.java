package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventOrder;

/**
 * 2012-12-11<br>
 * $Id: IBEventOrderTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventOrderTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBEventOrder event;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		event = new IBEventOrder(type, 100500);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(type, event.getType());
		assertEquals(100500, event.getOrderId());
	}
	
	@Test
	public void testEquals() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		assertTrue(event.equals(event));
		assertTrue(event.equals(new IBEventOrder(type, 100500)));
		assertFalse(event.equals(new IBEventOrder(type, 20000)));
		assertFalse(event.equals(new IBEventOrder(type2, 100500)));
		assertFalse(event.equals(null));
		assertFalse(event.equals(true));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121211, 102449)
			.append(type)
			.append(100500)
			.toHashCode(), event.hashCode());
	}

}
