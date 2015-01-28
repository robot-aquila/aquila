package ru.prolib.aquila.core.fsm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.fsm.FSMEventType;
import ru.prolib.aquila.core.fsm.FSMStateActor;
import ru.prolib.aquila.core.utils.Variant;

public class FSMEventTypeTest {
	private IMocksControl control;
	private FSMStateActor actor;
	private FSMEventType type;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		actor = control.createMock(FSMStateActor.class);
		type = new FSMEventType(actor, "foo");
	}
	
	@Test
	public void testBaseClass() throws Exception {
		assertTrue(type instanceof EventTypeImpl);
	}
	
	@Test
	public void testGetOwner() throws Exception {
		assertSame(actor, type.getOwner());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(type.equals(type));
		assertFalse(type.equals(null));
		assertFalse(type.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<FSMStateActor> vOwner = new Variant<FSMStateActor>()
			.add(actor)
			.add(control.createMock(FSMStateActor.class));
		Variant<String> vId = new Variant<String>(vOwner)
			.add("foo")
			.add("bar");
		Variant<?> iterator = vId;
		int foundCnt = 0;
		FSMEventType x, found = null;
		do {
			x = new FSMEventType(vOwner.get(), vId.get());
			if ( type.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(actor, found.getOwner());
		assertEquals("foo", found.getId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		String expectedId =
			EventTypeImpl.AUTO_ID_PREFIX + EventTypeImpl.getAutoId();
		type = new FSMEventType(actor);
		assertSame(actor, type.getOwner());
		assertEquals(expectedId, type.getId());
	}

}
