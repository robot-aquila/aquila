package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetDisplayName;

/**
 * 2012-12-19<br>
 * $Id: SetSecurityDisplayNameTest.java 344 2012-12-19 17:16:34Z whirlwind $
 */
public class SecuritySetDisplayNameTest {
	private static IMocksControl control;
	private static EditableSecurity security;
	private static SecuritySetDisplayName setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		setter = new SecuritySetDisplayName();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// initial value, converted value, set?
				{ "foobar", "foobar", true  },
				{ 12345,	null,	  false },
				{ this,		null,	  false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fix[i][2] ) {
				security.setDisplayName((String) fix[i][1]); 
			}
			control.replay();
			setter.set(security, fix[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetDisplayName()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121219, 203647)
			.toHashCode(), setter.hashCode());
	}

}
