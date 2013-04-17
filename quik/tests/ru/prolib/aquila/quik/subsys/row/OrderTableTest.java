package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrderTableTest {
	private static Account acc1, acc2;
	private static SecurityDescriptor descr1, descr2;
	private static OrderTableRow row1, row2, row3;
	private OrderTable table;
	private IMocksControl control;
	private EventDispatcher dispatcher1, dispatcher2;
	private EventType type1, type2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		acc1 = new Account("foo");
		acc2 = new Account("bar");
		descr1 = new SecurityDescriptor("SBER","EQBR","RUB",SecurityType.STK);
		descr2 = new SecurityDescriptor("RIM3","SPFUT","RUB",SecurityType.FUT);
		row1 = new OrderTableRow(100L, 500L, acc1, new Date(),
				OrderDirection.BUY, descr1, 10L, 34.90D, 1L,
				OrderStatus.ACTIVE, OrderType.LIMIT);
		row2 = new OrderTableRow(120L, 501L, acc2, new Date(),
				OrderDirection.SELL, descr2, 1L, null, 1L,
				OrderStatus.ACTIVE, OrderType.MARKET);
		row3 = new OrderTableRow(120L, 501L, acc2, new Date(),
				OrderDirection.SELL, descr2, 1L, null, 0L,
				OrderStatus.FILLED, OrderType.MARKET);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher1 = control.createMock(EventDispatcher.class);
		expect(dispatcher1.asString()).andStubReturn("test");
		dispatcher2 = control.createMock(EventDispatcher.class);
		expect(dispatcher2.asString()).andStubReturn("foobar");
		type1 = new EventTypeImpl(dispatcher1);
		type2 = new EventTypeImpl(dispatcher2);
		
		table = new OrderTable(dispatcher1, type1);
		table.setRow(row1);
		table.setRow(row2);
	}
	
	@Test
	public void testGetRow() throws Exception {
		assertEquals(row1, table.getRow(100L));
		assertEquals(row2, table.getRow(120L));
	}
	
	@Test
	public void testGetRow_IfHasNoRow() throws Exception {
		assertNotNull(table.getRow(100L));
		assertNull(table.getRow(101L));
	}
	
	@Test
	public void testSetRow_UpdateExisting() throws Exception {
		table.setRow(row3);
		assertEquals(row3, table.getRow(120L));
	}
	
	@Test
	public void testClear() throws Exception {
		table.clear();
		assertNull(table.getRow(100L));
		assertNull(table.getRow(120L));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(table.equals(table));
		assertFalse(table.equals(null));
		assertFalse(table.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<OrderTableRow> rows1 = new Vector<OrderTableRow>();
		rows1.add(row1);
		rows1.add(row2);
		List<OrderTableRow> rows2 = new Vector<OrderTableRow>();
		rows2.add(row3);
		Variant<List<OrderTableRow>> vRows = new Variant<List<OrderTableRow>>()
			.add(rows1)
			.add(rows2);
		Variant<EventType> vType = new Variant<EventType>(vRows)
			.add(type1)
			.add(type2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vType)
			.add(dispatcher1)
			.add(dispatcher2);
		Variant<?> iterator = vDisp;
		int foundCnt = 0;
		OrderTable x = null, found = null;
		do {
			x = new OrderTable(vDisp.get(), vType.get());
			for ( OrderTableRow row : vRows.get() ) {
				x.setRow(row);
			}
			if ( table.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(row1, found.getRow(100L));
		assertEquals(row2, found.getRow(120L));
		assertSame(type1, found.OnChanged());
		assertSame(dispatcher1, found.getEventDispatcher());
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		dispatcher1.dispatch(eq(new EventImpl(type1)));
		control.replay();
		
		table.fireChangedEvent();
		
		control.verify();
	}

}
