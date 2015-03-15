package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.dde.DDETable;

public class DDETableEventTest {
	private IMocksControl control;
	private EventTypeSI type1,type2;
	private DDETable table1,table2;
	private DDETableEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventTypeSI.class);
		type2 = control.createMock(EventTypeSI.class);
		table1 = control.createMock(DDETable.class);
		table2 = control.createMock(DDETable.class);
		event = new DDETableEvent(type1, "service", table1);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type1, event.getType());
		assertEquals("service", event.getService());
		assertSame(table1, event.getTable());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfEventTypeIsNull() throws Exception {
		new DDETableEvent(null, "service", table1);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfServiceIsNull() throws Exception {
		new DDETableEvent(type1, null, table1);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTableIsNull() throws Exception {
		new DDETableEvent(type1, "service", null);
	}
	
	@Test
	public void testEquals() throws Exception {
		Object fix[][] = {
			// to compare, expected result
			{ new DDETableEvent(type1, "service", table1), true  },
			{ new DDETableEvent(type2, "service", table1), false },
			{ new DDETableEvent(type1, "sxxxice", table1), false },
			{ new DDETableEvent(type1, "service", table2), false },
			{ null,							 			   false },
			{ event,						 			   true  },
			{ this,							 			   false }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][1], event.equals(fix[i][0]));
		}
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/72129)
			.append(type1)
			.append("service")
			.append(table1)
			.toHashCode();
		assertEquals(hashCode, event.hashCode());
	}

}
