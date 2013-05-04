package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * 2013-02-20<br>
 * $Id: ValidateSecurityRowTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class ValidateSecurityRowTest {
	private SecurityDescriptor descr;
	private IMocksControl control;
	private RowSet rs;
	private QUIKServiceLocator locator;
	private PartiallyKnownObjects descrs;
	private ValidateSecurityRow validator;

	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
		control = createStrictControl();
		rs = control.createMock(RowSet.class);
		locator = control.createMock(QUIKServiceLocator.class);
		descrs = control.createMock(PartiallyKnownObjects.class);
		validator = new ValidateSecurityRow(locator);
		expect(locator.getPartiallyKnownObjects()).andStubReturn(descrs);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, validator.getServiceLocator());
	}
	
	@Test
	public void testValidate() throws Exception {
		expect(rs.get(eq("SEC_DESCR"))).andReturn(descr);
		expect(rs.get(eq("SEC_SHORTNAME"))).andReturn("Сбербанк");
		descrs.registerSecurityDescriptor(same(descr), eq("Сбербанк"));
		control.replay();
		
		assertTrue(validator.validate(rs));
		
		control.verify();
	}
	
	@Test
	public void testValidate_ThrowsIfRowGetDescrThrows() throws Exception {
		RowException expected = new RowException("test");
		expect(rs.get(eq("SEC_DESCR"))).andThrow(expected);
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
	public void testValidate_ThrowsIfRowGetNameThrows() throws Exception {
		RowException expected = new RowException("test");
		expect(rs.get(eq("SEC_DESCR"))).andReturn(descr);
		expect(rs.get(eq("SEC_SHORTNAME"))).andThrow(expected);
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
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<?> iterator = vLoc;
		int foundCnt = 0;
		ValidateSecurityRow x = null, found = null;
		do {
			x = new ValidateSecurityRow(vLoc.get());
			if ( validator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
	}

}
