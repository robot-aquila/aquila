package ru.prolib.aquila.core.BusinessEntities.setter;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.*;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * 2013-02-21<br>
 * $Id: OrderSetTimeTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderSetTimeTest {
	private static IMocksControl control;
	private static EditableOrder order;
	private static OrderSetTime setter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = control.createMock(EditableOrder.class);
		setter = new OrderSetTime();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		DateTime d = new DateTime();
		Object fixture[][] = {
			// value, expected value, set?
			{ d,		d,		true },
			{ 201.1D,	null,	false },
			{ null,		null,	false },
			{ this,		null,	false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				order.setTime((DateTime) fixture[i][1]);
			}
			control.replay();
			setter.set(order, fixture[i][0]);
			control.verify();
		}

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new OrderSetTime()));
		assertFalse(setter.equals(this));
		assertFalse(setter.equals(null));
	}

}
