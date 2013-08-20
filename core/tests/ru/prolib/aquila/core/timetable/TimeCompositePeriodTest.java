package ru.prolib.aquila.core.timetable;


import static org.junit.Assert.*;

import java.util.*;

import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TimeCompositePeriodTest {
	private TimeCompositePeriod period;

	@Before
	public void setUp() throws Exception {
		period = new TimeCompositePeriod();
		period.add(new HMPeriod(new HMSpan(10, 0), new HMSpan(14, 0)));
		period.add(new HMPeriod(new HMSpan(14, 10), new HMSpan(18, 45)));
		period.add(new HMPeriod(new HMSpan(19, 0), new HMSpan(23, 50)));
	}
	
	@Test
	public void testContains() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 15,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 15,  9, 59, 59, 999), false),
				new FR(new DateTime(2013, 8, 15, 10,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 15, 12, 44, 19, 180), true),
				new FR(new DateTime(2013, 8, 15, 13, 59, 59, 999), true),
				new FR(new DateTime(2013, 8, 15, 14,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 15, 14,  9, 59, 999), false),
				new FR(new DateTime(2013, 8, 15, 14, 10,  0,   0), true),
				new FR(new DateTime(2013, 8, 15, 16, 30, 20,  15), true),
				new FR(new DateTime(2013, 8, 15, 18, 44, 59, 999), true),
				new FR(new DateTime(2013, 8, 15, 18, 45,  0,   0), false),
				new FR(new DateTime(2013, 8, 15, 18, 59, 59, 999), false),
				new FR(new DateTime(2013, 8, 15, 19,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 15, 21, 15, 24, 105), true),
				new FR(new DateTime(2013, 8, 15, 23, 49, 59, 999), true),
				new FR(new DateTime(2013, 8, 15, 23, 50,  0,   0), false),
				new FR(new DateTime(2013, 8, 15, 23, 59, 59, 999), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, period.contains(fr.time));
		}
	}
	
	@Test
	public void testNextStartTime() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 15,  0,  0,  0,   0),
						new DateTime(2013, 8, 15, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15,  9, 59, 59, 999), 
						new DateTime(2013, 8, 15, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 10,  0,  0,   0),
						new DateTime(2013, 8, 15, 14, 10,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 12, 44, 19, 180),
						new DateTime(2013, 8, 15, 14, 10,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 13, 59, 59, 999),
						new DateTime(2013, 8, 15, 14, 10,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 14,  0,  0,   0),
						new DateTime(2013, 8, 15, 14, 10,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 14,  9, 59, 999),
						new DateTime(2013, 8, 15, 14, 10,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 14, 10,  0,   0),
						new DateTime(2013, 8, 15, 19,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 16, 30, 20,  15),
						new DateTime(2013, 8, 15, 19,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 18, 44, 59, 999),
						new DateTime(2013, 8, 15, 19,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 18, 45,  0,   0),
						new DateTime(2013, 8, 15, 19,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 18, 59, 59, 999),
						new DateTime(2013, 8, 15, 19,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 19,  0,  0,   0), null),
				new FR2(new DateTime(2013, 8, 15, 21, 15, 24, 105), null),
				new FR2(new DateTime(2013, 8, 15, 23, 49, 59, 999), null),
				new FR2(new DateTime(2013, 8, 15, 23, 50,  0,   0), null),
				new FR2(new DateTime(2013, 8, 15, 23, 59, 59, 999), null),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextStartTime(fr.time));
		}
	}
	
	@Test
	public void testNextEndTime() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 15,  0,  0,  0,   0),
						new DateTime(2013, 8, 15, 14,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15,  9, 59, 59, 999),
						new DateTime(2013, 8, 15, 14,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 10,  0,  0,   0),
						new DateTime(2013, 8, 15, 14,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 14,  0,  0,   0),
						new DateTime(2013, 8, 15, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 14,  9, 59, 999),
						new DateTime(2013, 8, 15, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 14, 10,  0,   0),
						new DateTime(2013, 8, 15, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 16, 10, 15,  98),
						new DateTime(2013, 8, 15, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 18, 44, 59, 999),
						new DateTime(2013, 8, 15, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 18, 45,  0,   0),
						new DateTime(2013, 8, 15, 23, 50,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 19, 20, 12,  56),
						new DateTime(2013, 8, 15, 23, 50,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 23, 49, 59, 999),
						new DateTime(2013, 8, 15, 23, 50,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 23, 50,  0,   0), null),
				new FR2(new DateTime(2013, 8, 15, 23, 51, 45, 203), null),
				new FR2(new DateTime(2013, 8, 15, 23, 59, 59, 999), null)
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextEndTime(fr.time));
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(period.equals(period));
		assertFalse(period.equals(null));
		assertFalse(period.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<HMPeriod> rows1 = new Vector<HMPeriod>();
		rows1.add(new HMPeriod(new HMSpan(10,  0), new HMSpan(14,  0)));
		rows1.add(new HMPeriod(new HMSpan(14, 10), new HMSpan(18, 45)));
		rows1.add(new HMPeriod(new HMSpan(19,  0), new HMSpan(23, 50)));
		List<HMPeriod> rows2 = new Vector<HMPeriod>();
		rows2.add(new HMPeriod(new HMSpan(10,  0), new HMSpan(13, 50)));
		rows2.add(new HMPeriod(new HMSpan(14, 10), new HMSpan(18, 45)));
		Variant<List<HMPeriod>> vRows = new Variant<List<HMPeriod>>()
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		TimeCompositePeriod x, found = null;
		do {
			x = new TimeCompositePeriod();
			for ( HMPeriod p : vRows.get() ) { x.add(p); }
			if ( period.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rows1, found.getPeriods());
	}
	
	@Test
	public void testMarshalling() throws Exception {
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		
		String expected = "<TimeComposite>\n" +
		"  <period class=\"HourMinutePeriod\" value=\"10:00-14:00\"/>\n" +
		"  <period class=\"HourMinutePeriod\" value=\"14:10-18:45\"/>\n" +
		"  <period class=\"HourMinutePeriod\" value=\"19:00-23:50\"/>\n" +
		"</TimeComposite>";
		
		assertEquals(expected, xstream.toXML(period));
	}

}
