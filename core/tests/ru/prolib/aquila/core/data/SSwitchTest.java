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
 * 2013-01-07<br>
 * $Id: SSwitchTest.java 399 2013-01-06 23:29:15Z whirlwind $
 */
public class SSwitchTest {
	private static IMocksControl control;
	private static Validator validator;
	private static S<SSwitchTest> ifTrue,ifFalse;
	private static SSwitch<SSwitchTest> sw;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		validator = control.createMock(Validator.class);
		ifTrue = control.createMock(S.class);
		ifFalse = control.createMock(S.class);
		sw = new SSwitch<SSwitchTest>(validator, ifTrue, ifFalse);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(validator, sw.getValidator());
		assertSame(ifTrue, sw.getSetterIfTrue());
		assertSame(ifFalse, sw.getSetterIfFalse());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(sw.equals(sw));
		assertFalse(sw.equals(this));
		assertFalse(sw.equals(null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<Validator> vVal = new Variant<Validator>()
			.add(validator)
			.add(control.createMock(Validator.class));
		Variant<S<SSwitchTest>> vTrue = new Variant<S<SSwitchTest>>(vVal)
			.add(ifTrue)
			.add(control.createMock(S.class));
		Variant<S<SSwitchTest>> vFalse = new Variant<S<SSwitchTest>>(vTrue)
			.add(control.createMock(S.class))
			.add(ifFalse);
		Variant<?> iterator = vFalse;
		int foundCnt = 0;
		SSwitch<SSwitchTest> x = null, found = null;
		do {
			x = new SSwitch<SSwitchTest>(vVal.get(), vTrue.get(), vFalse.get());
			if ( sw.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(validator, found.getValidator());
		assertSame(ifTrue, found.getSetterIfTrue());
		assertSame(ifFalse, found.getSetterIfFalse());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 032345)
			.append(validator)
			.append(ifTrue)
			.append(ifFalse)
			.toHashCode(), sw.hashCode());
	}
	
	@Test
	public void testSet_IfTrue() throws Exception {
		expect(validator.validate(eq(new SetterArgs(this, "abc"))))
				.andReturn(true);
		ifTrue.set(same(this), eq("abc"));
		control.replay();
		sw.set(this, "abc");
		control.verify();
	}
	
	@Test
	public void testSet_IfFalse() throws Exception {
		expect(validator.validate(eq(new SetterArgs(this, "gamma"))))
			.andReturn(false);
		ifFalse.set(same(this), eq("gamma"));
		control.replay();
		sw.set(this, "gamma");
		control.verify();
	}
	
	@Test
	public void testSet_ThrowsIfValidatorThrows() throws Exception {
		ValidatorException expected = new ValidatorException("test");
		expect(validator.validate(eq(new SetterArgs(this, "gamma"))))
			.andThrow(expected);
		control.replay();
		
		try {
			sw.set(this, "gamma");
			fail("Expected: " + ValueException.class.getSimpleName());
		} catch ( ValueException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}

}
