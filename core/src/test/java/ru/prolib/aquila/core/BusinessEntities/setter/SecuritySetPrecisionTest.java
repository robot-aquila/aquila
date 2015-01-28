package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetPrecision;

/**
 * 2012-08-12<br>
 * $Id: SetSecurityPrecisionTest.java 252 2012-08-12 16:51:42Z whirlwind $
 */
public class SecuritySetPrecisionTest {
	private EditableSecurity security;
	private SecuritySetPrecision setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetPrecision();
	}

	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ null,					null, false },
				{ this,					null, false },
				{ new Boolean(false),	null, false },
				{ new Double(71.123d),	71,   true  },
				{ new Integer(123),		123,  true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setPrecision((Integer) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetPrecision()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 155535).toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}
	
}
