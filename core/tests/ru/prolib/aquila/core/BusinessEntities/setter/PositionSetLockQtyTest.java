package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.setter.PositionSetLockQty;

/**
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetLockQtyTest {
	private static EditablePosition position;
	private static PositionSetLockQty setter;
	private static IMocksControl control;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		position = control.createMock(EditablePosition.class);		
		setter = new PositionSetLockQty();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(10),		  10L, true  },
				{ new Double(201.1d),	 201L, true  },
				{ new Long(1000L),		1000L, true  },
				{ null,					null,  false },
				{ new Boolean(false),	null,  false },
				{ this,					null,  false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				position.setLockQty((Long) fixture[i][1]);
			}
			control.replay();
			setter.set(position, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PositionSetLockQty()));
		assertFalse(setter.equals(this));
		assertFalse(setter.equals(null));
	}

}
