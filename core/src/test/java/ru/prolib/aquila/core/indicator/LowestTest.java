package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.EditableSeries;
import ru.prolib.aquila.core.data.ValueEvent;
import ru.prolib.aquila.core.indicator.Lowest;


public class LowestTest {
	private IMocksControl control;
	private EventType type;
	private EditableSeries<Double> target;
	private Lowest indicator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		target = control.createMock(EditableSeries.class);
		indicator = new Lowest(target, 5);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(target, indicator.getTarget());
		assertEquals(5, indicator.getPeriod());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodLessThan2() throws Exception {
		new Lowest(target, 1);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTargetIsNull() throws Exception {
		new Lowest(null, 5);
	}
	
	@Test
	public void testOnEvent() throws Exception {
		Double fixture[][] = {
			// value, expected min
			{ null, null },
			{ 140d, 140d },
			{ 130d, 130d },
			{ null, 130d },
			{ 230d, 130d },
			{ 200d, 130d },
			{ 160d, 130d },
			{ 180d, 160d },
		};
		target.add(null);
		target.add(140d);
		target.add(130d);
		expectLastCall().times(5);
		target.add(160d);

		control.replay();
		for ( int i = 0; i < fixture.length; i ++ ) {
			indicator.onEvent(new ValueEvent<Double>(type, fixture[i][0], i));
		}
		control.verify();
	}

}
