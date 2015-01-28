package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetMinStepPrice;

/**
 * 2012-08-12<br>
 * $Id: SetSecurityMinStepPriceTest.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMinStepPriceTest {
	private EditableSecurity security;
	private SecuritySetMinStepPrice setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetMinStepPrice();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Double(88.5514d),	88.5514d, true  },
				{ new Boolean(false),	null,     false },
				{ new Integer(999),		null,     false },
				{ null,					null,     true  },
				{ this,					null,     false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setMinStepPrice((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetMinStepPrice()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
