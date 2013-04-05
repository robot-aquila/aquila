package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.RowSet;

/**
 * 2013-02-18<br>
 * $Id$
 */
public class ValidateLimitTypeTest {
	private IMocksControl control;
	private RowSet rs;
	private ValidateLimitType validator;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rs = control.createMock(RowSet.class);
		validator = new ValidateLimitType();
	}
	
	@Test
	public void testValidate() throws Exception {
		expect(rs.get("LIMIT_TYPE")).andReturn("Ден.средства");
		expect(rs.get("LIMIT_TYPE")).andReturn("Клиринговые ден.средства");
		control.replay();
		
		assertTrue(validator.validate(rs));
		assertFalse(validator.validate(rs));
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertTrue(validator.equals(new ValidateLimitType()));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}

}
