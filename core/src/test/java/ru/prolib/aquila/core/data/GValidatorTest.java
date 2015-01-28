package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-30<br>
 * $Id: GValidatorTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GValidatorTest {
	private static IMocksControl control;
	private static G<Integer> getter;
	private static Validator subValidator;
	private static GValidator validator;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		getter = control.createMock(G.class);
		subValidator = control.createMock(Validator.class);
		validator = new GValidator(getter, subValidator);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testGet() throws Exception {
		Integer value = 12345;
		expect(getter.get(same(this))).andReturn(value);
		expect(subValidator.validate(same(value))).andReturn(true);
		control.replay();
		
		assertTrue(validator.validate(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_ThrowsIfGetterThrows() throws Exception {
		ValueException expected = new ValueException("test");
		expect(getter.get(same(this))).andThrow(expected);
		control.replay();
		
		try {
			validator.validate(this);
			fail("Expected: " + ValidatorException.class.getSimpleName());
		} catch ( ValidatorException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<G<Integer>> vGetter = new Variant<G<Integer>>()
			.add(null).add(getter).add(control.createMock(G.class));
		Variant<Validator> vValidator = new Variant<Validator>(vGetter)
			.add(subValidator).add(validator).add(null);
		int foundCnt = 0;
		GValidator found = null;
		do {
			GValidator actual = new GValidator(vGetter.get(), vValidator.get());
			if ( validator.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
			
		} while ( vValidator.next() );
		assertEquals(1, foundCnt);
		assertSame(getter, found.getGetter());
		assertSame(subValidator, found.getValidator());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(null));
		assertFalse(validator.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/55551)
			.append(getter)
			.append(subValidator)
			.hashCode();
		assertEquals(hashCode, validator.hashCode());
	}

}
