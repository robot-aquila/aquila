package ru.prolib.aquila.ta.math;

import org.junit.*;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.math.Cross;
import static org.junit.Assert.*;

public class CrossTest {
	private TestValue<Double> src1;
	private TestValue<Double> src2;
	private Cross cross;

	@Before
	public void setUp() throws Exception {
		 src1 = new TestValue<Double>("foo");
		 src2 = new TestValue<Double>("bar");
		 cross = new Cross(src1, src2);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(src1, cross.getSource1());
		assertSame(src2, cross.getSource2());
		assertEquals(ValueImpl.DEFAULT_ID, cross.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		cross = new Cross(src1, src2, "foobar");
		assertSame(src1, cross.getSource1());
		assertSame(src2, cross.getSource2());
		assertEquals("foobar", cross.getId());
	}
	
	@Test
	public void testUpdate() throws Exception {
		double fixture[][] = {
				//src1,  src2
				{ 10.0d,  12.0d}, // 1 ниже 2 -> NONE
				{ 11.0d,  15.0d}, // 1 ниже 2 -> NONE
				{ 18.0d,  14.0d}, // 1 выше 2 -> сигнал ABOVE
				{ 13.0d,  11.0d}, // 1 выше 2 -> NONE
				{ 13.0d,  11.0d}, // 1 выше 2 -> NONE
				{101.0d, 121.0d}, // 1 ниже 2 -> сигнал BELOW
		};
		int expected[] = {
				ru.prolib.aquila.ta.indicator.Cross.NONE,
				ru.prolib.aquila.ta.indicator.Cross.NONE,
				ru.prolib.aquila.ta.indicator.Cross.ABOVE,
				ru.prolib.aquila.ta.indicator.Cross.NONE,
				ru.prolib.aquila.ta.indicator.Cross.NONE,
				ru.prolib.aquila.ta.indicator.Cross.BELOW,
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			src1.addToStackAndUpdate(fixture[i][0]);
			src2.addToStackAndUpdate(fixture[i][1]);
			cross.update();
			assertEquals("At sequence #" + i,
					expected[i], (int)cross.get());
		}
		assertEquals(expected.length, cross.getLength());
	}

}
