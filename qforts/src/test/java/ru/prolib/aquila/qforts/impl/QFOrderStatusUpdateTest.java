package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Variant;

public class QFOrderStatusUpdateTest {
	private QFOrderStatusUpdate update;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		update = new QFOrderStatusUpdate();
	}
	
	@Test
	public void testSetters() {
		assertSame(update, update.setInitialStatus(OrderStatus.PENDING));
		assertSame(update, update.setFinalStatus(OrderStatus.ACTIVE));
		assertSame(update, update.setFinalizationTime(T("2017-04-14T22:32:00Z")));
		assertSame(update, update.setSystemMessage("foobar"));
		
		assertEquals(OrderStatus.PENDING, update.getInitialStatus());
		assertEquals(OrderStatus.ACTIVE, update.getFinalStatus());
		assertEquals(T("2017-04-14T22:32:00Z"), update.getFinalizationTime());
		assertEquals("foobar", update.getSystemMessage());
	}

	@Test
	public void testToString() {
		update.setInitialStatus(OrderStatus.ACTIVE)
			.setFinalStatus(OrderStatus.CANCELLED)
			.setFinalizationTime(T("1998-01-05T00:10:00Z"))
			.setSystemMessage("zulu24");
		
		assertEquals("QFOrderStatusUpdate[is=ACTIVE fs=CANCELLED "
				+ "ft=1998-01-05T00:10:00Z sm=zulu24]", update.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(update.equals(update));
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
	}
	
	@Test
	public void testEquals() {
		update.setInitialStatus(OrderStatus.ACTIVE)
			.setFinalStatus(OrderStatus.CANCELLED)
			.setFinalizationTime(T("1998-01-05T00:10:00Z"))
			.setSystemMessage("zulu24");
		
		Variant<OrderStatus> viSt = new Variant<>(OrderStatus.ACTIVE, OrderStatus.REJECTED),
			vfSt = new Variant<>(viSt, OrderStatus.CANCELLED, OrderStatus.CANCEL_SENT);
		Variant<Instant> vFinTm = new Variant<>(vfSt, T("1998-01-05T00:10:00Z"), null);
		Variant<String> vSysMsg = new Variant<>(vFinTm, "zulu24", "charlie");
		Variant<?> iterator = vSysMsg;
		int foundCnt = 0;
		QFOrderStatusUpdate x, found = null;
		do {
			x = new QFOrderStatusUpdate()
				.setInitialStatus(viSt.get())
				.setFinalStatus(vfSt.get())
				.setFinalizationTime(vFinTm.get())
				.setSystemMessage(vSysMsg.get());
			if ( update.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(OrderStatus.ACTIVE, found.getInitialStatus());
		assertEquals(OrderStatus.CANCELLED, found.getFinalStatus());
		assertEquals(T("1998-01-05T00:10:00Z"), found.getFinalizationTime());
		assertEquals("zulu24", found.getSystemMessage());
	}
	
}
