package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.setter.PositionSetCurrQty;

/**
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetCurrQtyTest {
	private static EditablePosition position;
	private static PositionSetCurrQty setter;
	private static IMocksControl control;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		position = control.createMock(EditablePosition.class);		
		setter = new PositionSetCurrQty();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Double(281.1d),	 281L, true  },
				{ new Integer(12),		  12L, true  },
				{ new Long(1234L),		1234L, true  },
				{ null,					null,  false },
				{ new Boolean(false),	null,  false },
				{ this,					null,  false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				position.setCurrQty((Long) fixture[i][1]);
			}
			control.replay();
			setter.set(position, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PositionSetCurrQty()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
