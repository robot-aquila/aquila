package ru.prolib.aquila.core.BusinessEntities.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.OrderRowHandler;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolver;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-16<br>
 * $Id: OrderRowHandlerTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderRowHandlerTest {
	private IMocksControl control;
	private OrderResolver resolver;
	private EditableOrder order;
	private S<EditableOrder> modifier;
	private Row row;
	private OrderRowHandler handler;
	private FirePanicEvent firePanic;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(FirePanicEvent.class);
		resolver = control.createMock(OrderResolver.class);
		order = control.createMock(EditableOrder.class);
		modifier = control.createMock(S.class);
		row = control.createMock(Row.class);
		handler = new OrderRowHandler(firePanic, resolver, modifier);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, handler.getFirePanicEvent());
		assertSame(resolver, handler.getOrderResolver());
		assertSame(modifier, handler.getOrderModifier());
	}
	
	@Test
	public void testHandle_PanicIfOrderIdIsNull() throws Exception {
		expect(row.get("ORD_ID")).andStubReturn(null);
		expect(row.get("ORD_TRANSID")).andStubReturn(123L);
		firePanic.firePanicEvent(eq(1),
				eq("Cannot handle order: orderId is NULL"));
		control.replay();
		
		handler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle_IfTransIdIsNull() throws Exception {
		expect(row.get("ORD_ID")).andStubReturn(456L);
		expect(row.get("ORD_TRANSID")).andStubReturn(null);
		expect(resolver.resolveOrder(456L, null)).andReturn(order);
		modifier.set(order, row);
		control.replay();
		
		handler.handle(row);
		
		control.verify();
	}
	
	@Test
	public void testHandle() throws Exception {
		expect(row.get("ORD_ID")).andStubReturn(456L);
		expect(row.get("ORD_TRANSID")).andStubReturn(123L);
		expect(resolver.resolveOrder(456L, 123L)).andReturn(order);
		modifier.set(order, row);
		control.replay();
		
		handler.handle(row);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(control.createMock(FirePanicEvent.class));
		Variant<OrderResolver> vRes = new Variant<OrderResolver>(vFire)
			.add(resolver)
			.add(control.createMock(OrderResolver.class));
		Variant<S<EditableOrder>> vMod = new Variant<S<EditableOrder>>(vRes)
			.add(modifier)
			.add(control.createMock(S.class));
		Variant<?> iterator = vMod;
		int foundCnt = 0;
		OrderRowHandler found = null, x = null;
		do {
			x = new OrderRowHandler(vFire.get(), vRes.get(), vMod.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertSame(resolver, found.getOrderResolver());
		assertSame(modifier, found.getOrderModifier());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 144445)
			.append(firePanic)
			.append(resolver)
			.append(modifier)
			.toHashCode();
		assertEquals(hashCode, handler.hashCode());
	}

}
