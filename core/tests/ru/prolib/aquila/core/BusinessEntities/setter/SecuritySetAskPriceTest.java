package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetAskPrice;

/**
 * 2012-12-20<br>
 * $Id: SetSecurityAskPriceTest.java 346 2012-12-20 16:48:36Z whirlwind $
 */
public class SecuritySetAskPriceTest {
	private static IMocksControl control;
	private static EditableSecurity security;
	private static SecuritySetAskPrice setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		setter = new SecuritySetAskPrice();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// initial value, converted value, set?
				{ 8234l,	8234.0d,	true  },
				{ 8234,		8234.0d,	true  },
				{ this,		null,		false },
				{ 8234.1d,	8234.1d,	true  },
				{ null,     null,   	false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fix[i][2] ) {
				security.setAskPrice((Double) fix[i][1]); 
			}
			control.replay();
			setter.set(security, fix[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetAskPrice()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121221, 103157)
			.toHashCode(), setter.hashCode());
	}

}
