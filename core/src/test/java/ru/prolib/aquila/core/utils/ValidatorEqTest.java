package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-10-15<br>
 * $Id: ValidatorEqTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class ValidatorEqTest {
	private static ValidatorEq validator;
	private static Object etalon;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		etalon = new String("foobar");
		validator = new ValidatorEq(etalon);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(etalon, validator.getEtalon());
	}
	
	@Test
	public void testValidate() throws Exception {
		Object fixture[][] = {
				// object, result
				{ null,		false },
				{ this,		false },
				{ etalon,	true  },
				{ "foobar",	true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			boolean actual = validator.validate(fixture[i][0]);
			assertEquals(msg, (Boolean) fixture[i][1], actual);
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(validator.equals(validator));
		assertTrue(validator.equals(new ValidatorEq(etalon)));
		assertTrue(validator.equals(new ValidatorEq("foobar")));
		assertFalse(validator.equals(new ValidatorEq("zulu24")));
		assertFalse(validator.equals(this));
		assertFalse(validator.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder().append("foobar").hashCode(); 
		assertEquals(hashCode, validator.hashCode());
	}

}
