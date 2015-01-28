package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.setter.PositionSetVarMargin;

/**
 * 2012-09-16<br>
 * $Id$
 */
public class PositionSetVarMarginTest {
	private static EditablePosition position;
	private static PositionSetVarMargin setter;
	private static IMocksControl control;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		position = control.createMock(EditablePosition.class);		
		setter = new PositionSetVarMargin();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(10),		null,   false },
				{ new Double(201.1d),	201.1d, true  },
				{ null,					null,   false },
				{ new Boolean(false),	null,   false },
				{ this,					null,   false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fixture[i][2] ) {
				position.setVarMargin((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(position, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PositionSetVarMargin()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
