package ru.prolib.aquila.ta.ds.quik;


import org.junit.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import ru.prolib.aquila.rxltdde.Xlt;

public class RXltDdeDispatcherTest {
	RXltDdeDispatcher dispatcher;
	RXltDdeTableHandler h1,h2,h3;

	@Before
	public void setUp() throws Exception {
		h1 = createMock(RXltDdeTableHandler.class);
		h2 = createMock(RXltDdeTableHandler.class);
		h3 = createMock(RXltDdeTableHandler.class);
		dispatcher = new RXltDdeDispatcher();
	}
	
	@Test
	public void testInitialState() throws Exception {
		assertEquals(0, dispatcher.map.size());
	}
	
	@Test
	public void testAddRemoveClear() throws Exception {
		dispatcher.add("foobar", h1);
		assertEquals(1, dispatcher.map.size());
		assertTrue(dispatcher.map.containsKey("foobar"));
		assertEquals(1, dispatcher.map.get("foobar").size());
		assertTrue(dispatcher.map.get("foobar").contains(h1));
		
		dispatcher.add("foobar", h2);
		assertEquals(1, dispatcher.map.size());
		assertTrue(dispatcher.map.containsKey("foobar"));
		assertEquals(2, dispatcher.map.get("foobar").size());
		assertTrue(dispatcher.map.get("foobar").contains(h1));
		assertTrue(dispatcher.map.get("foobar").contains(h2));

		dispatcher.add("zulu4", h3);
		assertEquals(2, dispatcher.map.size());
		assertTrue(dispatcher.map.containsKey("foobar"));
		assertEquals(2, dispatcher.map.get("foobar").size());
		assertTrue(dispatcher.map.get("foobar").contains(h1));
		assertTrue(dispatcher.map.get("foobar").contains(h2));
		assertTrue(dispatcher.map.containsKey("zulu4"));
		assertEquals(1, dispatcher.map.get("zulu4").size());
		assertTrue(dispatcher.map.get("zulu4").contains(h3));
		
		dispatcher.remove("foobar", h1);
		assertEquals(2, dispatcher.map.size());
		assertTrue(dispatcher.map.containsKey("foobar"));
		assertEquals(1, dispatcher.map.get("foobar").size());
		assertFalse(dispatcher.map.get("foobar").contains(h1));
		assertTrue(dispatcher.map.get("foobar").contains(h2));
		assertTrue(dispatcher.map.containsKey("zulu4"));
		assertEquals(1, dispatcher.map.get("zulu4").size());
		assertTrue(dispatcher.map.get("zulu4").contains(h3));

		dispatcher.remove("foobar", h3); // should not be changed
		assertEquals(2, dispatcher.map.size());
		assertTrue(dispatcher.map.containsKey("foobar"));
		assertEquals(1, dispatcher.map.get("foobar").size());
		assertFalse(dispatcher.map.get("foobar").contains(h1));
		assertTrue(dispatcher.map.get("foobar").contains(h2));
		assertTrue(dispatcher.map.containsKey("zulu4"));
		assertEquals(1, dispatcher.map.get("zulu4").size());
		assertTrue(dispatcher.map.get("zulu4").contains(h3));
		
		dispatcher.remove("foobar", h2);
		assertEquals(1, dispatcher.map.size());
		assertFalse(dispatcher.map.containsKey("foobar"));
		assertTrue(dispatcher.map.containsKey("zulu4"));
		assertEquals(1, dispatcher.map.get("zulu4").size());
		assertTrue(dispatcher.map.get("zulu4").contains(h3));

		dispatcher.clear();
		assertEquals(0, dispatcher.map.size());
	}
	
	@Test
	public void testOnRawData_NoHandlers() throws Exception {
		byte[] data = {1,2,3,4,5};
		dispatcher.onRawData("foobar", "R1C1:R1C1", data);
	}
	
	@Test
	public void testOnRawData_HasHandlers() throws Exception {
		byte[] data = {1,2,3,4,5};
		Xlt.ITable table = createMock(Xlt.ITable.class);
		h1.onTable(same(table));
		h2.onTable(same(table));
		replay(h1);
		replay(h2);
		dispatcher = createMockBuilder(RXltDdeDispatcher.class)
			.addMockedMethod("readTable")
			.withConstructor()
			.createMock();
		expect(dispatcher.readTable(eq("foobar"), eq("R1C1:R1C1"), same(data)))
			.andReturn(table);
		replay(dispatcher);
		
		dispatcher.add("foobar", h1);
		dispatcher.add("foobar", h2);
		
		dispatcher.onRawData("foobar", "R1C1:R1C1", data);
		
		verify(h1);
		verify(h2);
		verify(dispatcher);
	}

}
