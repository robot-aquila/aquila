package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Variant;

public class OrderIsDoneTest {
	private IMocksControl control;
	private OrderIsDone validator;
	private EditableOrder order;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		validator = new OrderIsDone();
		order = control.createMock(EditableOrder.class);
	}
	
	@Test
	public void testValidate() throws Exception {
		Set<OrderStatus> expected = new HashSet<OrderStatus>();
		expected.add(OrderStatus.CANCEL_FAILED);
		expected.add(OrderStatus.CANCELLED);
		expected.add(OrderStatus.FILLED);
		expected.add(OrderStatus.REJECTED);
		
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
		Set<OrderStatus> actual = new HashSet<OrderStatus>();
		int found = 0;
		int index = 0;
		do {
			setUp();
			String msg = "At #" + index;
			expect(order.getStatus()).andStubReturn(vStatus.get());
			expect(order.hasChanged(EditableOrder.STATUS_CHANGED))
				.andStubReturn(vChanged.get());
			control.replay();
			if ( vChanged.get() == true && expected.contains(vStatus.get()) ) { 
				found ++;
				assertTrue(msg, validator.validate(order));
				assertTrue(msg, order.hasChanged(EditableOrder.STATUS_CHANGED));
				actual.add(vStatus.get());
			} else {
				assertFalse(msg, validator.validate(order));
			}
			control.verify();
			index ++;
		} while ( iterator.next() );
		assertEquals(4, found);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
		assertTrue(validator.equals(new OrderIsDone()));
	}

}
