package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Editable;
import ru.prolib.aquila.core.BusinessEntities.EditableObjectException;
import ru.prolib.aquila.core.BusinessEntities.FireEditableEvent;
import ru.prolib.aquila.core.BusinessEntities.setter.EditableEventGenerator;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.core.utils.ValidatorStub;

/**
 * 2012-11-29<br>
 * $Id: EditableEventGeneratorTest.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public class EditableEventGeneratorTest {
	private static IMocksControl control;
	private static Editable object;
	private static Validator avail;
	private static FireEditableEvent fire;
	private static EditableEventGenerator<Editable> generator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		avail = control.createMock(Validator.class);
		fire = control.createMock(FireEditableEvent.class);
		object = control.createMock(Editable.class);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		generator = new EditableEventGenerator<Editable>(avail, fire);
	}
	
	@Test
	public void testSet_IfNotChanged() throws Exception {
		expect(object.hasChanged()).andReturn(false);
		control.replay();
		generator.set(object, null);
		control.verify();
	}
	
	@Test
	public void testSet_IfChanged() throws Exception {
		expect(object.hasChanged()).andReturn(true);
		expect(object.isAvailable()).andReturn(true);
		object.fireChangedEvent();
		object.resetChanges();
		control.replay();
		generator.set(object, null);
		control.verify();
	}

	@Test
	public void testSet_IfNotAvailable() throws Exception {
		expect(object.hasChanged()).andReturn(true);
		expect(object.isAvailable()).andReturn(false);
		expect(avail.validate(object)).andReturn(false);
		object.resetChanges();
		control.replay();
		generator.set(object, null);
		control.verify();
	}

	@Test
	public void testSet_IfAvailable() throws Exception {
		expect(object.hasChanged()).andReturn(true);
		expect(object.isAvailable()).andReturn(false);
		expect(avail.validate(object)).andReturn(true);
		fire.fireEvent(object);
		object.setAvailable(true);
		object.resetChanges();
		control.replay();
		generator.set(object, null);
		control.verify();
	}
	
	@Test
	public void testSet_ThrowsIfValidatorThrows() throws Exception {
		ValidatorException expected = new ValidatorException("test");
		expect(object.hasChanged()).andReturn(true);
		expect(object.isAvailable()).andReturn(false);
		expect(avail.validate(object)).andThrow(expected);
		control.replay();
		
		try {
			generator.set(object, null);
			fail("Expected: " + ValueException.class.getSimpleName());
		} catch ( ValueException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}
	
	@Test
	public void testSet_ThrowsIfFireChangedEventThrows() throws Exception {
		EditableObjectException expected = new EditableObjectException("test");
		expect(object.hasChanged()).andReturn(true);
		expect(object.isAvailable()).andReturn(true);
		object.fireChangedEvent();
		expectLastCall().andThrow(expected);
		control.replay();
		
		try {
			generator.set(object, null);
			fail("Expected: " + ValueException.class.getSimpleName());
		} catch ( ValueException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testEquals() throws Exception {
		assertTrue(generator.equals(generator));
		assertTrue(generator.equals(new EditableEventGenerator(avail, fire)));
		assertFalse(generator.equals(null));
		assertFalse(generator.equals(this));
		assertFalse(generator.equals(new EditableEventGenerator(avail,
				control.createMock(FireEditableEvent.class))));
		assertFalse(generator.equals(new EditableEventGenerator(
				control.createMock(Validator.class),
				control.createMock(FireEditableEvent.class))));
		assertFalse(generator.equals(new EditableEventGenerator(
				control.createMock(Validator.class), fire)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121129, 194619)
			.append(avail)
			.append(fire)
			.toHashCode();
		assertEquals(hashCode, generator.hashCode());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(avail, generator.getAvailabilityValidator());
		assertSame(fire, generator.getFireAvailableEvent());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		generator = new EditableEventGenerator<Editable>(fire);
		assertSame(fire, generator.getFireAvailableEvent());
		assertEquals(new ValidatorStub(true),
					 generator.getAvailabilityValidator());
	}

}
