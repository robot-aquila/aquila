package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityStatus;
import ru.prolib.aquila.core.BusinessEntities.setter.SecuritySetStatus;

/**
 * 2012-12-29<br>
 * $Id: SetSecurityStatusTest.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetStatusTest {
	private static IMocksControl control;
	private static EditableSecurity security;
	private static SecuritySetStatus setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		setter = new SecuritySetStatus();
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new SecuritySetStatus()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121229, 161851)
			.append(SecuritySetStatus.class)
			.toHashCode(), setter.hashCode());
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// value, to set, set?
				{ SecurityStatus.TRADING, SecurityStatus.TRADING, true },
				{ null, null, false },
				{ this, null, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fix[i][2] == true ) {
				security.setStatus((SecurityStatus) eq(fix[i][1]));
			}
			control.replay();
			setter.set(security, fix[i][1]);
			control.verify();
		}
	}

}
