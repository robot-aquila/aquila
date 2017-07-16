package ru.prolib.aquila.core.fsm;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.fsm.FSMEventDispatcher;
import ru.prolib.aquila.core.fsm.FSMEventType;
import ru.prolib.aquila.core.fsm.FSMStateActor;
import ru.prolib.aquila.core.utils.Variant;

public class FSMEventDispatcherTest {
	private EventSystem es;
	private IMocksControl control;
	private FSMStateActor actor;
	private FSMEventDispatcher dispatcher;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		control = createStrictControl();
		actor = control.createMock(FSMStateActor.class);
		dispatcher = new FSMEventDispatcher(es.getEventQueue(), actor, "bar");
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testBaseClass() throws Exception {
		assertTrue(dispatcher instanceof EventDispatcherImpl);
	}
	
	@Test
	public void testGetOwner() throws Exception {
		assertSame(actor, dispatcher.getOwner());
	}
	
	@Test
	public void testCreateType0() throws Exception {
		String expectedId =
			"bar." + EventTypeImpl.AUTO_ID_PREFIX + EventTypeImpl.getAutoId();
		FSMEventType expected = new FSMEventType(actor, expectedId);
		FSMEventType actual = dispatcher.createType();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateType1() throws Exception {
		FSMEventType expected = new FSMEventType(actor, "bar.foo");
		FSMEventType actual = dispatcher.createType("foo");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetCreatedTypes() throws Exception {
		List<FSMEventType> list = new Vector<FSMEventType>();
		list.add(dispatcher.createType());
		list.add(dispatcher.createType("zulu"));
		list.add(dispatcher.createType());
		assertEquals(list, dispatcher.getCreatedTypes());
	}
	
	@Test
	public void testGetCreatedType() throws Exception {
		FSMEventType type1 = dispatcher.createType();
		FSMEventType type2 = dispatcher.createType("foobar");
		FSMEventType type3 = dispatcher.createType("zulu");
		assertSame(type1, dispatcher.getCreatedType(0));
		assertSame(type2, dispatcher.getCreatedType(1));
		assertSame(type3, dispatcher.getCreatedType(2));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(dispatcher.equals(dispatcher));
		assertFalse(dispatcher.equals(null));
		assertFalse(dispatcher.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<String> rows1 = new Vector<String>();
		rows1.add("zulu");
		rows1.add("gamma");
		for ( String id : rows1 ) dispatcher.createType(id);
		List<String> rows2 = new Vector<String>();
		rows2.add("zulu");
		rows2.add("baka");
		rows2.add("zephyr");
		Variant<FSMStateActor> vOwner = new Variant<FSMStateActor>()
			.add(actor)
			.add(control.createMock(FSMStateActor.class));
		Variant<String> vId = new Variant<String>(vOwner)
			.add("bar")
			.add("foo");
		Variant<List<String>> vRows = new Variant<List<String>>(vId)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		FSMEventDispatcher x, found = null;
		do {
			x = new FSMEventDispatcher(es.getEventQueue(), vOwner.get(), vId.get());
			for ( String id : vRows.get() ) x.createType(id);
			if ( dispatcher.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(actor, found.getOwner());
		assertEquals("bar", found.getId());
		assertEquals(dispatcher.getCreatedTypes(), found.getCreatedTypes());
	}
	
	@Test
	public void testFireEvent() throws Exception {
		FSMEventType type1 = dispatcher.createType("Test");
		final List<Event> actual = new Vector<Event>();
		type1.addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		});
		
		dispatcher.fireEvent(type1);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new EventImpl(type1));
		assertEquals(expected, actual);
	}

}
