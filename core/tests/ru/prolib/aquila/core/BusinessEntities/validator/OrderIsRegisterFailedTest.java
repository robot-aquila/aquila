package ru.prolib.aquila.core.BusinessEntities.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsRegisterFailed;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-23<br>
 * $Id: OrderIsRegisterFailedTest.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class OrderIsRegisterFailedTest {
	private static IMocksControl control;
	private static OrderIsRegisterFailed validator;
	private EditableOrder order;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		validator = new OrderIsRegisterFailed();
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
		Variant<OrderStatus> vCurrStat = new Variant<OrderStatus>()
			.add(OrderStatus.PENDING)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.CANCELLED)
			.add(OrderStatus.ACTIVE)
			.add(null)
			.add(OrderStatus.FAILED);
		Variant<OrderStatus> vPrevStat = new Variant<OrderStatus>(vCurrStat)
			.add(OrderStatus.PENDING)
			.add(OrderStatus.FILLED)
			.add(OrderStatus.CANCELLED)
			.add(OrderStatus.ACTIVE)
			.add(null)
			.add(OrderStatus.FAILED);
		Variant<Boolean> vChanged = new Variant<Boolean>(vPrevStat)
			.add(true)
			.add(false);
		Variant<?> iterator = vChanged;
		int found = 0;
		int index = 0;
		do {
			String msg = "At #" + index;
			control.resetToStrict();
			expect(order.getStatus()).andStubReturn(vCurrStat.get());
			expect(order.getPreviousStatus()).andStubReturn(vPrevStat.get());
			expect(order.hasChanged(OrderImpl.STATUS_CHANGED))
				.andStubReturn(vChanged.get());
			control.replay();
			if ( vChanged.get() == true &&
				 vCurrStat.get() == OrderStatus.FAILED &&
				 vPrevStat.get() == OrderStatus.PENDING )
			{
				found ++;
				assertTrue(msg, validator.validate(order));
				assertTrue(msg, order.hasChanged(OrderImpl.STATUS_CHANGED));
				assertEquals(msg,OrderStatus.FAILED, order.getStatus());
				assertEquals(msg,OrderStatus.PENDING,order.getPreviousStatus());
			} else {
				assertFalse(msg, validator.validate(order));
			}
			control.verify();
			index ++;
		} while ( iterator.next() );
		assertEquals(1, found);
	}


}
