package ru.prolib.aquila.core.BusinessEntities.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsCancelFailed;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-23<br>
 * $Id: OrderIsCancelFailedTest.java 282 2012-09-25 02:27:46Z whirlwind $
 */
public class OrderIsCancelFailedTest {
	private IMocksControl control;
	private OrderIsCancelFailed validator;
	private EditableOrder order;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		validator = new OrderIsCancelFailed();
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
			.add(OrderStatus.ACTIVE)
			.add(OrderStatus.CANCEL_FAILED)
			.add(OrderStatus.CANCEL_SENT)
			.add(OrderStatus.CANCELLED)
			.add(OrderStatus.CONDITION)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.PENDING)
			.add(OrderStatus.REJECTED)
			.add(OrderStatus.SENT);
		Variant<Boolean> vChanged = new Variant<Boolean>(vStatus)
			.add(true)
			.add(false);
		Variant<?> iterator = vChanged;
		int found = 0;
		int index = 0;
		do {
			String msg = "At #" + index;
			setUp();
			expect(order.getStatus()).andStubReturn(vStatus.get());
			expect(order.hasChanged(OrderImpl.STATUS_CHANGED))
				.andStubReturn(vChanged.get());
			control.replay();
			if ( vChanged.get() == true
					&& vStatus.get() == OrderStatus.CANCEL_FAILED )
			{
				found ++;
				assertTrue(msg, validator.validate(order));
				assertTrue(msg, order.hasChanged(OrderImpl.STATUS_CHANGED));
			} else {
				assertFalse(msg, validator.validate(order));
			}
			control.verify();
			index ++;
		} while ( iterator.next() );
		assertEquals(1, found);
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
		assertTrue(validator.equals(new OrderIsCancelFailed()));
	}

}
