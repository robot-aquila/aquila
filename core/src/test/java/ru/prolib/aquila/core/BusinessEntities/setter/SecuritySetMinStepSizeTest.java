package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetMinStepSize;

/**
 * 2012-08-12<br>
 * $Id: SetSecurityMinStepSizeTest.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMinStepSizeTest {
	private EditableSecurity security;
	private SecuritySetMinStepSize setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetMinStepSize();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ null,					null,    false },
				{ this,					null,    false },
				{ new Boolean(false),	null,    false },
				{ new Double(71.123d),	71.123d, true  },
				{ new Integer(123),		null,    false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setMinStepSize((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetMinStepSize()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
