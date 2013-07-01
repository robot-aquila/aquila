package ru.prolib.aquila.ib.assembler.cache;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.OrderStatusEntry;

public class OrderStatusEntryTest {
	private OrderStatusEntry entry;

	@Before
	public void setUp() throws Exception {
		entry = new OrderStatusEntry(25, "Submitted", 5, 24.93d);
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		setUp();
		assertEquals(new Date(), entry.getEntryTime());
	}
	
	@Test
	public void testGetId() throws Exception {
		assertEquals(new Long(25), entry.getId());
	}
	
	@Test
	public void testGetStatus() throws Exception {
		Object fix[][] = {
				// IB status, local status
				{ "PendingSubmit", OrderStatus.ACTIVE },
				{ "PendingCancel", null },
				{ "PreSubmitted", OrderStatus.ACTIVE },
				{ "Submitted", OrderStatus.ACTIVE },
				{ "Cancelled", OrderStatus.CANCELLED },
				{ "Filled", OrderStatus.FILLED },
				{ "Inactive", null },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			entry = new OrderStatusEntry(25, (String) fix[i][0], 0, 0d);
			assertEquals(msg, fix[i][1], entry.getStatus());
		}
	}
	
	@Test
	public void testGetQtyRest() throws Exception {
		assertEquals(new Long(5), entry.getQtyRest());
	}
	
	@Test
	public void testGetAvgExecutedPrice() throws Exception {
		assertEquals(24.93d, entry.getAvgExecutedPrice(), 0.01d);
	}

	@Test
	public void testGetNativeStatus() throws Exception {
		assertEquals("Submitted", entry.getNativeStatus());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vId = new Variant<Integer>()
			.add(25)
			.add(115);
		Variant<String> vStat = new Variant<String>(vId)
			.add("Submitted")
			.add("foobar");
		Variant<Integer> vRest = new Variant<Integer>(vStat)
			.add(5)
			.add(0);
		Variant<Double> vAvgPr = new Variant<Double>(vRest)
			.add(24.93d)
			.add(18.23d);
		Variant<?> iterator = vAvgPr;
		int foundCnt = 0;
		OrderStatusEntry x = null, found = null;
		do {
			x = new OrderStatusEntry(vId.get(), vStat.get(), vRest.get(),
					vAvgPr.get());
			if ( entry.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Long(25), found.getId());
		assertEquals("Submitted", found.getNativeStatus());
		assertEquals(new Long(5), found.getQtyRest());
		assertEquals(24.93d, found.getAvgExecutedPrice(), 0.01d);
	}
	
}
