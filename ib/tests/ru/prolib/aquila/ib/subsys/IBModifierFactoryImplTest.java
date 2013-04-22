package ru.prolib.aquila.ib.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.getter.IBGetterFactory;
import ru.prolib.aquila.ib.getter.IBGetterFactoryImpl;
import ru.prolib.aquila.ib.subsys.IBModifierFactoryImpl;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfContract;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfTick;

/**
 * 2012-12-20<br>
 * $Id: IBModifierFactoryImplTest.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class IBModifierFactoryImplTest {
	private static IMocksControl control;
	private static SetterFactory sfactory;
	private static IBGetterFactory gfactory;
	private static IBServiceLocator locator;
	private static EditableTerminal terminal;
	private static IBModifierFactoryImpl factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		sfactory = control.createMock(SetterFactory.class);
		gfactory = control.createMock(IBGetterFactory.class);
		terminal = control.createMock(EditableTerminal.class);
		locator = control.createMock(IBServiceLocator.class);
		factory = new IBModifierFactoryImpl(locator, gfactory, sfactory);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		expect(locator.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBServiceLocator> vLoc = new Variant<IBServiceLocator>()
			.add(locator)
			.add(control.createMock(IBServiceLocator.class));
		Variant<IBGetterFactory> vGf = new Variant<IBGetterFactory>(vLoc)
			.add(gfactory)
			.add(control.createMock(IBGetterFactory.class));
		Variant<SetterFactory> vSf = new Variant<SetterFactory>(vGf)
			.add(sfactory)
			.add(control.createMock(SetterFactory.class));
		Variant<?> iterator = vSf;
		int foundCnt = 0;
		IBModifierFactoryImpl found = null, x = null;
		do {
			x = new IBModifierFactoryImpl(vLoc.get(), vGf.get(), vSf.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(gfactory, found.getGetterFactory());
		assertSame(sfactory, found.getSetterFactory());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121221, 211515)
			.append(locator)
			.append(gfactory)
			.append(sfactory)
			.toHashCode(), factory.hashCode());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOoAccount() throws Exception {
		G<Account> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.openOrderAccount()).andReturn(getter);
		expect(sfactory.orderSetAccount()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOoAccount());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOoDir() throws Exception {
		G<OrderDirection> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.openOrderDir()).andReturn(getter);
		expect(sfactory.orderSetDirection()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOoDir());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOoQty() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.openOrderQty()).andReturn(getter);
		expect(sfactory.orderSetQty()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOoQty());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOoSecurity() throws Exception {
		G<SecurityDescriptor> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.openOrderSecDescr()).andReturn(getter);
		expect(sfactory.orderSetSecurityDescriptor()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOoSecurityDescr());
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOoStatus() throws Exception {
		G<OrderStatus> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.openOrderStatus()).andReturn(getter);
		expect(sfactory.orderSetStatus()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOoStatus());
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOoType() throws Exception {
		G<OrderType> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.openOrderType()).andReturn(getter);
		expect(sfactory.orderSetType()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOoType());
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOsQtyRest() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.orderStatusRemaining()).andReturn(getter);
		expect(sfactory.orderSetQtyRest()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOsQtyRest());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOsStatus() throws Exception {
		G<OrderStatus> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.orderStatusStatus()).andReturn(getter);
		expect(sfactory.orderSetStatus()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOsStatus());
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPortCash() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(gfactory.portCash()).andReturn(getter);
		expect(sfactory.portfolioSetCash()).andReturn(setter);
		S<EditablePortfolio> expected =
				new MStd<EditablePortfolio>(getter, setter);
		control.replay();
		assertEquals(expected, factory.portCash());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPortBalance() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePortfolio> setter = control.createMock(S.class);
		expect(gfactory.portBalance()).andReturn(getter);
		expect(sfactory.portfolioSetBalance()).andReturn(setter);
		S<EditablePortfolio> expected =
				new MStd<EditablePortfolio>(getter, setter);
		control.replay();
		assertEquals(expected, factory.portBalance());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPosCurrQty() throws Exception {
		G<Long> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.posCurrValue()).andReturn(getter);
		expect(sfactory.positionSetCurrQty()).andReturn(setter);
		S<EditablePosition> expected =
				new MStd<EditablePosition>(getter, setter);
		control.replay();
		assertEquals(expected, factory.posCurrQty());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPosMarketValue() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.posMarketValue()).andReturn(getter);
		expect(sfactory.positionSetMarketValue()).andReturn(setter);
		S<EditablePosition> expected =
				new MStd<EditablePosition>(getter, setter);
		control.replay();
		assertEquals(expected, factory.posMarketValue());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPosBookValue() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.posBalanceCost()).andReturn(getter);
		expect(sfactory.positionSetBookValue()).andReturn(setter);
		S<EditablePosition> expected =
				new MStd<EditablePosition>(getter, setter);
		control.replay();
		assertEquals(expected, factory.posBookValue());
		control.verify();
	}
	
	@Test
	public void testSecurityContract() throws Exception {
		S<EditableSecurity> expected = new IBSecurityModifierOfContract();
		control.replay();
		assertEquals(expected, factory.securityContract());
		control.verify();
	}

	@Test
	public void testSecurityTick() throws Exception {
		S<EditableSecurity> expected = new IBSecurityModifierOfTick();
		control.replay();
		assertEquals(expected, factory.securityTick());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOsExecutedVolume() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.orderStatusExecutedVolume()).andReturn(getter);
		expect(sfactory.orderSetExecutedVolume()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOsExecutedVolume());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOrderOsAvgExevutedPrice() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditableOrder> setter = control.createMock(S.class);
		expect(gfactory.orderStatusAvgExecutedPrice()).andReturn(getter);
		expect(sfactory.orderSetAvgExecutedPrice()).andReturn(setter);
		S<EditableOrder> expected = new MStd<EditableOrder>(getter, setter);
		control.replay();
		assertEquals(expected, factory.orderOsAvgExecutedPrice());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPosVarMargin() throws Exception {
		G<Double> getter = control.createMock(G.class);
		S<EditablePosition> setter = control.createMock(S.class);
		expect(gfactory.posPL()).andReturn(getter);
		expect(sfactory.positionSetVarMargin()).andReturn(setter);
		S<EditablePosition> expected =
				new MStd<EditablePosition>(getter, setter);
		control.replay();
		assertEquals(expected, factory.posVarMargin());
		control.verify();
	}
	
	@Test
	public void testConstruct1() throws Exception {
		Object expected = new IBModifierFactoryImpl(locator,
				new IBGetterFactoryImpl(locator), new SetterFactoryImpl());
		assertEquals(expected, new IBModifierFactoryImpl(locator));
	}

}
