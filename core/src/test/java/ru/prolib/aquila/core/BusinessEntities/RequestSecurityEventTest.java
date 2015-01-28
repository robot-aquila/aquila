package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

public class RequestSecurityEventTest {
	private IMocksControl control;
	private EventType type1, type2;
	private SecurityDescriptor descr1, descr2;
	private RequestSecurityEvent event;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		descr1 = new SecurityDescriptor("SBER", "EQBR", "RUR");
		descr2 = new SecurityDescriptor("GAZP", "EQBR", "RUR");
		event = new RequestSecurityEvent(type1, descr1, 1, "foobar text");
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertEquals(type1, event.getType());
		assertEquals(descr1, event.getSecurityDescriptor());
		assertEquals(1, event.getCode());
		assertEquals("foobar text", event.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type1)
			.add(type2);
		Variant<SecurityDescriptor> vDescr =
				new Variant<SecurityDescriptor>(vType)
			.add(null)
			.add(descr1)
			.add(descr2);
		Variant<Integer> vCode = new Variant<Integer>(vDescr)
			.add(1)
			.add(256);
		Variant<String> vMsg = new Variant<String>(vCode)
			.add(null)
			.add("foobar text")
			.add("zulu charlie");
		Variant<?> it = vMsg;
		int foundCount = 0;
		RequestSecurityEvent found = null;
		do {
			RequestSecurityEvent actual = new RequestSecurityEvent(vType.get(),
					vDescr.get(), vCode.get(), vMsg.get());
			if ( event.equals(actual) ) {
				foundCount ++;
				found = actual;
			}
		} while ( it.next() );
		assertEquals(1, foundCount);
		assertNotNull(found);
		assertSame(type1, found.getType());
		assertSame(descr1, found.getSecurityDescriptor());
		assertEquals(1, found.getCode());
		assertEquals("foobar text", found.getMessage());
	}

}
