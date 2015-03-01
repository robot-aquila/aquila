package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-04<br>
 * $Id$
 */
public class PanicEventTest {
	private static IMocksControl control;
	private static EventTypeSI type,type2;
	private static PanicEvent event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = new EventTypeImpl("foo");
		type2 = new EventTypeImpl("bar");
		event = new PanicEvent(type, 123, "TEST_MSG", new Object[] { 1, "A" });
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct4() throws Exception {
		assertSame(type, event.getType());
		assertEquals(123, event.getCode());
		assertEquals("TEST_MSG", event.getMessageId());
		assertArrayEquals(new Object[] { 1, "A" }, event.getArgs());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		PanicEvent event = new PanicEvent(type2, 678, "MSG");
		assertSame(type2, event.getType());
		assertEquals(678, event.getCode());
		assertArrayEquals(new Object[] { }, event.getArgs());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventTypeSI> vType = new Variant<EventTypeSI>()
			.add(type)
			.add(type2);
		Variant<Integer> vCode = new Variant<Integer>(vType)
			.add(123)
			.add(987);
		Variant<String> vMsg = new Variant<String>(vCode)
			.add("TEST_MSG")
			.add("UNKNOWN");
		Variant<Object[]> vArgs = new Variant<Object[]>(vMsg)
			.add(new Object[] { 1, "A" })
			.add(new Object[] { 567 });
		Variant<?> integer = vArgs;
		int foundCnt = 0;
		PanicEvent found = null, x = null;
		do {
			x = new PanicEvent(vType.get(),vCode.get(),vMsg.get(),vArgs.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( integer.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(123, found.getCode());
		assertEquals("TEST_MSG", found.getMessageId());
		assertArrayEquals(new Object[] { 1, "A" }, found.getArgs());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130205, 191437)
			.append(type)
			.append(123)
			.append("TEST_MSG")
			.append(new Object[] { 1, "A" })
			.toHashCode(), event.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("foo.PanicEvent[123] TEST_MSG", event.toString());
	}

}
