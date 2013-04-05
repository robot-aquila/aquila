package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetAskSize;

/**
 * 2012-12-20<br>
 * $Id: SetSecurityAskSizeTest.java 346 2012-12-20 16:48:36Z whirlwind $
 */
public class SecuritySetAskSizeTest {
	private static IMocksControl control;
	private static EditableSecurity security;
	private static SecuritySetAskSize setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		setter = new SecuritySetAskSize();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// initial value, converted value, set?
				{ 1234l,	1234l,	true  },
				{ 1234,		1234l,	true  },
				{ this,		null,	false },
				{ 1234,		1234l,	true  },
				{ 1234.0d,	1234l,	true  },
				{ 6789.5d,	6789l,	true  },
				{ null,     null,   false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fix[i][2] ) {
				security.setAskSize((Long) fix[i][1]); 
			}
			control.replay();
			setter.set(security, fix[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetAskSize()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121221, 94023)
			.toHashCode(), setter.hashCode());
	}

}
