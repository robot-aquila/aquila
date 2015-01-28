package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

public class OrderIsChangedTest {
	private IMocksControl control;
	private OrderIsChanged validator;
	private EditableOrder order;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		validator = new OrderIsChanged();
		order = control.createMock(EditableOrder.class);
	}
	
	@Test
	public void testValidate() throws Exception {
		boolean fixture[][] = {
			// changed, expected valid?
			{ false,   false },
			{ true,    true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			setUp();
			String msg = "At #" + i;
			expect(order.hasChanged()).andReturn(fixture[i][0]);
			control.replay();
			assertEquals(msg, fixture[i][1], validator.validate(order));
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
		assertTrue(validator.equals(new OrderIsChanged()));
	}

}
