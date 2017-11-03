package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class OrderExecutionImplTest {
	private static Symbol symbol1 = new Symbol("SBER"), symbol2 = new Symbol("GAZP");
	private IMocksControl control;
	private Terminal terminal1, terminal2;
	private OrderExecutionImpl execution;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal1 = control.createMock(Terminal.class);
		terminal1 = control.createMock(Terminal.class);
		execution = new OrderExecutionImpl(terminal1, 1024L, "foo35", symbol1,
			OrderAction.BUY, 850L, Instant.parse("2012-03-27T00:00:00Z"),
			CDecimalBD.of("36.19"),
			CDecimalBD.of("10"),
			CDecimalBD.of("361.90", "RUB"));
	}
	
	@Test
	public void testCtor9() {
		assertEquals(terminal1, execution.getTerminal());
		assertEquals(1024L, execution.getID());
		assertEquals("foo35", execution.getExternalID());
		assertEquals(symbol1, execution.getSymbol());
		assertEquals(OrderAction.BUY, execution.getAction());
		assertEquals(850L, execution.getOrderID());
		assertEquals(Instant.parse("2012-03-27T00:00:00Z"), execution.getTime());
		assertEquals(CDecimalBD.of("36.19"), execution.getPricePerUnit());
		assertEquals(CDecimalBD.of("10"), execution.getVolume());
		assertEquals(CDecimalBD.of("361.90", "RUB"), execution.getValue());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(execution.equals(execution));
		assertFalse(execution.equals(null));
		assertFalse(execution.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Terminal> vTerm = new Variant<Terminal>(terminal1, terminal2);
		Variant<Long> vID = new Variant<Long>(vTerm, 1024L, 1926L);
		Variant<String> vExtID = new Variant<String>(vID, "foo35", null);
		Variant<Symbol> vSymb = new Variant<Symbol>(vExtID, symbol1, symbol2);
		Variant<OrderAction> vAct = new Variant<OrderAction>(vSymb,
				OrderAction.BUY, OrderAction.SELL);
		Variant<Long> vOrdID = new Variant<Long>(vAct, 850L, 420L);
		Variant<Instant> vTime = new Variant<Instant>(vOrdID,
				Instant.parse("2012-03-27T00:00:00Z"), Instant.now());
		Variant<CDecimal> vPrice = new Variant<CDecimal>(vTime)
				.add(CDecimalBD.of("36.19"))
				.add(CDecimalBD.of("45.24"));
		Variant<CDecimal> vVol = new Variant<CDecimal>(vPrice)
				.add(CDecimalBD.of("10"))
				.add(CDecimalBD.of("100"));
		Variant<CDecimal> vVal = new Variant<CDecimal>(vVol)
				.add(CDecimalBD.of("361.90", "RUB"))
				.add(CDecimalBD.of("440.00", "USD"));
		Variant<?> iterator = vVal;
		int foundCnt = 0;
		OrderExecutionImpl found = null, x = null;
		do {
			x = new OrderExecutionImpl(vTerm.get(), vID.get(), vExtID.get(),
					vSymb.get(), vAct.get(), vOrdID.get(), vTime.get(),
					vPrice.get(), vVol.get(), vVal.get());
			if ( execution.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(terminal1, found.getTerminal());
		assertEquals(1024L, found.getID());
		assertEquals("foo35", found.getExternalID());
		assertEquals(symbol1, found.getSymbol());
		assertEquals(OrderAction.BUY, found.getAction());
		assertEquals(850L, found.getOrderID());
		assertEquals(Instant.parse("2012-03-27T00:00:00Z"), found.getTime());
		assertEquals(CDecimalBD.of("36.19"), found.getPricePerUnit());
		assertEquals(CDecimalBD.of("10"), found.getVolume());
		assertEquals(CDecimalBD.of("361.90", "RUB"), found.getValue());
	}

}
