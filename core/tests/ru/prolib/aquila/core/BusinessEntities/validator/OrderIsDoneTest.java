package ru.prolib.aquila.core.BusinessEntities.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsDone;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-24<br>
 * $Id: OrderIsDoneTest.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsDoneTest {
	private static IMocksControl control;
	private static OrderIsDone validator;
	private EditableOrder order;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		validator = new OrderIsDone();
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		order = control.createMock(EditableOrder.class);
	}
	
	@Test
	public void testValidate_Null() throws Exception {
		assertFalse(validator.validate(null));
	}
	
	@Test
	public void testValidate_OtherClassInstance() throws Exception {
		assertFalse(validator.validate(this));
	}
	
	@Test
	public void testValidate() throws Exception {
		Variant<OrderStatus> vStatus = new Variant<OrderStatus>()
			.add(OrderStatus.PENDING)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.CANCELLED)
			.add(OrderStatus.ACTIVE)
			.add(null);
		Variant<Boolean> vChanged = new Variant<Boolean>(vStatus)
			.add(true)
			.add(false);
		int found = 0;
		int index = 0;
		do {
			String msg = "At #" + index;
			control.resetToStrict();
			expect(order.getStatus()).andStubReturn(vStatus.get());
			expect(order.hasChanged(OrderImpl.STATUS_CHANGED))
				.andStubReturn(vChanged.get());
			control.replay();
			if ( vChanged.get() == true && (
					vStatus.get() == OrderStatus.FILLED ||
					vStatus.get() == OrderStatus.CANCELLED ))
			{
				found ++;
				assertTrue(msg, validator.validate(order));
				assertTrue(msg, order.hasChanged(OrderImpl.STATUS_CHANGED));
				assertTrue(msg, order.getStatus() == OrderStatus.FILLED
							||  order.getStatus() == OrderStatus.CANCELLED);
			} else {
				assertFalse(msg, validator.validate(order));
			}
			control.verify();
			index ++;
		} while ( vChanged.next() );
		assertEquals(2, found);
	}

}
