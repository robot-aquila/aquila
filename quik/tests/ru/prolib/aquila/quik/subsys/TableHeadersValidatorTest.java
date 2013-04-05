package ru.prolib.aquila.quik.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-09<br>
 * $Id$
 */
public class TableHeadersValidatorTest {
	private static Set<String> headers1, headers2;
	private static String[] required1, required2;
	private IMocksControl control;
	private FirePanicEvent firePanic;
	private TableHeadersValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		headers1 = new HashSet<String>();
		headers1.add("one");
		headers1.add("two");
		headers1.add("three");
		headers2 = new HashSet<String>();
		headers2.add("one");
		headers2.add("three");
		required1 = new String[] { "one", "two", "three" };
		required2 = new String[] { "one", "three" };
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(EditableTerminal.class);
		validator = new TableHeadersValidator(firePanic, "foo", required1);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, validator.getFirePanicEvent());
		assertEquals("foo", validator.getTableId());
		assertArrayEquals(required1, validator.getRequiredHeaders());
	}
	
	@Test
	public void testValidate_Ok() throws Exception {
		control.replay();
		validator.validate(headers1);
		control.verify();
	}
	
	@Test
	public void testValidate_WrongTable() throws Exception {
		firePanic.firePanicEvent(eq(1),
				eq(validator.getClass().getSimpleName()+ "#validate:{}"),
				EasyMock.aryEq(new Object[] { "foo" }));
		control.replay();
		validator.validate(headers2);
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(validator.equals(validator));
		assertFalse(validator.equals(this));
		assertFalse(validator.equals(null));
	}

	@Test
	public void testEquals() throws Exception {
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(control.createMock(FirePanicEvent.class));
		Variant<String> vTab = new Variant<String>(vFire)
			.add("bar")
			.add("foo");
		Variant<String[]> vReq = new Variant<String[]>(vTab)
			.add(new String[] { "one", "two", "three" })
			.add(required2);
		Variant<?> iterator = vReq;
		int foundCnt = 0;
		TableHeadersValidator found = null, x = null;
		do {
			x = new TableHeadersValidator(vFire.get(), vTab.get(), vReq.get());
			if ( validator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertEquals("foo", found.getTableId());
		assertArrayEquals(required1, found.getRequiredHeaders());
	}

}
