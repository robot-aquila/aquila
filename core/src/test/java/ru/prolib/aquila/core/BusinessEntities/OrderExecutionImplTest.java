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
			FDecimal.of2(36.19), 10L, FMoney.ofRUB2(361.9));
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
		assertEquals(FDecimal.of2(36.19), execution.getPricePerUnit());
		assertEquals(10L, execution.getVolume());
		assertEquals(FMoney.ofRUB2(361.9), execution.getValue());
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
		Variant<FDecimal> vPrice = new Variant<FDecimal>(vTime)
				.add(FDecimal.of2(36.19))
				.add(FDecimal.of3(45.24));
		Variant<Long> vVol = new Variant<Long>(vPrice, 10L, 100L);
		Variant<FMoney> vVal = new Variant<FMoney>(vVol)
				.add(FMoney.ofRUB2(361.9))
				.add(FMoney.ofUSD2(440.0));
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
		assertEquals(FDecimal.of2(36.19), found.getPricePerUnit());
		assertEquals(10L, found.getVolume());
		assertEquals(FMoney.ofRUB2(361.9), found.getValue());
	}

}
