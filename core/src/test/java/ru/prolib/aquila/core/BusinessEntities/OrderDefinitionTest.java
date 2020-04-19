package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class OrderDefinitionTest {
	static final Account account1 = new Account("account1"), account2 = new Account("account2");
	static final Symbol symbol1 = new Symbol("foo"), symbol2 = new Symbol("bar");
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	OrderDefinition service;

	@Before
	public void setUp() throws Exception {
		service = new OrderDefinition(
				account1,
				symbol1,
				OrderType.LMT,
				OrderAction.BUY,
				of(100L),
				of("215.26"),
				"Test order",
				30000L,
				T("2020-04-10T02:17:00Z")
			);
	}
	
	@Test
	public void testCtor9() {
		assertEquals(account1, service.getAccount());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(OrderType.LMT, service.getType());
		assertEquals(OrderAction.BUY, service.getAction());
		assertEquals(of(100L), service.getQty());
		assertEquals(of("215.26"), service.getPrice());
		assertEquals("Test order", service.getComment());
		assertEquals(30000L, service.getMaxExecutionTime());
		assertEquals(T("2020-04-10T02:17:00Z"), service.getPlacementTime());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("OrderDefinition[")
				.append("account=account1,")
				.append("symbol=foo,")
				.append("type=LMT,")
				.append("action=BUY,")
				.append("qty=100,")
				.append("price=215.26,")
				.append("comment=Test order,")
				.append("maxExecTime=30000,")
				.append("placementTime=2020-04-10T02:17:00Z")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());	
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(917623221, 715)
				.append(account1)
				.append(symbol1)
				.append(OrderType.LMT)
				.append(OrderAction.BUY)
				.append(of(100L))
				.append(of("215.26"))
				.append("Test order")
				.append(30000L)
				.append(T("2020-04-10T02:17:00Z"))
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Account> vAcc = new Variant<>(account1, account2);
		Variant<Symbol> vSym = new Variant<>(vAcc, symbol1, symbol2);
		Variant<OrderType> vTyp = new Variant<>(vSym, OrderType.LMT, OrderType.MKT);
		Variant<OrderAction> vAct = new Variant<>(vTyp, OrderAction.BUY, OrderAction.COVER);
		Variant<CDecimal> vQty = new Variant<>(vAct, of(100L), of(250L));
		Variant<CDecimal> vPr = new Variant<>(vQty, of("215.26"), of("115.982"));
		Variant<String> vCom = new Variant<>(vPr, "Test order", "Best guest");
		Variant<Long> vETM = new Variant<>(vCom, 30000L, 15000L);
		Variant<Instant> vPTM = new Variant<>(vETM, T("2020-04-10T02:17:00Z"), T("2020-01-01T00:00:00Z"));
		Variant<?> iterator = vPTM;
		int found_cnt = 0;
		OrderDefinition x, found = null;
		do {
			x = new OrderDefinition(vAcc.get(), vSym.get(), vTyp.get(), vAct.get(),
					vQty.get(), vPr.get(), vCom.get(), vETM.get(), vPTM.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(account1, found.getAccount());
		assertEquals(symbol1, found.getSymbol());
		assertEquals(OrderType.LMT, found.getType());
		assertEquals(OrderAction.BUY, found.getAction());
		assertEquals(of(100L), found.getQty());
		assertEquals(of("215.26"), found.getPrice());
		assertEquals("Test order", found.getComment());
		assertEquals(30000L, found.getMaxExecutionTime());
		assertEquals(T("2020-04-10T02:17:00Z"), found.getPlacementTime());
	}

}
