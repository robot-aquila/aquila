package ru.prolib.aquila.ib.subsys.order;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.SetterArgs;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.subsys.order.IBIsEventOpenOrder;

/**
 * 2013-01-07<br>
 * $Id: IBIsEventOpenOrderTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBIsEventOpenOrderTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBIsEventOpenOrder validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		validator = new IBIsEventOpenOrder();
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertTrue(validator.equals(new IBIsEventOpenOrder()));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 41817)
			.append(IBIsEventOpenOrder.class)
			.toHashCode(), validator.hashCode());
	}

	@Test
	public void testValidate() throws Exception {
		Event valid = new IBEventOpenOrder(type, 1, new Contract(), new Order(),
				control.createMock(OrderState.class)); 
		Object fix[][] = {
			// object, expected result
			{ valid, false },
			{ new SetterArgs("unused", valid), true },
			{ null, false },
			{ this, false },
			{ new SetterArgs("unused", this), false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			assertEquals(msg,(Boolean) fix[i][1],validator.validate(fix[i][0]));
		}
	}

}
