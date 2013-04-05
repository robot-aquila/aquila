package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetBidSize;

/**
 * 2012-12-20<br>
 * $Id: SetSecurityBidSizeTest.java 346 2012-12-20 16:48:36Z whirlwind $
 */
public class SecuritySetBidSizeTest {
	private static IMocksControl control;
	private static EditableSecurity security;
	private static SecuritySetBidSize setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		setter = new SecuritySetBidSize();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// initial value, converted value, set?
				{ 51234l,	51234l,	true  },
				{ 51234,	51234l,	true  },
				{ this,		null,	false },
				{ 51234,	51234l,	true  },
				{ 51234.0d,	51234l,	true  },
				{ 56789.5d,	56789l,	true  },
				{ null,     null,   false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fix[i][2] ) {
				security.setBidSize((Long) fix[i][1]); 
			}
			control.replay();
			setter.set(security, fix[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetBidSize()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121221, 95849)
			.toHashCode(), setter.hashCode());
	}

}
