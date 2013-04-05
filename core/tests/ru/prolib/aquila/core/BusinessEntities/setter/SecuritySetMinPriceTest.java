package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetMinPrice;

/**
 * 2012-08-12<br>
 * $Id: SetSecurityMinPriceTest.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetMinPriceTest {
	private EditableSecurity security;
	private SecuritySetMinPrice setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetMinPrice();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(10),		null,     false },
				{ new Double(24.1514d),	24.1514d, true  },
				{ null,					null,     true  },
				{ new Boolean(false),	null,     false },
				{ this,					null,     false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setMinPrice((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetMinPrice()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
}
