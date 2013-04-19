package ru.prolib.aquila.core.BusinessEntities.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsPartiallyFilled;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-24<br>
 * $Id: OrderIsPartiallyFilledTest.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsPartiallyFilledTest {
	private static IMocksControl control;
	private static OrderIsPartiallyFilled validator;
	private EditableOrder order;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		validator = new OrderIsPartiallyFilled();
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
		Variant<Long> vQtyRest = new Variant<Long>(vStatus)
			.add(0L)
			.add(5L)
			.add(null);
		Variant<Boolean> vChanged = new Variant<Boolean>(vQtyRest)
			.add(true)
			.add(false);
		int found = 0;
		int index = 0;
		do {
			String msg = "At #" + index;
			control.resetToStrict();
			expect(order.getStatus()).andStubReturn(vStatus.get());
			expect(order.getQtyRest()).andStubReturn(vQtyRest.get());
			expect(order.hasChanged(OrderImpl.STATUS_CHANGED))
				.andStubReturn(vChanged.get());
			control.replay();
			if ( vStatus.get()==OrderStatus.CANCELLED && vChanged.get()==true
					&& vQtyRest.get() != null && vQtyRest.get() > 0 )
			{
				found ++;
				assertTrue(msg, validator.validate(order));
				assertTrue(msg, order.hasChanged(OrderImpl.STATUS_CHANGED));
				assertEquals(msg, OrderStatus.CANCELLED, order.getStatus());
				assertEquals(msg, new Long(5L), order.getQtyRest());
			} else {
				assertFalse(msg, validator.validate(order));
			}
			control.verify();
			index ++;
		} while ( vChanged.next() );
		assertEquals(1, found);
	}

}
