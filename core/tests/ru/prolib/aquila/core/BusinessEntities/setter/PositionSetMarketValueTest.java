package ru.prolib.aquila.core.BusinessEntities.setter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.setter.PositionSetMarketValue;

/**
 * 2012-12-30<br>
 * $Id: PositionSetMarketValueTest.java 390 2012-12-30 19:49:58Z whirlwind $
 */
public class PositionSetMarketValueTest {
	private static IMocksControl control;
	private static EditablePosition position;
	private static PositionSetMarketValue setter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		position = control.createMock(EditablePosition.class);
		setter = new PositionSetMarketValue();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// to setter, to position, expected?
				{ 1234.5d, 1234.5d, true  },
				{ 1234567, null,    false },
				{ null,    null,    false },
				{ this,    null,    false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			if ( (Boolean) fix[i][2] == true ) {
				position.setMarketValue((Double) fix[i][1]);
			}
			control.replay();
			setter.set(position, fix[i][0]);
			control.verify();
		}
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121231, 110351)
			.append(PositionSetMarketValue.class)
			.toHashCode(), setter.hashCode());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(setter.equals(setter));
		assertTrue(setter.equals(new PositionSetMarketValue()));
		assertFalse(setter.equals(null));
		assertFalse(setter.equals(this));
	}

}
