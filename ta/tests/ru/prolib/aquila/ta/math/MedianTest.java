package ru.prolib.aquila.ta.math;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.*;

public class MedianTest {
	private TestValue<Double> src1,src2;
	private Median v;

	@Before
	public void setUp() throws Exception {
		src1 = new TestValue<Double>();
		src2 = new TestValue<Double>();
		v =  new Median(src1, src2);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(src1, v.getSourceValue1());
		assertSame(src2, v.getSourceValue2());
		assertEquals(ValueImpl.DEFAULT_ID, v.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		v = new Median(src1, src2, "foobar");
		assertSame(src1, v.getSourceValue1());
		assertSame(src2, v.getSourceValue2());
		assertEquals("foobar", v.getId());
	}
	
	@Test
	public void testUpdate() throws Exception {
		double data[][] = {
				// src1, src2, expected
				{ 1.0d, 3.0d, 2.0d },
				{ 5.0d, 13.9d, 9.45d },
				{ 81.5d, 1.14d, 41.32d },
		};
		for ( int i = 0; i < data.length; i ++ ) {
			src1.addToStackAndUpdate(data[i][0]);
			src2.addToStackAndUpdate(data[i][1]);
			v.update();
			
			assertEquals("At sequence #" + i, data[i][2], v.get(), 0.000001d);
		}
		assertEquals(data.length, v.getLength());
	}

}
