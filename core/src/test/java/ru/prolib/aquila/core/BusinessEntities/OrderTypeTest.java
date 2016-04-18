package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.*;


/**
 * 2012-10-14<br>
 * $Id: OrderTypeTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderTypeTest {
	
	static class NewType extends OrderType {

		protected NewType(String code) {
			super(code);
		}
		
	}
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("Limit", OrderType.LIMIT.toString());
		assertEquals("Market", OrderType.MARKET.toString());
	}
	
	@Test
	public void testByCode() throws Exception {
		assertSame(OrderType.LIMIT, OrderType.byCode("Limit"));
		assertSame(OrderType.MARKET, OrderType.byCode("Market"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testByCode_ThrowsIfNotExists() {
		OrderType.byCode("zulu24");
	}
	
	@Test
	public void testRegisterType1_Str() throws Exception {
		OrderType type3 = OrderType.registerType("Type3");
		OrderType type4 = OrderType.registerType("Type4");
		
		assertNotNull(type3);
		assertNotNull(type4);
		
		assertSame(type3, OrderType.byCode("Type3"));
		assertSame(type4, OrderType.byCode("Type4"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterType1_Str_ThrowsIfAlreadyExists() {
		OrderType.registerType("MyStringType");
		OrderType.registerType("MyStringType");
	}
	
	@Test
	public void testRegisterType1_Obj() throws Exception {
		NewType type5 = new NewType("Type5");
		NewType type6 = new NewType("Type6");
		
		assertSame(type5, OrderType.registerType(type5));
		assertSame(type6, OrderType.registerType(type6));
		
		assertSame(type5, OrderType.byCode("Type5"));
		assertSame(type6, OrderType.byCode("Type6"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterType1_Obj_ThrowsIfAlreadyExists() {
		OrderType.registerType(new NewType("Type7"));
		OrderType.registerType(new NewType("Type7"));
	}

}
