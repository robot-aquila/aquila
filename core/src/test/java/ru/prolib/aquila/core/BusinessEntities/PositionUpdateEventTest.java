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

public class PositionUpdateEventTest {
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
	Position positionMock1, positionMock2;
	Map<Integer, Object> oldVals1, oldVals2, newVals1, newVals2;
	PositionUpdateEvent service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		typeMock1 = control.createMock(EventType.class);
		typeMock2 = control.createMock(EventType.class);
		positionMock1 = control.createMock(Position.class);
		positionMock2 = control.createMock(Position.class);
		oldVals1 = m(
				p(PositionField.USED_MARGIN, ofRUB5("126678.12")),
				p(PositionField.OPEN_PRICE, of("115.26"))
			);
		newVals1 = m(
				p(PositionField.USED_MARGIN, ofRUB5("115002.83")),
				p(PositionField.OPEN_PRICE, of("117.13"))
			);
		oldVals2 = m(p(PositionField.PROFIT_AND_LOSS, ofRUB2("1000.00")));
		newVals2 = m(p(PositionField.PROFIT_AND_LOSS, ofRUB2("2500.00")));
		service1 = new PositionUpdateEvent(typeMock1, positionMock1, time1, oldVals1, newVals1);
		service2 = new PositionUpdateEvent(typeMock2, positionMock2, time2, oldVals2, newVals2);
	}
	
	@Test
	public void testAccessors() {
		assertEquals(typeMock1, service1.getType());
		assertEquals(positionMock1, service1.getPosition());
		assertEquals(time1, service1.getTime());
		assertEquals(oldVals1, service1.getOldValues());
		assertEquals(newVals1, service1.getNewValues());
		assertEquals(new HashSet<>(Arrays.asList(
				PositionField.USED_MARGIN,
				PositionField.OPEN_PRICE)), service1.getUpdatedTokens());
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
		Variant<Position> vPO = new Variant<>(vTY, positionMock1, positionMock2);
		Variant<Instant> vTM = new Variant<>(vPO, time1, time2);
		Variant<Map<Integer, Object>> vOV = new Variant<>(vTM, oldVals1, oldVals2),
				vNV = new Variant<>(vOV, newVals1, newVals2);
		Variant<?> iterator = vNV;
		int found_cnt = 0;
		PositionUpdateEvent x, found = null;
		do {
			x = new PositionUpdateEvent(vTY.get(), vPO.get(), vTM.get(), vOV.get(), vNV.get());
			if ( service1.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(typeMock1, found.getType());
		assertEquals(positionMock1, found.getPosition());
		assertEquals(time1, found.getTime());
		assertEquals(oldVals1, found.getOldValues());
		assertEquals(newVals1, found.getNewValues());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(10087651, 107)
				.append(typeMock1)
				.append(positionMock1)
				.append(time1)
				.append(oldVals1)
				.append(newVals1)
				.build();
		
		assertEquals(expected, service1.hashCode());
	}

}
