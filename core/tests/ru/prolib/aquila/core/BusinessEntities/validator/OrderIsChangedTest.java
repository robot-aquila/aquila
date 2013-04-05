package ru.prolib.aquila.core.BusinessEntities.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsChanged;

/**
 * 2012-09-24<br>
 * $Id: OrderIsChangedTest.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsChangedTest {
	private static IMocksControl control;
	private static OrderIsChanged validator;
	private EditableOrder order;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		validator = new OrderIsChanged();
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
	public void testValidate() {
		Object fixture[][] = {
			// changed, valid?
			{ false,   false },
			{ true,    true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			control.resetToStrict();
			expect(order.hasChanged()).andStubReturn((Boolean) fixture[i][0]);
			control.replay();
			assertEquals(msg, fixture[i][1], validator.validate(order));
			control.verify();
		}
	}

}
