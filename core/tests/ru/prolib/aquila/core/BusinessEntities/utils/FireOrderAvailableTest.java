package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.same;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.FireOrderAvailable;

/**
 * 2012-12-17<br>
 * $Id: FireEventOrderAvailableTest.java 339 2012-12-17 00:35:39Z whirlwind $
 */
public class FireOrderAvailableTest {
	private static IMocksControl control;
	private static EditableOrders orders;
	private static EditableOrder order;
	private static FireOrderAvailable feoa;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		orders = control.createMock(EditableOrders.class);
		order = control.createMock(EditableOrder.class);
		feoa = new FireOrderAvailable(orders);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testFireEvent() throws Exception {
		orders.fireOrderAvailableEvent(same(order));
		control.replay();
		feoa.fireEvent(order);
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(feoa.equals(feoa));
		assertFalse(feoa.equals(null));
		assertFalse(feoa.equals(this));
		assertTrue(feoa.equals(new FireOrderAvailable(orders)));
		assertFalse(feoa.equals(new FireOrderAvailable(
				control.createMock(EditableOrders.class))));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121217, 43533)
			.append(orders)
			.toHashCode();
		assertEquals(hashCode, feoa.hashCode());
	}


}
