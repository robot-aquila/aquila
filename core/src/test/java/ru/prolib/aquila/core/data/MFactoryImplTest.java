package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.SetterFactory;
import ru.prolib.aquila.core.utils.*;

/**
 * 2012-09-29<br>
 * $Id: MFactoryImplTest.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class MFactoryImplTest {
	private static IMocksControl control;
	private static GetterFactory gfactory;
	private static SetterFactory sfactory;
	private static MFactoryImpl factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gfactory = control.createMock(GetterFactory.class);
		sfactory = control.createMock(SetterFactory.class);
		factory = new MFactoryImpl(gfactory, sfactory);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<GetterFactory> vG = new Variant<GetterFactory>()
			.add(null)
			.add(gfactory)
			.add(control.createMock(GetterFactory.class));
		Variant<SetterFactory> vS = new Variant<SetterFactory>(vG)
			.add(sfactory)
			.add(control.createMock(SetterFactory.class))
			.add(null);
		int foundCnt = 0;
		MFactoryImpl found = null;
		do {
			MFactoryImpl actual = new MFactoryImpl(vG.get(), vS.get());
			if ( factory.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vS.next() );
		assertEquals(1, foundCnt);
		assertSame(gfactory, found.getGetterFactory());
		assertSame(sfactory, found.getSetterFactory());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(this));
		assertFalse(factory.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, /*00*/3619)
			.append(gfactory)
			.append(sfactory)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gfactory, factory.getGetterFactory());
		assertSame(sfactory, factory.getSetterFactory());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdAccount1() throws Exception {
		G<Account> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowAccount("account")).andReturn(getter);
		expect(sfactory.orderSetAccount()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdAccount("account"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdAccount2() throws Exception {
		G<Account> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowAccount("CODE", "SUBCODE")).andReturn(getter);
		expect(sfactory.orderSetAccount()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdAccount("CODE", "SUBCODE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdDir() throws Exception {
		G<Direction> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowOrderDir("dir", "BUY")).andReturn(getter);
		expect(sfactory.orderSetDirection()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdDir("dir", "BUY"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdId() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowLong("ID")).andReturn(getter);
		expect(sfactory.orderSetId()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdId("ID"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("price")).andReturn(getter);
		expect(sfactory.orderSetPrice()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdPrice("price"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdQty() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowLong("QTY")).andReturn(getter);
		expect(sfactory.orderSetQty()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdQty("QTY"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdQtyRest() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowLong("QTY_REST")).andReturn(getter);
		expect(sfactory.orderSetQtyRest()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdQtyRest("QTY_REST"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdSecDescr() throws Exception {
		G<SecurityDescriptor> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(sfactory.orderSetSecurityDescriptor()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdSecDescr(getter));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdStatus() throws Exception {
		Map<?, OrderStatus> map = new HashMap<String, OrderStatus>();
		G<Object> objGetter = control.createMock(G.class);
		G<OrderStatus> getter = new GMapTR<OrderStatus>(objGetter, map);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowObject("status")).andReturn(objGetter);
		expect(sfactory.orderSetStatus()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdStatus("status", map));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdType2TR() throws Exception {
		Map<?, OrderType> map = new HashMap<String, OrderType>();
		G<Object> objGetter = control.createMock(G.class);
		G<OrderType> getter = new GMapTR<OrderType>(objGetter, map); 
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowObject("type")).andReturn(objGetter);
		expect(sfactory.orderSetType()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdType("type", map));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdType2VR() throws Exception {
		Map<Validator, OrderType> map = new HashMap<Validator, OrderType>();
		G<OrderType> getter = new GMapVR<OrderType>(map);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(sfactory.orderSetType()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdType(map));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowOrdType2() throws Exception {
		G<Security> gS = control.createMock(G.class);
		G<Double> gPrice = control.createMock(G.class);
		G<OrderType> getter = new GOrderType(gS, gPrice);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("PRICE")).andReturn(gPrice);
		expect(sfactory.orderSetType()).andReturn(setter);
		control.replay();
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		assertEquals(expected, factory.rowOrdType(gS, "PRICE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSecLot() throws Exception {
		G<Integer> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowInteger("LOT")).andReturn(getter);
		expect(sfactory.securitySetLotSize()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecLot("LOT"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSecMaxPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("MAX_PRICE")).andReturn(getter);
		expect(sfactory.securitySetMaxPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecMaxPrice("MAX_PRICE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSecMinPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("MIN_PRICE")).andReturn(getter);
		expect(sfactory.securitySetMinPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecMinPrice("MIN_PRICE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSecMinStepPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("MIN_STEP")).andReturn(getter);
		expect(sfactory.securitySetMinStepPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecMinStepPrice("MIN_STEP"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSetMinStepSize() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("STEP_SIZE")).andReturn(getter);
		expect(sfactory.securitySetMinStepSize()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecMinStepSize("STEP_SIZE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSecPrecision() throws Exception {
		G<Integer> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowInteger("PREC")).andReturn(getter);
		expect(sfactory.securitySetPrecision()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecPrecision("PREC"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowSecLastPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("LAST")).andReturn(getter);
		expect(sfactory.securitySetLastPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected = new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecLastPrice("LAST"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPortCash() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("CASH")).andReturn(getter);
		expect(sfactory.portfolioSetCash()).andReturn(setter);
		control.replay();
		S<EditablePortfolio> expected =
				new MStd<EditablePortfolio>(getter, setter);
		assertEquals(expected, factory.rowPortCash("CASH"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPortVarMargin() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("MGR")).andReturn(getter);
		expect(sfactory.portfolioSetVarMargin()).andReturn(setter);
		control.replay();
		S<EditablePortfolio> expected =
				new MStd<EditablePortfolio>(getter, setter);
		assertEquals(expected, factory.rowPortVarMargin("MGR"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPortBalance() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("BAL")).andReturn(getter);
		expect(sfactory.portfolioSetBalance()).andReturn(setter);
		control.replay();
		S<EditablePortfolio> expected =
				new MStd<EditablePortfolio>(getter, setter);
		assertEquals(expected, factory.rowPortBalance("BAL"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPosVarMargin() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("MGR")).andReturn(getter);
		expect(sfactory.positionSetVarMargin()).andReturn(setter);
		control.replay();
		S<EditablePosition> expected = new MStd<EditablePosition>(getter, setter);
		assertEquals(expected, factory.rowPosVarMargin("MGR"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPosOpenValue() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("VAL")).andReturn(getter);
		expect(sfactory.positionSetOpenQty()).andReturn(setter);
		control.replay();
		S<EditablePosition> expected = new MStd<EditablePosition>(getter, setter);
		assertEquals(expected, factory.rowPosOpenValue("VAL"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPosLockValue() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("VAL")).andReturn(getter);
		expect(sfactory.positionSetLockQty()).andReturn(setter);
		control.replay();
		S<EditablePosition> expected = new MStd<EditablePosition>(getter, setter);
		assertEquals(expected, factory.rowPosLockValue("VAL"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowPosCurrValue() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("VAL")).andReturn(getter);
		expect(sfactory.positionSetCurrQty()).andReturn(setter);
		control.replay();
		S<EditablePosition> expected = new MStd<EditablePosition>(getter, setter);
		assertEquals(expected, factory.rowPosCurrValue("VAL"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdDir() throws Exception {
		G<Direction> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowOrderDir("dir", "BUY")).andReturn(getter);
		expect(sfactory.tradeSetDirection()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdDir("dir", "BUY"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdId() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowLong("ID")).andReturn(getter);
		expect(sfactory.tradeSetId()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdId("ID"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("PRICE")).andReturn(getter);
		expect(sfactory.tradeSetPrice()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdPrice("PRICE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdQty() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowLong("QTY")).andReturn(getter);
		expect(sfactory.tradeSetQty()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdQty("QTY"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdSecDescr() throws Exception {
		G<SecurityDescriptor> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(sfactory.tradeSetSecurityDescr()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdSecDescr(getter));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdTime4() throws Exception {
		G<Date> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowDate("DATE","TIME","DATEF","TIMEF")).andReturn(getter);
		expect(sfactory.tradeSetTime()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdTime("DATE","TIME","DATEF","TIMEF"));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdTime1() throws Exception {
		G<Date> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowDate("beta")).andReturn(getter);
		expect(sfactory.tradeSetTime()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdTime("beta"));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRowTrdVolume() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<Trade> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("VOL")).andReturn(getter);
		expect(sfactory.tradeSetVolume()).andReturn(setter);
		control.replay();
		S<Trade> expected = new MStd<Trade>(getter, setter);
		assertEquals(expected, factory.rowTrdVolume("VOL"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecAskPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("ASK")).andReturn(getter);
		expect(sfactory.securitySetAskPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecAskPrice("ASK"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecAskSize() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowLong("ASK_SIZE")).andReturn(getter);
		expect(sfactory.securitySetAskSize()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecAskSize("ASK_SIZE"));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void rowSecBidPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("BID")).andReturn(getter);
		expect(sfactory.securitySetBidPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecBidPrice("BID"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecBidSize() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowLong("BID_SIZE")).andReturn(getter);
		expect(sfactory.securitySetBidSize()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecBidSize("BID_SIZE"));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void rowSecClosePrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("CLOSE")).andReturn(getter);
		expect(sfactory.securitySetClosePrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecClosePrice("CLOSE"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecDisplayName() throws Exception {
		G<String> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowString("#")).andReturn(getter);
		expect(sfactory.securitySetDisplayName()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecDisplayName("#"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecHighPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("HIGH")).andReturn(getter);
		expect(sfactory.securitySetHighPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecHighPrice("HIGH"));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void rowSecLowPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("LOW")).andReturn(getter);
		expect(sfactory.securitySetLowPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecLowPrice("LOW"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecOpenPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(gfactory.rowDouble("OPEN")).andReturn(getter);
		expect(sfactory.securitySetOpenPrice()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecOpenPrice("OPEN"));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void rowSecStatus() throws Exception {
		G<SecurityStatus> getter = control.createMock(G.class);
		S<EditableSecurity> setter = control.createMock(S.class);
		expect(sfactory.securitySetStatus()).andReturn(setter);
		control.replay();
		S<EditableSecurity> expected=new MStd<EditableSecurity>(getter, setter);
		assertEquals(expected, factory.rowSecStatus(getter));
		control.verify();
	}

}
