package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.FirePositionAvailable;

/**
 * 2012-12-03<br>
 * $Id: FireEventPositionAvailableTest.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public class FirePositionAvailableTest {
	private static IMocksControl control;
	private static EditablePositions positions;
	private static EditablePosition position;
	private static FirePositionAvailable fire;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		positions = control.createMock(EditablePositions.class);
		position = control.createMock(EditablePosition.class);
		fire = new FirePositionAvailable(positions);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(positions, fire.getPositions());
	}
	
	@Test
	public void testFireEvent() throws Exception {
		positions.firePositionAvailableEvent(same(position));
		control.replay();
		fire.fireEvent(position);
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(fire.equals(fire));
		assertTrue(fire.equals(new FirePositionAvailable(positions)));
		assertFalse(fire.equals(null));
		assertFalse(fire.equals(this));
		assertFalse(fire.equals(new FirePositionAvailable(
				control.createMock(EditablePositions.class))));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121203, 35623)
			.append(positions)
			.toHashCode();
		assertEquals(hashCode, fire.hashCode());
	}

}
