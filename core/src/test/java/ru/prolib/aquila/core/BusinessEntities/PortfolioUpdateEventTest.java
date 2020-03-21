package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

public class PortfolioUpdateEventTest {
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
	Portfolio portfolioMock1, portfolioMock2;
	Map<Integer, Object> oldVals1, oldVals2, newVals1, newVals2;
	PortfolioUpdateEvent service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		typeMock1 = control.createMock(EventType.class);
		typeMock2 = control.createMock(EventType.class);
		portfolioMock1 = control.createMock(Portfolio.class);
		portfolioMock2 = control.createMock(Portfolio.class);
		oldVals1 = m(
				p(PortfolioField.CURRENCY, "USD"),
				p(PortfolioField.BALANCE, ofUSD5("117.12"))
			);
		newVals1 = m(
				p(PortfolioField.CURRENCY, "RUB"),
				p(PortfolioField.BALANCE, ofRUB5("28091.45"))
			);
		oldVals2 = m(
				p(PortfolioField.EQUITY, ofRUB5("24596.124"))
			);
		newVals2 = m(
				p(PortfolioField.EQUITY, ofRUB5("28901.291"))
			);
		service1 = new PortfolioUpdateEvent(typeMock1, portfolioMock1, time1, oldVals1, newVals1);
		service2 = new PortfolioUpdateEvent(typeMock2, portfolioMock2, time2, oldVals2, newVals2);
	}
	
	@Test
	public void testAccessors() {
		assertEquals(typeMock1, service1.getType());
		assertEquals(portfolioMock1, service1.getPortfolio());
		assertEquals(time1, service1.getTime());
		assertEquals(oldVals1, service1.getOldValues());
		assertEquals(newVals1, service1.getNewValues());
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
		Variant<Portfolio> vEO = new Variant<>(vTY, portfolioMock1, portfolioMock2);
		Variant<Instant> vTM = new Variant<>(vEO, time1, time2);
		Variant<Map<Integer, Object>> vOV = new Variant<>(vTM, oldVals1, oldVals2),
				vNV = new Variant<>(vOV, newVals1, newVals2);
		Variant<?> iterator = vNV;
		int found_cnt = 0;
		PortfolioUpdateEvent x, found = null;
		do {
			x = new PortfolioUpdateEvent(vTY.get(), vEO.get(), vTM.get(), vOV.get(), vNV.get());
			if ( service1.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(typeMock1, found.getType());
		assertEquals(portfolioMock1, found.getPortfolio());
		assertEquals(time1, found.getTime());
		assertEquals(oldVals1, found.getOldValues());
		assertEquals(newVals1, found.getNewValues());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(776541273, 921)
				.append(typeMock1)
				.append(portfolioMock1)
				.append(time1)
				.append(oldVals1)
				.append(newVals1)
				.build();
		
		assertEquals(expected, service1.hashCode());
	}

}
