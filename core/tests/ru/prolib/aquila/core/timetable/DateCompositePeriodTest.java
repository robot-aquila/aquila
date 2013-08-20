package ru.prolib.aquila.core.timetable;


import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.joda.time.DateTime;
import org.junit.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import ru.prolib.aquila.core.utils.Variant;

public class DateCompositePeriodTest {
	private DateCompositePeriod period;

	@Before
	public void setUp() throws Exception {
		period = new DateCompositePeriod();
		period.add(new DOWPeriod(DOW.TUESDAY, DOW.TUESDAY));
		period.add(new DOWPeriod(DOW.THURSDAY, DOW.FRIDAY));
	}
	
	@Test
	public void testContains() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 19,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 19, 23, 59, 59, 999), false),
				new FR(new DateTime(2013, 8, 20,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 20, 23, 59, 59, 999), true),
				new FR(new DateTime(2013, 8, 21,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 22,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 23,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 24,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 25,  0,  0,  0,   0), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, period.contains(fr.time));
		}
	}
	
	@Test
	public void testNextDate() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 19, 12, 14, 25, 815),
						new DateTime(2013, 8, 20,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 20,  0,  0,  0,   0),
						new DateTime(2013, 8, 22,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 20, 23, 59, 59, 999),
						new DateTime(2013, 8, 22,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 21,  8, 40, 12,  34),
						new DateTime(2013, 8, 22,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 22,  0,  0,  0,   0),
						new DateTime(2013, 8, 23,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 23,  0,  0,  0,   0),
						new DateTime(2013, 8, 27,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 24, 15, 26, 10, 120),
						new DateTime(2013, 8, 27,  0,  0,  0,   0)),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextDate(fr.time));
		}
	}
	
	@Test
	public void testIsEndDate() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 19, 12, 14, 25, 815), false),
				new FR(new DateTime(2013, 8, 20,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 20, 23, 59, 59, 999), true),
				new FR(new DateTime(2013, 8, 21,  8, 40, 12,  34), false),
				new FR(new DateTime(2013, 8, 22,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 23,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 24, 15, 26, 10, 120), false),
				new FR(new DateTime(2013, 8, 25, 21, 30, 15, 220), false),
				new FR(new DateTime(2013, 8, 26,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 27,  0,  0,  0,   0), true),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, period.isEndDate(fr.time));
		}
	}
	
	@Test
	public void testNextEndDate() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 19, 12, 14, 25, 815),
						new DateTime(2013, 8, 20,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 20, 12, 30, 59, 428),
						new DateTime(2013, 8, 20, 12, 30, 59, 428)),
						
				new FR2(new DateTime(2013, 8, 21,  8, 40, 12,  34),
						new DateTime(2013, 8, 23,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 22,  0,  0,  0,   0),
						new DateTime(2013, 8, 23,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 23, 15, 10, 20,  11),
						new DateTime(2013, 8, 23, 15, 10, 20,  11)),
				new FR2(new DateTime(2013, 8, 24, 15, 26, 10, 120),
						new DateTime(2013, 8, 27,  0,  0,  0,   0)),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextEndDate(fr.time));
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
		List<DOWPeriod> rows1 = new Vector<DOWPeriod>();
		rows1.add(new DOWPeriod(DOW.TUESDAY, DOW.TUESDAY));
		rows1.add(new DOWPeriod(DOW.THURSDAY, DOW.FRIDAY));
		List<DOWPeriod> rows2 = new Vector<DOWPeriod>();
		rows2.add(new DOWPeriod(DOW.SATURDAY, DOW.SUNDAY));
		Variant<List<DOWPeriod>> vRows = new Variant<List<DOWPeriod>>()
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		DateCompositePeriod x, found = null;
		do {
			x = new DateCompositePeriod();
			for ( DOWPeriod p : vRows.get() ) { x.add(p); }
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
		XStream stream = new XStream(new DomDriver());
		stream.autodetectAnnotations(true);
		
		String expected = "<DateComposite>\n" +
		"  <period class=\"DayOfWeekPeriod\" value=\"TUESDAY\"/>\n" +
		"  <period class=\"DayOfWeekPeriod\" value=\"THURSDAY-FRIDAY\"/>\n" +
		"</DateComposite>";

		assertEquals(expected, stream.toXML(period));
	}

}
