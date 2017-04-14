package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class QFOrderExecutionUpdateTest {
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
		assertSame(update, update.setInitialCurrentVolume(100L));
		assertSame(update, update.setFinalCurrentVolume(250L));
		
		assertEquals(150L, update.getChangeCurrentVolume());
	}
	
	@Test
	public void testGetChangeCurrentVolume_ByChangeVolume() {
		assertSame(update, update.setChangeCurrentVolume(25L));
		
		assertEquals(25L, update.getChangeCurrentVolume());
	}
	
	@Test
	public void testGetChangeExecutedValue_ByInitialAndFinalValues() {
		assertSame(update, update.setInitialExecutedValue(FMoney.ofRUB2(215.01)));
		assertSame(update, update.setFinalExecutedValue(FMoney.ofRUB2(415.09)));
		
		assertEquals(FMoney.ofRUB2(200.08), update.getChangeExecutedValue());
	}
	
	@Test
	public void testGetChangeExecutedValue_ByChangeValue() {
		assertSame(update, update.setChangeExecutedValue(FMoney.ofRUB2(65.13)));
		
		assertEquals(FMoney.ofRUB2(65.13), update.getChangeExecutedValue());
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
		assertSame(update, update.setExecutionPrice(FDecimal.of2(45.42)));
		
		assertEquals(FDecimal.of2(45.42), update.getExecutionPrice());
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
		assertSame(update, update.setExecutionValue(FMoney.ofRUB2(14.0)));
		
		assertEquals(FMoney.ofRUB2(14.0), update.getExecutionValue());
	}
	
	@Test
	public void testGetExecutionVolume() {
		assertSame(update, update.setExecutionVolume(94L));
		
		assertEquals(94L, update.getExecutionVolume());
	}
	
	@Test
	public void testGetFinalCurrentVolume_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialCurrentVolume(150L));
		assertSame(update, update.setChangeCurrentVolume(-15L));
		
		assertEquals(135L, update.getFinalCurrentVolume());
	}
	
	@Test
	public void testGetFinalCurrentVolumeByFinalValue() {
		assertSame(update, update.setFinalCurrentVolume(132L));
		
		assertEquals(132L, update.getFinalCurrentVolume());
	}
	
	@Test
	public void testGetFinalExecutedValue_ByInitialAndChangeValues() {
		assertSame(update, update.setInitialExecutedValue(FMoney.ofRUB2(100.05)));
		assertSame(update, update.setChangeExecutedValue(FMoney.ofRUB2(0.15)));
		
		assertEquals(FMoney.ofRUB2(100.2), update.getFinalExecutedValue());
	}
	
	@Test
	public void testGetFinalExecutedValue_ByFinalValue() {
		assertSame(update, update.setFinalExecutedValue(FMoney.ofRUB2(245.0)));
		
		assertEquals(FMoney.ofRUB2(245.0), update.getFinalExecutedValue());
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
		assertSame(update, update.setChangeCurrentVolume(-5L));
		assertSame(update, update.setFinalCurrentVolume(15L));
		
		assertEquals(20L, update.getInitialCurrentVolume());
	}
	
	@Test
	public void testGetInitialCurrentVolume_ByInitialValue() {
		assertSame(update, update.setInitialCurrentVolume(420L));
		
		assertEquals(420L, update.getInitialCurrentVolume());
	}
	
	@Test
	public void testGetInitialExecutedValue_ByChangeAndFinalValues() {
		assertSame(update, update.setChangeExecutedValue(FMoney.ofRUB2(-256.01)));
		assertSame(update, update.setFinalExecutedValue(FMoney.ofRUB2(26.12)));
		
		assertEquals(FMoney.ofRUB2(282.13), update.getInitialExecutedValue());
	}
	
	@Test
	public void testGetInitialExecutedValue_ByInitialValue() {
		assertSame(update, update.setInitialExecutedValue(FMoney.ofRUB2(114.05)));
		
		assertEquals(FMoney.ofRUB2(114.05), update.getInitialExecutedValue());
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
			.setExecutionPrice(FDecimal.of2(100.24))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2014-01-26T00:00:00Z"))
			.setExecutionValue(FMoney.ofRUB2(48.26))
			.setExecutionVolume(10L)
			.setFinalCurrentVolume(20L)
			.setFinalExecutedValue(FMoney.ofRUB2(11.02))
			.setFinalizationTime(T("1927-10-03T00:00:00Z"))
			.setFinalStatus(OrderStatus.FILLED)
			.setInitialCurrentVolume(5L)
			.setInitialExecutedValue(FMoney.ofRUB2(426.0))
			.setInitialStatus(OrderStatus.ACTIVE);
		
		String expected = "QFOrderExecutionUpdate["
			+ "exOrd=110055 "
			+ "exTm=2014-01-26T00:00:00Z "
			+ "exAct=BUY "
			+ "exSym=AAPL "
			+ "exPr=100.24 "
			+ "exVol=10 "
			+ "exVal=48.26 "
			+ "currentVolume[i=5 c=null f=20] "
			+ "executedValue[i=426.00 RUB c=null f=11.02 RUB] "
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
			.setExecutionPrice(FDecimal.of2(100.24))
			.setExecutionSymbol(symbol1)
			.setExecutionTime(T("2014-01-26T00:00:00Z"))
			.setExecutionValue(FMoney.ofRUB2(48.26))
			.setExecutionVolume(10L)
			.setChangeCurrentVolume(20L)
			.setChangeExecutedValue(FMoney.ofRUB2(11.02))
			.setFinalizationTime(T("1927-10-03T00:00:00Z"))
			.setFinalStatus(OrderStatus.FILLED)
			.setInitialStatus(OrderStatus.ACTIVE);
		
		Variant<OrderAction> vExAct = new Variant<>(OrderAction.BUY, OrderAction.SELL);
		Variant<Long> vExOID = new Variant<>(vExAct, 110055L, 876L);
		Variant<FDecimal> vExPr = new Variant<>(vExOID, FDecimal.of2(100.24), FDecimal.of2(5.0));
		Variant<Symbol> vExSym = new Variant<>(vExPr, symbol1, symbol2);
		Variant<Instant> vExTm = new Variant<>(vExSym, T("2014-01-26T00:00:00Z"), T("1997-05-05T00:00:00Z"));
		Variant<FMoney> vExVal = new Variant<>(vExTm, FMoney.ofRUB2(48.26), FMoney.ofRUB2(1.5));
		Variant<Long> vExVol = new Variant<>(vExVal, 20L, 10L);
		Variant<Long> vcCurVol = new Variant<>(vExVol, 20L, 15L);
		Variant<FMoney> vcExVal = new Variant<>(vcCurVol, FMoney.ofRUB2(11.02), FMoney.ofRUB2(11.03));
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
		assertEquals(FDecimal.of2(100.24), found.getExecutionPrice());
		assertEquals(symbol1, found.getExecutionSymbol());
		assertEquals(T("2014-01-26T00:00:00Z"), found.getExecutionTime());
		assertEquals(FMoney.ofRUB2(48.26), found.getExecutionValue());
		assertEquals(10L, found.getExecutionVolume());
		assertEquals(20L, found.getChangeCurrentVolume());
		assertEquals(FMoney.ofRUB2(11.02), found.getChangeExecutedValue());
		assertEquals(T("1927-10-03T00:00:00Z"), found.getFinalizationTime());
		assertEquals(OrderStatus.FILLED, found.getFinalStatus());
		assertEquals(OrderStatus.ACTIVE, found.getInitialStatus());
	}
	
}
