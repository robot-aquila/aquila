package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

public class OrderUpdateEventTest {
	static Instant time1 = T("2020-03-20T00:50:00Z"), time2 = T("1998-12-01T14:00:35Z");
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	static Pair<Integer, Object> p(int token, Object value) {
		return Pair.of(token, value);
	}
	
	@SafeVarargs
	static Map<Integer, Object> m(Pair<Integer, Object> ...pairs) {
		Map<Integer, Object> r = new HashMap<>();
		for ( Pair<Integer, Object> pair : pairs ) {
			r.put(pair.getKey(), pair.getValue());
		}
		return r;
	}
	
	IMocksControl control;
	EventType typeMock1, typeMock2;
	Order orderMock1, orderMock2;
	Map<Integer, Object> oldVals1, oldVals2, newVals1, newVals2;
	OrderUpdateEvent service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		typeMock1 = control.createMock(EventType.class);
		typeMock2 = control.createMock(EventType.class);
		orderMock1 = control.createMock(Order.class);
		orderMock2 = control.createMock(Order.class);
		oldVals1 = m(
				p(OrderField.COMMENT, "zulu"),
				p(OrderField.STATUS, OrderStatus.ACTIVE)
			);
		newVals1 = m(
				p(OrderField.COMMENT, "charlie"),
				p(OrderField.STATUS, OrderStatus.CANCELLED)
			);
		oldVals2 = m(
				p(OrderField.EXTERNAL_ID, null),
				p(OrderField.EXECUTED_VALUE, ofRUB5("215.46"))
			);
		newVals2 = m(
				p(OrderField.EXTERNAL_ID, "12345XXL"),
				p(OrderField.EXECUTED_VALUE, ofRUB5("412.07"))
			);
		service1 = new OrderUpdateEvent(typeMock1, orderMock1, time1, oldVals1, newVals1);
		service2 = new OrderUpdateEvent(typeMock2, orderMock2, time2, oldVals2, newVals2);
	}
	
	@Test
	public void testAccessors() {
		assertEquals(typeMock1, service1.getType());
		assertEquals(orderMock1, service1.getOrder());
		assertEquals(time1, service1.getTime());
		assertEquals(oldVals1, service1.getOldValues());
		assertEquals(newVals1, service1.getNewValues());
		assertEquals(new HashSet<>(Arrays.asList(
				OrderField.COMMENT,
				OrderField.STATUS)), service1.getUpdatedTokens());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service1.equals(service1));
		assertFalse(service1.equals(null));
		assertFalse(service1.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<EventType> vTY = new Variant<>(typeMock1, typeMock2);
		Variant<Order> vEO = new Variant<>(vTY, orderMock1, orderMock2);
		Variant<Instant> vTM = new Variant<>(vEO, time1, time2);
		Variant<Map<Integer, Object>> vOV = new Variant<>(vTM, oldVals1, oldVals2),
				vNV = new Variant<>(vOV, newVals1, newVals2);
		Variant<?> iterator = vNV;
		int found_cnt = 0;
		OrderUpdateEvent x, found = null;
		do {
			x = new OrderUpdateEvent(vTY.get(), vEO.get(), vTM.get(), vOV.get(), vNV.get());
			if ( service1.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(typeMock1, found.getType());
		assertEquals(orderMock1, found.getOrder());
		assertEquals(time1, found.getTime());
		assertEquals(oldVals1, found.getOldValues());
		assertEquals(newVals1, found.getNewValues());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(11776551, 61)
				.append(typeMock1)
				.append(orderMock1)
				.append(time1)
				.append(oldVals1)
				.append(newVals1)
				.build();
		
		assertEquals(expected, service1.hashCode());
	}


}
