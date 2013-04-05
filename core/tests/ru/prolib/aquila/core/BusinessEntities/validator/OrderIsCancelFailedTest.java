package ru.prolib.aquila.core.BusinessEntities.validator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsCancelFailed;

/**
 * 2012-09-23<br>
 * $Id: OrderIsCancelFailedTest.java 282 2012-09-25 02:27:46Z whirlwind $
 */
public class OrderIsCancelFailedTest {
	private static IMocksControl control;
	private static OrderIsCancelFailed validator;
	private EditableOrder order;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		validator = new OrderIsCancelFailed();
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		order = control.createMock(EditableOrder.class);
	}
	
	@Test
	public void testValidate() {
		assertFalse(validator.validate(order));
	}

}
