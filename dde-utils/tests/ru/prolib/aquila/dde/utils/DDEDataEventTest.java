package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

public class DDEDataEventTest {
	private IMocksControl control;
	private EventType type;
	private byte[] data;
	private DDEDataEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		data = new byte[128];
		for ( int i = 0; i < data.length; i ++ ) { data[i] = (byte) (i / 2); }
		event = new DDEDataEvent(type, "service", "topic", "item", data);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type, event.getType());
		assertEquals("service", event.getService());
		assertEquals("topic", event.getTopic());
		assertEquals("item", event.getItem());
		assertSame(data, event.getData());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfEventTypeIsNull() throws Exception {
		new DDEDataEvent(null, "service", "topic", "item", data);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfServiceIsNull() throws Exception {
		new DDEDataEvent(type, null, "topic", "item", data);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTopicIsNull() throws Exception {
		new DDEDataEvent(type, "service", null, "item", data);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfItemIsNull() throws Exception {
		new DDEDataEvent(type, "service", "topic", null, data);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfDataIsNull() throws Exception {
		new DDEDataEvent(type, "service", "topic", "item", null);
	}
	
	@Test
	public void testEquals() throws Exception {
		byte data2[] = new byte[128];
		for ( int i = 0; i < data2.length; i ++ ) { data2[i] = (byte) (i / 2); }
		byte data3[] = new byte[256];
		for ( int i = 0; i < data3.length; i ++ ) { data3[i] = (byte) (i / 2); }
		byte data4[] = new byte[2]; data4[0] = 64; data4[1] = 32;
		
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<String> vService = new Variant<String>(vType)
			.add("service")
			.add("bubba");
		Variant<String> vTopic = new Variant<String>(vService)
			.add("topic")
			.add("gupta depot");
		Variant<String> vItem = new Variant<String>(vTopic)
			.add("item")
			.add("cappa sport");
		Variant<byte[]> vData = new Variant<byte[]>(vItem)
			.add(data2)
			.add(data3)
			.add(data4);
		int foundCnt = 0;
		DDEDataEvent found = null, x = null;
		do {
			x = new DDEDataEvent(vType.get(), vService.get(), vTopic.get(),
					vItem.get(), vData.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vData.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals("service", found.getService());
		assertEquals("topic", found.getTopic());
		assertEquals("item", found.getItem());
		assertArrayEquals(data, found.getData());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(this));
		assertFalse(event.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/85051)
			.append(type)
			.append("service")
			.append("topic")
			.append("item")
			.append(data)
			.toHashCode();
		assertEquals(hashCode, event.hashCode());
	}

}
