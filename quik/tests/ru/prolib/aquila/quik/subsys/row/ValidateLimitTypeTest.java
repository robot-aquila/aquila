package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.ValidatorException;

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
	public void testValidate_ThrowsIfRowGetThrows() throws Exception {
		RowException expected = new RowException("test");
		expect(rs.get("LIMIT_TYPE")).andThrow(expected);
		control.replay();
		
		try {
			validator.validate(rs);
			fail("Expected: " + ValidatorException.class.getSimpleName());
		} catch ( ValidatorException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertTrue(validator.equals(new ValidateLimitType()));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}

}
