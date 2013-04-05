package ru.prolib.aquila.dde.utils.table;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDETable;

/**
 * 2012-08-15<br>
 * $Id: DDETableHandlerImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class DDETableHandlerImplTest {
	private IMocksControl control;
	private DDETableRowSetBuilder setBuilder;
	private RowHandler rowHandler;
	private DDETableHandlerImpl handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		setBuilder = control.createMock(DDETableRowSetBuilder.class);
		rowHandler = control.createMock(RowHandler.class);
		handler = new DDETableHandlerImpl(setBuilder, rowHandler);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Object fixture[][] = {
				// set builder, row handler, exception?
				{ setBuilder, rowHandler, false },
				{ null,		  rowHandler, true  },
				{ setBuilder, null,		  true  },
				{ null,		  null,		  true  }
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			DDETableRowSetBuilder sb = (DDETableRowSetBuilder)fixture[i][0];
			RowHandler rh = (RowHandler)fixture[i][1];
			boolean exception = false;
			try {
				handler = new DDETableHandlerImpl(sb, rh);
			} catch ( NullPointerException e ) {
				exception = true;
			}
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fixture[i][2], exception);
			if ( ! exception ) {
				assertSame(msg, sb, handler.getRowSetBuilder());
				assertSame(msg, rh, handler.getRowHandler());
			}
		}
	}
	
	@Test
	public void testHandle() throws Exception {
		DDETable table = control.createMock(DDETable.class);
		RowSet rs = control.createMock(RowSet.class);
		expect(setBuilder.createRowSet(same(table))).andReturn(rs);
		for ( int i = 0; i < 5; i ++ ) {
			expect(rs.next()).andReturn(true);
			rowHandler.handle(same(rs));
		}
		expect(rs.next()).andReturn(false);
		control.replay();
		
		handler.handle(table);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<DDETableRowSetBuilder> sb = new Variant<DDETableRowSetBuilder>()
			.add(setBuilder)
			.add(control.createMock(DDETableRowSetBuilder.class));
		Variant<RowHandler> rh = new Variant<RowHandler>(sb)
			.add(rowHandler)
			.add(control.createMock(RowHandler.class));
		int foundCnt = 0;
		DDETableHandlerImpl found = null;
		do {
			DDETableHandlerImpl x = new DDETableHandlerImpl(sb.get(), rh.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( rh.next() );
		assertEquals(1, foundCnt);
		assertSame(setBuilder, found.getRowSetBuilder());
		assertSame(rowHandler, found.getRowHandler());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 201147)
			.append(setBuilder)
			.append(rowHandler)
			.toHashCode();
		assertEquals(hashCode, handler.hashCode());
	}

}
