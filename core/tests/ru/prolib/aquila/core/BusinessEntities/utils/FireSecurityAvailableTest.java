package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.FireSecurityAvailable;

/**
 * 2013-02-06<br>
 * $Id$
 */
public class FireSecurityAvailableTest {
	private IMocksControl control;
	private EditableSecurities securities;
	private EditableSecurity security;
	private FireSecurityAvailable fire;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		securities = control.createMock(EditableSecurities.class);
		security = control.createMock(EditableSecurity.class);
		fire = new FireSecurityAvailable(securities);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(securities, fire.getSecurities());
	}
	
	@Test
	public void testFireEvent() throws Exception {
		securities.fireSecurityAvailableEvent(same(security));
		control.replay();
		fire.fireEvent(security);
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(fire.equals(fire));
		assertFalse(fire.equals(null));
		assertFalse(fire.equals(this));
		assertTrue(fire.equals(new FireSecurityAvailable(securities)));
		EditableSecurities s2 = control.createMock(EditableSecurities.class);
		assertFalse(fire.equals(new FireSecurityAvailable(s2)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130207, 214223)
			.append(securities)
			.toHashCode(), fire.hashCode());
	}

}
