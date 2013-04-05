package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetLotSize;

/**
 * 2012-08-12<br>
 * $Id: SetSecurityLotSizeTest.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetLotSizeTest {
	private EditableSecurity security;
	private SecuritySetLotSize setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetLotSize();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(10),		10,   true  },
				{ new Double(20.15d),	20,   true  },
				{ null,					null, false },
				{ new Boolean(false),	null, false },
				{ this,					null, false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = control.createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setLotSize((Integer) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetLotSize()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
