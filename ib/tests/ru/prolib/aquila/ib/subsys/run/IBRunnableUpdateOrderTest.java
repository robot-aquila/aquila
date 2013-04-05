package ru.prolib.aquila.ib.subsys.run;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventOrder;
import ru.prolib.aquila.ib.subsys.run.IBRunnableUpdateOrder;

/**
 * 2013-01-07<br>
 * $Id: IBRunnableUpdateOrderTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBRunnableUpdateOrderTest {
	private static IMocksControl control;
	private static OrderResolver resolver;
	private static S<EditableOrder> modifier;
	private static IBEventOrder event;
	private static IBRunnableUpdateOrder runnable;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		resolver = control.createMock(OrderResolver.class);
		modifier = control.createMock(S.class);
		event = control.createMock(IBEventOrder.class);
		runnable = new IBRunnableUpdateOrder(resolver, modifier, event);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(resolver, runnable.getOrderResolver());
		assertSame(modifier, runnable.getOrderModifier());
		assertSame(event, runnable.getEvent());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(runnable.equals(runnable));
		assertFalse(runnable.equals(this));
		assertFalse(runnable.equals(null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<OrderResolver> vRes = new Variant<OrderResolver>()
			.add(control.createMock(OrderResolver.class))
			.add(resolver);
		Variant<S<EditableOrder>> vMod = new Variant<S<EditableOrder>>(vRes)
			.add(modifier)
			.add(control.createMock(S.class));
		Variant<IBEventOrder> vEvt = new Variant<IBEventOrder>(vMod)
			.add(event)
			.add(control.createMock(IBEventOrder.class));
		Variant<?> iterator = vEvt;
		int foundCnt = 0;
		IBRunnableUpdateOrder x = null, found = null;
		do {
			x = new IBRunnableUpdateOrder(vRes.get(), vMod.get(), vEvt.get());
			if ( runnable.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(resolver, found.getOrderResolver());
		assertSame(modifier, found.getOrderModifier());
		assertSame(event, found.getEvent());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 50345)
			.append(resolver)
			.append(modifier)
			.append(event)
			.toHashCode(), runnable.hashCode());
	}
	
	@Test
	public void testRun() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(event.getOrderId()).andReturn(156);
		expect(resolver.resolveOrder(eq(156l), eq(156l))).andReturn(order);
		modifier.set(same(order), same(event));
		control.replay();
		runnable.run();
		control.verify();
	}
	
	@Test
	public void testToString() throws Exception {
		expect(event.getOrderId()).andStubReturn(158);
		control.replay();
		assertEquals("Update order #158", runnable.toString());
	}

}
