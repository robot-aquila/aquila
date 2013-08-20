package ru.prolib.aquila.core.timetable;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.DomDriver;

import ru.prolib.aquila.core.utils.Variant;

public class DailyIntradayTest {
	private DatePeriod date;
	private TimePeriod time;
	private DailyIntraday period;
	private IMocksControl control;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		// вт-пт
		date = new DOWPeriod(new DOWSpan(DOW.TUESDAY), new DOWSpan(DOW.FRIDAY));
		// 10:00-18:45
		time = new HMPeriod(new HMSpan(10, 0), new HMSpan(18, 45));
		period = new DailyIntraday(date, time);
	}
	
	@Test
	public void testContains() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013,  8, 12,  0,  0,  0,   0), false),
				new FR(new DateTime(2013,  8, 12, 10, 15, 30, 581), false),
				new FR(new DateTime(2013,  8, 13,  8, 29, 45, 112), false),
				new FR(new DateTime(2013,  8, 13,  9, 59, 59, 999), false),
				new FR(new DateTime(2013,  8, 13, 10,  0,  0,   0), true),
				new FR(new DateTime(2013,  8, 13, 10, 15, 32,  12), true),
				new FR(new DateTime(2013,  8, 13, 18, 15, 32,  12), true),
				new FR(new DateTime(2013,  8, 13, 18, 44, 59, 999), true),
				new FR(new DateTime(2013,  8, 13, 18, 45,  0,   0), false),
				new FR(new DateTime(2013,  8, 16, 10,  0,  0,   0), true),
				new FR(new DateTime(2013,  8, 17, 10,  0,  0,   0), false),
				new FR(new DateTime(2013,  8, 18, 10,  0,  0,   0), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, period.contains(fr.time));
		}
	}
	
	@Test
	public void testNextStartTime_EndOfDate() throws Exception {
		date = control.createMock(DatePeriod.class);
		time = control.createMock(TimePeriod.class);
		period = new DailyIntraday(date, time);
		DateTime t = new DateTime();
		expect(date.contains(same(t))).andReturn(false);
		expect(date.nextDate(same(t))).andReturn(null);
		control.replay();
		
		assertNull(period.nextStartTime(t));
		
		control.verify();
	}
	
	@Test
	public void testNextStartTime_EndOfIntraday() throws Exception {
		date = control.createMock(DatePeriod.class);
		time = control.createMock(TimePeriod.class);
		period = new DailyIntraday(date, time);
		DateTime t = new DateTime(), t2 = new DateTime(), t3 = new DateTime();
		expect(date.contains(same(t))).andReturn(true);
		expect(time.nextStartTime(same(t))).andReturn(null);
		expect(date.nextDate(same(t))).andReturn(t2);
		
		
		expect(time.nextStartTime(same(t2))).andReturn(t3);
		control.replay();
		
		assertSame(t3, period.nextStartTime(t));
		
		control.verify();
	}
	
	@Test
	public void testNextStartTime_EndOfIntradayAndDate() throws Exception {
		date = control.createMock(DatePeriod.class);
		time = control.createMock(TimePeriod.class);
		period = new DailyIntraday(date, time);
		DateTime t = new DateTime();
		expect(date.contains(same(t))).andReturn(true);
		expect(time.nextStartTime(same(t))).andReturn(null);
		expect(date.nextDate(same(t))).andReturn(null);
		control.replay();
		
		assertNull(period.nextStartTime(t));
		
		control.verify();
	}
	
	@Test
	public void testNextStartTime() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 12,  0,  0,  0,   0),
						new DateTime(2013, 8, 13, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13,  1, 15, 34, 180),
						new DateTime(2013, 8, 13, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13,  9, 59, 59, 999),
						new DateTime(2013, 8, 13, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 10,  0,  0,   0),
						new DateTime(2013, 8, 14, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 10,  0,  0,   1),
						new DateTime(2013, 8, 14, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 18, 44, 59, 999),
						new DateTime(2013, 8, 14, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 18, 45,  0,   0),
						new DateTime(2013, 8, 14, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 23, 59, 59, 999),
						new DateTime(2013, 8, 14, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 14,  7, 30,  0,   0),
						new DateTime(2013, 8, 14, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 14, 15, 18, 22, 634),
						new DateTime(2013, 8, 15, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15,  0,  0,  0,   0),
						new DateTime(2013, 8, 15, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15, 18, 40,  0,   0),
						new DateTime(2013, 8, 16, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 16, 23, 59, 59, 999),
						new DateTime(2013, 8, 20, 10,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 17,  3, 15,  0,   0),
						new DateTime(2013, 8, 20, 10,  0,  0,   0)),
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
				new FR2(new DateTime(2013, 8, 12,  0,  0,  0,   0),
						new DateTime(2013, 8, 13, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 13,  1, 15, 34, 180),
						new DateTime(2013, 8, 13, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 18, 44, 59, 999),
						new DateTime(2013, 8, 13, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 13, 18, 45,  0,   0),
						new DateTime(2013, 8, 14, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 14, 10,  0,  0,   1),
						new DateTime(2013, 8, 14, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 14, 18, 45,  0,   1),
						new DateTime(2013, 8, 15, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 16,  8, 10,  0,   0),
						new DateTime(2013, 8, 16, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 16, 21, 19, 23, 911),
						new DateTime(2013, 8, 20, 18, 45,  0,   0)),
				new FR2(new DateTime(2013, 8, 17,  0,  0,  0,   0),
						new DateTime(2013, 8, 20, 18, 45,  0,   0)),
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
		Variant<DatePeriod> vDate = new Variant<DatePeriod>()
			.add(date)
			.add(control.createMock(DatePeriod.class));
		Variant<TimePeriod> vTime = new Variant<TimePeriod>(vDate)
			.add(time)
			.add(control.createMock(TimePeriod.class));
		Variant<?> iterator = vTime;
		int foundCnt = 0;
		DailyIntraday x, found = null;
		do {
			x = new DailyIntraday(vDate.get(), vTime.get());
			if ( period.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(date, found.getDatePeriod());
		assertEquals(time, found.getTimePeriod());
	}
	
	@Test
	public void testMarshalling() throws Exception {
		DateCompositePeriod date = new DateCompositePeriod();
		date.add(new DOWPeriod(DOW.MONDAY, DOW.MONDAY));
		date.add(new DOWPeriod(DOW.THURSDAY, DOW.FRIDAY));
		period = new DailyIntraday(date, time);
		
		XStream stream = new XStream(new DomDriver());
		stream.autodetectAnnotations(true);
		
		String expected = "<DailyIntraday>\n" +
		"  <datePeriod class=\"DateComposite\">\n" +
		"    <period class=\"DayOfWeekPeriod\" value=\"MONDAY\"/>\n" +
		"    <period class=\"DayOfWeekPeriod\" value=\"THURSDAY-FRIDAY\"/>\n" +
		"  </datePeriod>\n" +
		"  <timePeriod class=\"HourMinutePeriod\" value=\"10:00-18:45\"/>\n" +
		"</DailyIntraday>";
		//stream.toXML(period, System.out);
		
		assertEquals(expected, stream.toXML(period));
		assertEquals(period, stream.fromXML(expected));
	}

}
