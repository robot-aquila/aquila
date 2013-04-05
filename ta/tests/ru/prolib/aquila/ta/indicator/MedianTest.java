package ru.prolib.aquila.ta.indicator;


import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.*;

public class MedianTest {
	private ValueImpl<Double> src1,src2;
	private Median med;

	@Before
	public void setUp() throws Exception {
		src1 = new ValueImpl<Double>(); src1.add(null);
		src2 = new ValueImpl<Double>(); src2.add(null);
		med =  new Median(src1, src2);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(src1, med.getFirstSource());
		assertSame(src2, med.getSecondSource());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfFirstSourceIsNull() throws Exception {
		new Median(null, src2);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSecondSourceIsNull() throws Exception {
		new Median(src1, null);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double data[][] = {
				// src1, src2, expected
				{  null,  null,    null },
				{  null,  123d,    null },
				{  123d,  null,    null },
				{  1.0d,  3.0d,    2.0d },
				{  5.0d, 13.9d,   9.45d },
				{ 81.5d,  1.14d, 41.32d },
		};
		for ( int i = 0; i < data.length; i ++ ) {
			src1.set(data[i][0]);
			src2.set(data[i][1]);
			String msg = "At sequence #" + i;
			if ( data[i][2] == null ) {
				assertNull(msg, med.calculate());
			} else {
				assertEquals(msg, data[i][2], med.calculate(), 0.000001d);
			}
		}
	}

}
