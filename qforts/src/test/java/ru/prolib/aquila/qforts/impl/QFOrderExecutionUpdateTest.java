package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class QFOrderExecutionUpdateTest {
	private static final String RUB = "RUB";
	private static Symbol symbol1, symbol2;
	private QFOrderExecutionUpdate update;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		update = new QFOrderExecutionUpdate();
	}

	@Test
	public void testGetChangeCurrentVolume_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialCurrentVolume(CDecimalBD.of(100L)));
		assertSame(update, update.setFinalCurrentVolume(CDecimalBD.of(250L)));
		
		assertEquals(CDecimalBD.of(150L), update.getChangeCurrentVolume());
	}
	
	@Test
	public void testGetChangeCurrentVolume_ByChangeVolume() {
		assertSame(update, update.setChangeCurrentVolume(CDecimalBD.of(25L)));
		
		assertEquals(CDecimalBD.of(25L), update.getChangeCurrentVolume());
	}
	
	@Test
	public void testGetChangeExecutedValue_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialExecutedValue(CDecimalBD.of("215.01", RUB)));
		assertSame(update, update.setFinalExecutedValue(CDecimalBD.of("415.09", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("200.08"), update.getChangeExecutedValue());
	}
	
	@Test
	public void testGetChangeExecutedValue_ByChangeValue() {
		assertSame(update, update.setChangeExecutedValue(CDecimalBD.of("65.13", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("65.13"), update.getChangeExecutedValue());
	}
	
	@Test
	public void testGetExecutionAction() {
		assertSame(update, update.setExecutionAction(OrderAction.SELL));
		
		assertEquals(OrderAction.SELL, update.getExecutionAction());
	}
	
	@Test
	public void testGetExecutionOrderID() {
		assertSame(update, update.setExecutionOrderID(24396L));
		
		assertEquals(24396L, update.getExecutionOrderID());
	}
	
	@Test
	public void testGetExecutionPrice() {
		assertSame(update, update.setExecutionPrice(CDecimalBD.of("45.42")));
		
		assertEquals(CDecimalBD.of("45.42"), update.getExecutionPrice());
	}
	
	@Test
	public void testGetExecutionSymbol() {
		assertSame(update, update.setExecutionSymbol(symbol1));
		
		assertEquals(symbol1, update.getExecutionSymbol());
	}
	
	@Test
	public void testGetExecutionTime() {
		assertSame(update, update.setExecutionTime(T("1991-08-01T00:10:20Z")));
		
		assertEquals(T("1991-08-01T00:10:20Z"), update.getExecutionTime());
	}
	
	@Test
	public void testGetExecutionValue() {
		assertSame(update, update.setExecutionValue(CDecimalBD.of("14.00", RUB)));
		
		assertEquals(CDecimalBD.of("14.00", RUB), update.getExecutionValue());
	}
	
	@Test
	public void testGetExecutionVolume() {
		assertSame(update, update.setExecutionVolume(CDecimalBD.of(94L)));
		
		assertEquals(CDecimalBD.of(94L), update.getExecutionVolume());
	}
	
	@Test
	public void testGetFinalCurrentVolume_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialCurrentVolume(CDecimalBD.of(150L)));
		assertSame(update, update.setChangeCurrentVolume(CDecimalBD.of(-15L)));
		
		assertEquals(CDecimalBD.of(135L), update.getFinalCurrentVolume());
	}
	
	@Test
	public void testGetFinalCurrentVolumeByFinalValue() {
		assertSame(update, update.setFinalCurrentVolume(CDecimalBD.of(132L)));
		
		assertEquals(CDecimalBD.of(132L), update.getFinalCurrentVolume());
	}
	
	@Test
	public void testGetFinalExecutedValue_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialExecutedValue(CDecimalBD.of("100.05", RUB)));
		assertSame(update, update.setChangeExecutedValue(CDecimalBD.of("0.15", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("100.20"), update.getFinalExecutedValue());
	}
	
	@Test
	public void testGetFinalExecutedValue_ByFinalValue() {
		assertSame(update, update.setFinalExecutedValue(CDecimalBD.of("245.00", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("245.00"), update.getFinalExecutedValue());
	}
	
	@Test
	public void testGetFinalizationTime() {
		assertSame(update, update.setFinalizationTime(T("2040-01-01T00:01:02Z")));
		
		assertEquals(T("2040-01-01T00:01:02Z"), update.getFinalizationTime());
	}
	
	@Test
	public void testGetFinalStatus() {
		assertSame(update, update.setFinalStatus(OrderStatus.FILLED));
		
		assertEquals(OrderStatus.FILLED, update.getFinalStatus());
	}
	
	@Test
	public void testGetInitialCurrentVolume_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeCurrentVolume(CDecimalBD.of(-5L)));
		assertSame(update, update.setFinalCurrentVolume(CDecimalBD.of(15L)));
		
		assertEquals(CDecimalBD.of(20L), update.getInitialCurrentVolume());
	}
	
	@Test
	public void testGetInitialCurrentVolume_ByInitialValue() {
		assertSame(update, update.setInitialCurrentVolume(CDecimalBD.of(420L)));
		
		assertEquals(CDecimalBD.of(420L), update.getInitialCurrentVolume());
	}
	
	@Test
	public void testGetInitialExecutedValue_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeExecutedValue(CDecimalBD.of("-256.01", RUB)));
		assertSame(update, update.setFinalExecutedValue(CDecimalBD.of("26.12", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("282.13"), update.getInitialExecutedValue());
	}
	
	@Test
	public void testGetInitialExecutedValue_ByInitialValue() {
		assertSame(update, update.setInitialExecutedValue(CDecimalBD.of("114.05", RUB)));
		
		assertEquals(CDecimalBD.ofRUB5("114.05"), update.getInitialExecutedValue());
	}
	
	@Test
	public void testGetInitialStatus() {
		assertSame(update, update.setInitialStatus(OrderStatus.ACTIVE));
		
		assertEquals(OrderStatus.ACTIVE, update.getInitialStatus());
	}
	
	@Test
	public void testToString() {
		update.setExecutionAction(OrderAction.BUY)
			.setExecutionOrderID(110055L)
			.setExecutionPrice(CDecimalBD.of("100.24"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2014-01-26T00:00:00Z"))
			.setExecutionValue(CDecimalBD.of("48.26", RUB))
			.setExecutionVolume(CDecimalBD.of(10L))
			.setFinalCurrentVolume(CDecimalBD.of(20L))
			.setFinalExecutedValue(CDecimalBD.of("11.02", RUB))
			.setFinalizationTime(T("1927-10-03T00:00:00Z"))
			.setFinalStatus(OrderStatus.FILLED)
			.setInitialCurrentVolume(CDecimalBD.of(5L))
			.setInitialExecutedValue(CDecimalBD.of("426.00", RUB))
			.setInitialStatus(OrderStatus.ACTIVE);
		
		String expected = "QFOrderExecutionUpdate["
			+ "exOrd=110055 "
			+ "exTm=2014-01-26T00:00:00Z "
			+ "exAct=BUY "
			+ "exSym=AAPL "
			+ "exPr=100.24 "
			+ "exVol=10 "
			+ "exVal=48.26 RUB "
			+ "currentVolume[i=5 c=null f=20] "
			+ "executedValue[i=426.00000 RUB c=null f=11.02000 RUB] "
			+ "finTime=1927-10-03T00:00:00Z "
			+ "status[i=ACTIVE f=FILLED]]";
		assertEquals(expected, update.toString());
	}
	
	@Test
	public void testEquals_SpecialCase() {
		assertTrue(update.equals(update));
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
	}
	
	@Test
	public void testEquals() {
		update.setExecutionAction(OrderAction.BUY)
			.setExecutionOrderID(110055L)
			.setExecutionPrice(CDecimalBD.of("100.24"))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2014-01-26T00:00:00Z"))
			.setExecutionValue(CDecimalBD.of("48.26", RUB))
			.setExecutionVolume(CDecimalBD.of(10L))
			.setChangeCurrentVolume(CDecimalBD.of(20L))
			.setChangeExecutedValue(CDecimalBD.of("11.02", RUB))
			.setFinalizationTime(T("1927-10-03T00:00:00Z"))
			.setFinalStatus(OrderStatus.FILLED)
			.setInitialStatus(OrderStatus.ACTIVE);
		
		Variant<OrderAction> vExAct = new Variant<>(OrderAction.BUY, OrderAction.SELL);
		Variant<Long> vExOID = new Variant<>(vExAct, 110055L, 876L);
		Variant<CDecimal> vExPr = new Variant<>(vExOID, CDecimalBD.of("100.24"), CDecimalBD.of("5.00"));
		Variant<Symbol> vExSym = new Variant<>(vExPr, symbol1, symbol2);
		Variant<Instant> vExTm = new Variant<>(vExSym, T("2014-01-26T00:00:00Z"), T("1997-05-05T00:00:00Z"));
		Variant<CDecimal> vExVal = new Variant<>(vExTm, CDecimalBD.of("48.26", RUB), CDecimalBD.of("1.50", RUB));
		Variant<CDecimal> vExVol = new Variant<>(vExVal, CDecimalBD.of(20L), CDecimalBD.of(10L));
		Variant<CDecimal> vcCurVol = new Variant<>(vExVol, CDecimalBD.of(20L), CDecimalBD.of(15L));
		Variant<CDecimal> vcExVal = new Variant<>(vcCurVol, CDecimalBD.of("11.02", RUB), CDecimalBD.of("11.03", RUB));
		Variant<Instant> vFinTm = new Variant<>(vcExVal, T("1927-10-03T00:00:00Z"), T("2006-01-01T00:00:00Z"));
		Variant<OrderStatus> vfSt = new Variant<>(vFinTm, OrderStatus.FILLED, OrderStatus.SENT);
		Variant<OrderStatus> viSt = new Variant<>(vfSt, OrderStatus.ACTIVE, OrderStatus.REJECTED);
		Variant<?> iterator = viSt;
		int foundCnt = 0;
		QFOrderExecutionUpdate x, found = null;
		do {
			x = new QFOrderExecutionUpdate()
					.setExecutionAction(vExAct.get())
					.setExecutionOrderID(vExOID.get())
					.setExecutionPrice(vExPr.get())
					.setExecutionSymbol(vExSym.get())
					.setExecutionTime(vExTm.get())
					.setExecutionValue(vExVal.get())
					.setExecutionVolume(vExVol.get())
					.setChangeCurrentVolume(vcCurVol.get())
					.setChangeExecutedValue(vcExVal.get())
					.setFinalizationTime(vFinTm.get())
					.setFinalStatus(vfSt.get())
					.setInitialStatus(viSt.get());
			if ( update.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(OrderAction.BUY, found.getExecutionAction());
		assertEquals(110055L, found.getExecutionOrderID());
		assertEquals(CDecimalBD.of("100.24"), found.getExecutionPrice());
		assertEquals(symbol1, found.getExecutionSymbol());
		assertEquals(T("2014-01-26T00:00:00Z"), found.getExecutionTime());
		assertEquals(CDecimalBD.of("48.26", RUB), found.getExecutionValue());
		assertEquals(CDecimalBD.of(10L), found.getExecutionVolume());
		assertEquals(CDecimalBD.of(20L), found.getChangeCurrentVolume());
		assertEquals(CDecimalBD.ofRUB5("11.02"), found.getChangeExecutedValue());
		assertEquals(T("1927-10-03T00:00:00Z"), found.getFinalizationTime());
		assertEquals(OrderStatus.FILLED, found.getFinalStatus());
		assertEquals(OrderStatus.ACTIVE, found.getInitialStatus());
	}
	
	@Test
	public void testGetPositionVolumeChange() {
		update.setExecutionAction(OrderAction.BUY)
			.setExecutionVolume(CDecimalBD.of(100L));
		
		assertEquals(CDecimalBD.of(100L), update.getPositionVolumeChange());
		
		update.setExecutionAction(OrderAction.SELL)
			.setExecutionVolume(CDecimalBD.of(50L));
		
		assertEquals(CDecimalBD.of(-50L), update.getPositionVolumeChange());
	}
	
}
