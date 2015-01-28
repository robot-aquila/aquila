package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.GCond;
import ru.prolib.aquila.core.data.GConst;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-03<br>
 * $Id: GCondTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class GCondTest {
	private IMocksControl control;
	private Row row;
	private GCond<String> getter;
	private Validator validator;
	private GConst<String> first,second;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		row = control.createMock(Row.class);
		first = new GConst<String>("first");
		second = new GConst<String>("second");
		validator = control.createMock(Validator.class);
		getter = new GCond<String>(validator, first, second);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(validator, getter.getValidator());
		assertSame(first, getter.getFirstGetter());
		assertSame(second, getter.getSecondGetter());
	}
	
	@Test
	public void testGet_IfValid() throws Exception {
		expect(validator.validate(row)).andReturn(true);
		control.replay();
		assertEquals("first", getter.get(row));
		control.verify();
	}
	
	@Test
	public void testGet_IfNotValid() throws Exception {
		expect(validator.validate(row)).andReturn(false);
		control.replay();
		assertEquals("second", getter.get(row));
		control.verify();
	}
	
	@Test
	public void testGet_ThrowsIfValidatorThrows() throws Exception {
		ValidatorException expected = new ValidatorException("test");
		expect(validator.validate(row)).andThrow(expected);
		control.replay();
		
		try {
			getter.get(row);
			fail("Expected: " + ValueException.class.getSimpleName());
		} catch ( ValueException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Validator> vValidator = new Variant<Validator>()
			.add(validator)
			.add(null)
			.add(control.createMock(Validator.class));
		Variant<G<String>> vFirst = new Variant<G<String>>(vValidator)
			.add(null)
			.add(new GConst<String>("first"))
			.add(new GConst<String>("gubba"));
		Variant<G<String>> vSecond = new Variant<G<String>>(vFirst)
			.add(null)
			.add(new GConst<String>("second"))
			.add(new GConst<String>("bubba"));
		int foundCnt = 0;
		GCond<String> found = null;
		do {
			GCond<String> actual = new GCond<String>(vValidator.get(),
						vFirst.get(), vSecond.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vSecond.next() );
		assertEquals(1, foundCnt);
		assertEquals(validator, found.getValidator());
		assertEquals(new GConst<String>("first"), found.getFirstGetter());
		assertEquals(new GConst<String>("second"),found.getSecondGetter());
		
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/052755)
			.append(validator)
			.append(new GConst<String>("first"))
			.append(new GConst<String>("second"))
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
