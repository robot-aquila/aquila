package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetLastPrice;

/**
 * 2012-11-04<br>
 * $Id: SecuritySetLastPriceTest.java 302 2012-11-05 04:02:02Z whirlwind $
 */
public class SecuritySetLastPriceTest {
	private EditableSecurity security;
	private SecuritySetLastPrice setter;
	private IMocksControl control;

	@Before
	public void setUp() throws Exception {
		setter = new SecuritySetLastPrice();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fixture[][] = {
				// value, expected value, set?
				{ new Integer(100),		null,   false },
				{ new Double(28.15d),	28.15d, true  },
				{ null,					null,   true  },
				{ new Boolean(false),	null,   false },
				{ this,					null,   false },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			control = createStrictControl();
			security = control.createMock(EditableSecurity.class);
			if ( (Boolean) fixture[i][2] ) {
				security.setLastPrice((Double) fixture[i][1]);
			}
			control.replay();
			setter.set(security, fixture[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetLastPrice()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121105, 140149)
			.toHashCode();
		assertEquals(hashCode, setter.hashCode());
	}

}
