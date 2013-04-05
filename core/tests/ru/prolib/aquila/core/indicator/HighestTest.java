package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.EditableSeries;
import ru.prolib.aquila.core.data.ValueEvent;
import ru.prolib.aquila.core.indicator.Highest;

public class HighestTest {
	private IMocksControl control;
	private EventType type;
	private EditableSeries<Double> target;
	private Highest indicator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		target = control.createMock(EditableSeries.class);
		indicator = new Highest(target, 5);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(target, indicator.getTarget());
		assertEquals(5, indicator.getPeriod());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodLessThan2() throws Exception {
		new Highest(target, 1);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTargetIsNull() throws Exception {
		new Highest(null, 5);
	}
	
	@Test
	public void testOnEvent() throws Exception {
		Double fixture[][] = {
			// value, expected max
			{null, null},
			{null, null},
			{ 20d, 20d },
			{ 19d, 20d },
			{null, 20d },
			{ 23d, 23d },
			{ 20d, 23d },
			{ 16d, 23d },
			{ 18d, 23d },
			{ 15d, 23d },
			{ 14d, 20d },
			{ 15d, 18d },
			{ 16d, 18d },
		};
		target.add(null);
		expectLastCall().times(2);
		target.add(20d);
		expectLastCall().times(3);
		target.add(23d);
		expectLastCall().times(5);
		target.add(20d);
		target.add(18d);
		expectLastCall().times(2);

		control.replay();
		for ( int i = 0; i < fixture.length; i ++ ) {
			indicator.onEvent(new ValueEvent<Double>(type, fixture[i][0], i));
		}
		control.verify();
	}

}
