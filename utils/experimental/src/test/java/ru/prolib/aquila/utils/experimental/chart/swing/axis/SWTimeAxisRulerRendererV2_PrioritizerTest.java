package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerRendererV2.Prioritizer;

public class SWTimeAxisRulerRendererV2_PrioritizerTest {
	private static ZoneId MSK = ZoneId.of("Europe/Moscow");
	private static ZoneId KLN = ZoneId.of("Europe/Kaliningrad");
	
	static Instant MT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(MSK).toInstant();
	}
	
	static Instant KT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(KLN).toInstant();
	}

	private Prioritizer service;
	
	@Before
	public void setUp() throws Exception {
		service = new Prioritizer();
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevIsNotDefined() {
		assertEquals(17, service.getPriority(null, MT("2018-11-30T00:00:00.259"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T18:26:49.259"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T23:59:59.999"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T01:00:00.182"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T01:05:00.100"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T01:10:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T01:15:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T01:30:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T03:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T06:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-30T12:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-21T00:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-11-01T00:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2018-01-01T00:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2010-01-01T00:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("2000-01-01T00:00:00.001"), MSK));
		assertEquals(17, service.getPriority(null, MT("1900-01-01T00:00:00.001"), MSK));
		
		assertEquals(17, service.getPriority(null, MT("1234-06-12T05:12:37.115"), MSK));
		assertEquals(17, service.getPriority(null, MT("1265-02-15T13:33:04.112"), MSK));
		
		assertEquals(16, service.getPriority(null, MT("1265-02-15T13:33:04.000"), MSK));
		assertEquals(15, service.getPriority(null, MT("1265-02-15T13:33:00.000"), MSK));
		assertEquals(10, service.getPriority(null, MT("1265-02-15T13:00:00.000"), MSK));
		assertEquals(14, service.getPriority(null, MT("1265-02-15T13:05:00.000"), MSK));
		assertEquals(13, service.getPriority(null, MT("1265-02-15T13:10:00.000"), MSK));
		assertEquals(12, service.getPriority(null, MT("1265-02-15T13:15:00.000"), MSK));
		assertEquals(13, service.getPriority(null, MT("1265-02-15T13:20:00.000"), MSK));
		assertEquals(14, service.getPriority(null, MT("1265-02-15T13:25:00.000"), MSK));
		assertEquals(11, service.getPriority(null, MT("1265-02-15T13:30:00.000"), MSK));
		assertEquals(14, service.getPriority(null, MT("1265-02-15T13:35:00.000"), MSK));
		assertEquals(13, service.getPriority(null, MT("1265-02-15T13:40:00.000"), MSK));
		assertEquals(12, service.getPriority(null, MT("1265-02-15T13:45:00.000"), MSK));
		assertEquals(13, service.getPriority(null, MT("1265-02-15T13:50:00.000"), MSK));
		assertEquals(14, service.getPriority(null, MT("1265-02-15T13:55:00.000"), MSK));
		assertEquals(10, service.getPriority(null, MT("1265-02-15T14:00:00.000"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevIsSameWithCurr() {
		assertEquals(17, service.getPriority(MT("2018-11-30T00:00:00.115"), MT("2018-11-30T00:00:00.115"), MSK));
		
		assertEquals(17, service.getPriority(MT("1234-06-12T05:12:37.115"), MT("1234-06-12T05:12:37.115"), MSK));
		assertEquals(17, service.getPriority(MT("1265-02-15T13:33:04.112"), MT("1265-02-15T13:33:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInSameSecond() {
		assertEquals(17, service.getPriority(MT("2018-11-30T00:00:00.115"), MT("2018-11-30T00:00:00.259"), MSK));
		assertEquals(17, service.getPriority(MT("2018-11-30T18:26:49.000"), MT("2018-11-30T18:26:49.259"), MSK));
		assertEquals(17, service.getPriority(MT("2018-11-30T23:59:59.022"), MT("2018-11-30T23:59:59.999"), MSK));
		
		assertEquals(17, service.getPriority(MT("1105-07-12T16:36:22.107"), MT("1105-07-12T16:36:22.703"), MSK));
		assertEquals(17, service.getPriority(MT("1265-02-15T13:33:04.860"), MT("1265-02-15T13:33:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherSecond() {
		assertEquals(16, service.getPriority(MT("2018-11-30T00:00:00.999"), MT("2018-11-30T00:00:01.001"), MSK));
		assertEquals(16, service.getPriority(MT("2018-11-30T18:26:00.259"), MT("2018-11-30T18:26:49.259"), MSK));
		assertEquals(16, service.getPriority(MT("2018-11-30T23:59:30.999"), MT("2018-11-30T23:59:59.999"), MSK));
		
		assertEquals(16, service.getPriority(MT("1009-03-27T09:30:13.206"), MT("1009-03-27T09:30:14.771"), MSK));
		assertEquals(16, service.getPriority(MT("1265-02-15T13:33:04.860"), MT("1265-02-15T13:33:06.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherMinute() {
		assertEquals(15, service.getPriority(MT("2018-11-30T00:00:00.999"), MT("2018-11-30T00:01:00.999"), MSK));
		assertEquals(15, service.getPriority(MT("2018-11-30T18:26:00.259"), MT("2018-11-30T18:27:00.259"), MSK));
		assertEquals(15, service.getPriority(MT("2018-11-30T23:56:30.999"), MT("2018-11-30T23:57:59.999"), MSK));
		
		assertEquals(15, service.getPriority(MT("1054-06-29T15:50:24.402"), MT("1054-06-29T15:53:23.720"), MSK));
		assertEquals(15, service.getPriority(MT("1265-02-15T13:33:04.860"), MT("1265-02-15T13:34:01.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother5Mins() {
		assertEquals(14, service.getPriority(MT("2018-11-30T00:00:00.000"), MT("2018-11-30T00:07:00.001"), MSK));
		assertEquals(14, service.getPriority(MT("2018-11-30T18:24:59.120"), MT("2018-11-30T18:28:00.259"), MSK));
		assertEquals(14, service.getPriority(MT("2018-11-30T23:52:12.108"), MT("2018-11-30T23:57:59.999"), MSK));
		
		assertEquals(14, service.getPriority(MT("1019-08-24T15:02:12.665"), MT("1019-08-24T15:06:16.090"), MSK));
		assertEquals(14, service.getPriority(MT("1265-02-15T13:33:04.860"), MT("1265-02-15T13:37:01.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother10Mins() {
		assertEquals(13, service.getPriority(MT("2018-11-30T00:00:00.000"), MT("2018-11-30T00:11:00.001"), MSK));
		assertEquals(13, service.getPriority(MT("2018-11-30T18:19:59.999"), MT("2018-11-30T18:20:00.001"), MSK));
		assertEquals(13, service.getPriority(MT("2018-11-30T23:48:12.108"), MT("2018-11-30T23:57:59.999"), MSK));
		
		assertEquals(13, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother15Mins() {
		assertEquals(12, service.getPriority(MT("2018-11-30T00:00:00.000"), MT("2018-11-30T00:16:00.001"), MSK));
		assertEquals(12, service.getPriority(MT("2018-11-30T18:14:59.999"), MT("2018-11-30T18:20:00.001"), MSK));
		assertEquals(12, service.getPriority(MT("2018-11-30T23:44:59.999"), MT("2018-11-30T23:45:00.001"), MSK));
		assertEquals(12, service.getPriority(MT("2018-11-30T09:08:12.999"), MT("2018-11-30T09:16:05.001"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-15T13:48:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother30Mins() {
		assertEquals(11, service.getPriority(MT("1995-02-13T03:15:46.240"), MT("1995-02-13T03:56:21.129"), MSK));
		assertEquals(11, service.getPriority(MT("2018-11-30T00:00:00.000"), MT("2018-11-30T00:30:00.001"), MSK));
		assertEquals(11, service.getPriority(MT("2018-11-30T00:00:00.000"), MT("2018-11-30T00:59:59.999"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:23:02.860"), MT("1265-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("1995-02-13T03:15:46.240"), MT("1995-02-13T04:56:21.129"), MSK));
		assertEquals(10, service.getPriority(MT("1995-02-13T03:15:46.240"), MT("1995-02-13T05:56:21.129"), MSK));
		assertEquals(10, service.getPriority(MT("2018-11-30T00:00:00.000"), MT("2018-11-30T02:29:07.001"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-15T14:12:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-15T03:15:24.001"), MSK));
		// But in another timezone the start of the day is shifted and it will give different prio
		assertEquals(10, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-15T03:15:24.001"), KLN));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-15T15:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-15T06:12:45.005"), MSK));
		assertEquals( 8, service.getPriority(MT("2015-03-15T05:59:59.999"), MT("2015-03-15T06:00:00.001"), MSK));
		assertEquals( 8, service.getPriority(MT("2015-03-15T02:00:00.000"), MT("2015-03-15T07:46:23.008"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-15T19:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-15T16:12:45.005"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T02:33:02.860"), MT("1265-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-16T02:44:03.012"), MSK));
		assertEquals( 6, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-20T16:12:45.005"), MSK));
		assertEquals( 6, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-11T03:19:53.295"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-18T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-05T07:12:43.508"), MSK));
		assertEquals( 5, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-03-21T02:38:25.441"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-02-21T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-08-30T09:40:51.678"), MSK));
		assertEquals( 4, service.getPriority(MT("2015-03-15T01:15:28.000"), MT("2015-12-21T02:38:25.441"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1265-05-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("1996-01-01T23:19:26.062"), MT("1999-12-31T23:59:59.097"), MSK));
		assertEquals( 3, service.getPriority(MT("2000-05-10T01:25:45.762"), MT("2009-11-21T11:00:15.982"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1266-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("1986-01-01T23:19:26.062"), MT("1999-12-31T23:59:59.097"), MSK));
		assertEquals( 2, service.getPriority(MT("2009-05-10T01:25:45.762"), MT("2018-11-21T11:00:15.982"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1277-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("1896-01-01T23:19:26.062"), MT("1905-12-31T23:59:59.097"), MSK));
		assertEquals( 1, service.getPriority(MT("2009-05-10T01:25:45.762"), MT("2345-11-21T11:00:15.982"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("1365-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_MSecs_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("1001-01-01T23:19:26.062"), MT("2205-12-31T23:59:59.097"), MSK));
		assertEquals( 0, service.getPriority(MT("2005-01-01T23:19:26.062"), MT("5105-12-31T23:59:59.097"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:33:02.860"), MT("2265-02-15T13:42:04.112"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevIsNotDefined() {
		assertEquals(16, service.getPriority(null, MT("2018-11-30T00:00:01"), MSK));
		assertEquals(16, service.getPriority(null, MT("2018-11-30T18:26:49"), MSK));
		assertEquals(16, service.getPriority(null, MT("2018-11-30T23:59:59"), MSK));
		assertEquals(16, service.getPriority(null, MT("2018-11-30T01:00:06"), MSK));
		
		assertEquals(16, service.getPriority(null, MT("1265-02-15T13:42:04"), MSK));
		assertEquals(16, service.getPriority(null, MT("1265-02-15T13:42:01"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevIsSameWithCurr() {
		assertEquals(16, service.getPriority(MT("2018-11-30T18:26:49"), MT("2018-11-30T18:26:49"), MSK));
		
		assertEquals(16, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T13:42:04"), MSK));
	}

	@Test
	public void testPrioritizer_Secs_PrevInSameMinute() {
		assertEquals(16, service.getPriority(MT("2018-11-30T18:26:49"), MT("2018-11-30T18:26:55"), MSK));
		assertEquals(16, service.getPriority(MT("1972-08-25T15:34:27"), MT("1972-08-25T15:34:56"), MSK));
		
		assertEquals(16, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T13:42:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherMinute() {
		assertEquals(15, service.getPriority(MT("2018-11-30T18:22:49"), MT("2018-11-30T18:23:00"), MSK));
		assertEquals(15, service.getPriority(MT("2018-11-30T18:22:49"), MT("2018-11-30T18:24:00"), MSK));
		assertEquals(15, service.getPriority(MT("2018-11-30T18:22:49"), MT("2018-11-30T18:24:05"), MSK));
		
		assertEquals(15, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother5Mins() {
		assertEquals(14, service.getPriority(MT("2018-11-30T18:22:49"), MT("2018-11-30T18:26:00"), MSK));
		assertEquals(14, service.getPriority(MT("2018-11-30T18:01:00"), MT("2018-11-30T18:07:09"), MSK));
		
		assertEquals(14, service.getPriority(MT("1265-02-15T13:32:04"), MT("1265-02-15T13:37:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother10Mins() {
		assertEquals(13, service.getPriority(MT("2018-11-30T18:01:15"), MT("2018-11-30T18:13:47"), MSK));
		assertEquals(13, service.getPriority(MT("2018-11-30T18:16:28"), MT("2018-11-30T18:24:50"), MSK));
		
		assertEquals(13, service.getPriority(MT("1265-02-15T13:32:04"), MT("1265-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother15Mins() {
		assertEquals(12, service.getPriority(MT("2018-11-30T18:11:00"), MT("2018-11-30T18:29:59"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T13:49:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother30Mins() {
		assertEquals(11, service.getPriority(MT("1995-06-12T20:19:01"), MT("1995-06-12T20:32:12"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:12:04"), MT("1265-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("1995-06-12T18:19:01"), MT("1995-06-12T20:13:42"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T14:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("2015-10-01T12:30:00"), MT("2015-10-01T16:45:03"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T16:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2015-10-01T12:30:00"), MT("2015-10-01T23:45:12"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-15T19:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2015-10-01T02:15:00"), MT("2015-10-01T23:45:12"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T10:42:04"), MT("1265-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2015-10-01T02:15:00"), MT("2015-10-02T02:22:38"), MSK));
		assertEquals( 6, service.getPriority(MT("2015-10-01T02:15:00"), MT("2015-10-09T12:18:50"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-17T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2015-10-01T02:15:00"), MT("2015-10-12T05:14:51"), MSK));
		assertEquals( 5, service.getPriority(MT("2015-10-01T02:15:00"), MT("2015-10-24T05:14:51"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-02-24T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2015-10-01T02:15:00"), MT("2015-12-12T15:33:21"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:42:04"), MT("1265-04-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2015-10-01T02:15:00"), MT("2018-01-25T08:19:45"), MSK));
		assertEquals( 3, service.getPriority(MT("2015-10-01T02:15:00"), MT("2019-12-31T23:59:59"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:42:04"), MT("1268-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2015-10-01T02:15:00"), MT("2020-01-25T08:19:45"), MSK));
		assertEquals( 2, service.getPriority(MT("2015-10-01T02:15:00"), MT("2045-01-25T08:19:45"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:42:04"), MT("1288-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2015-10-01T02:15:00"), MT("2101-01-25T08:19:45"), MSK));
		assertEquals( 1, service.getPriority(MT("2015-10-01T02:15:00"), MT("2562-01-25T08:19:45"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:42:04"), MT("1365-02-15T13:44:28"), MSK));
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:42:04"), MT("1565-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Secs_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2015-10-01T02:15:00"), MT("3101-01-25T08:19:45"), MSK));
		assertEquals( 0, service.getPriority(MT("2015-10-01T02:15:00"), MT("5101-01-25T08:19:45"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:42:04"), MT("2002-02-15T13:44:28"), MSK));
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:42:04"), MT("5265-02-15T13:44:28"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevIsNotDefined() {
		assertEquals(15, service.getPriority(null, MT("1997-12-01T23:04:00"), MSK));
		assertEquals(15, service.getPriority(null, MT("2018-11-30T00:01:00"), MSK));
		assertEquals(15, service.getPriority(null, MT("2018-11-30T18:26:00"), MSK));
		assertEquals(15, service.getPriority(null, MT("2018-11-30T23:59:00"), MSK));
		assertEquals(15, service.getPriority(null, MT("2018-11-30T01:01:00"), MSK));
		
		assertEquals(15, service.getPriority(null, MT("1265-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevIsSameWithCurr() {
		assertEquals(15, service.getPriority(MT("1997-12-01T23:04:00"), MT("1997-12-01T23:04:00"), MSK));
		
		assertEquals(15, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInSame5Mins() {
		assertEquals(15, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:01:00"), MSK));
		assertEquals(15, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:02:00"), MSK));
		assertEquals(15, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:03:00"), MSK));
		assertEquals(15, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:04:00"), MSK));
		
		assertEquals(15, service.getPriority(MT("1265-02-15T13:41:00"), MT("1265-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnother5Mins() {
		assertEquals(14, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:09:00"), MSK));
		
		assertEquals(14, service.getPriority(MT("1265-02-15T13:32:00"), MT("1265-02-15T13:37:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnother10Mins() {
		assertEquals(13, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:13:00"), MSK));
		
		assertEquals(13, service.getPriority(MT("1265-02-15T13:08:00"), MT("1265-02-15T13:12:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnother15Mins() {
		assertEquals(12, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:17:00"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-15T13:48:00"), MSK));
	}

	@Test
	public void testPrioritizer_Mins_PrevInAnother30Mins() {
		assertEquals(11, service.getPriority(MT("1997-12-01T23:02:00"), MT("1997-12-01T23:56:00"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:12:00"), MT("1265-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("1997-12-01T21:02:00"), MT("1997-12-01T23:56:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-15T14:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("1997-12-01T20:02:00"), MT("1997-12-01T23:56:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-15T16:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("1997-12-01T17:28:00"), MT("1997-12-01T23:56:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-15T18:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-01T23:56:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T03:44:00"), MT("1265-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-07T23:56:00"), MSK));
		assertEquals( 6, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-09T15:24:00"), MSK));
		assertEquals( 6, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-10T11:17:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-18T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-11T23:56:00"), MSK));
		assertEquals( 5, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-15T17:43:00"), MSK));
		assertEquals( 5, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-20T04:33:00"), MSK));
		assertEquals( 5, service.getPriority(MT("1997-12-01T11:47:00"), MT("1997-12-22T20:19:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-02-28T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("1997-02-01T11:47:00"), MT("1997-12-15T22:31:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-03-15T13:44:00"), MSK));
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-04-15T13:44:00"), MSK));
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:44:00"), MT("1265-05-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("1997-02-01T11:47:00"), MT("1999-05-21T00:02:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:44:00"), MT("1266-02-15T13:44:00"), MSK));
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:44:00"), MT("1269-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("1987-02-01T11:47:00"), MT("1991-05-21T00:02:00"), MSK));
		assertEquals( 2, service.getPriority(MT("1977-02-01T11:47:00"), MT("1991-05-21T00:02:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:44:00"), MT("1285-02-15T13:44:00"), MSK));
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:44:00"), MT("1295-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("1587-02-01T11:47:00"), MT("1991-05-21T00:02:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:44:00"), MT("1365-02-15T13:44:00"), MSK));
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:44:00"), MT("1665-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Mins_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("1587-02-01T11:47:00"), MT("2022-05-21T00:02:00"), MSK));
		assertEquals( 0, service.getPriority(MT("1587-02-01T11:47:00"), MT("2422-03-14T10:36:00"), MSK));
		assertEquals( 0, service.getPriority(MT("1587-02-01T11:47:00"), MT("3110-07-04T05:41:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:44:00"), MT("2265-02-15T13:44:00"), MSK));
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:44:00"), MT("3265-02-15T13:44:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevIsNotDefined() {
		assertEquals(14, service.getPriority(null, MT("1997-12-01T23:05:00"), MSK));
		assertEquals(14, service.getPriority(null, MT("2018-11-30T00:05:00"), MSK));
		assertEquals(14, service.getPriority(null, MT("2018-11-30T18:25:00"), MSK));
		assertEquals(14, service.getPriority(null, MT("2018-11-30T23:35:00"), MSK));
		assertEquals(14, service.getPriority(null, MT("2018-11-30T01:55:00"), MSK));
		
		assertEquals(14, service.getPriority(null, MT("1265-02-15T13:35:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevIsSameWithCurr() {
		assertEquals(14, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:05:00"), MSK));
		
		assertEquals(14, service.getPriority(MT("1265-02-15T13:55:00"), MT("1265-02-15T13:55:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInSame10Mins() {
		assertEquals(14, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:05:00"), MSK));
		assertEquals(13, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:10:00"), MSK));
		assertEquals(13, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:10:00"), MSK));
		assertEquals(12, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:15:00"), MSK));
		assertEquals(12, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:15:00"), MSK));
		assertEquals(12, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:20:00"), MSK));
		assertEquals(12, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:25:00"), MSK));
		assertEquals(11, service.getPriority(MT("1997-12-01T23:05:00"), MT("1997-12-01T23:30:00"), MSK));
		
		assertEquals(14, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnother10Mins() {
		assertEquals(14, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:05:00"), MSK));
		assertEquals(13, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:10:00"), MSK));
		assertEquals(12, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:15:00"), MSK));
		
		assertEquals(13, service.getPriority(MT("1265-02-15T13:05:00"), MT("1265-02-15T13:10:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnother15Mins() {
		assertEquals(12, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:25:00"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:05:00"), MT("1265-02-15T13:25:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnother30Mins() {
		assertEquals(11, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:35:00"), MSK));
		assertEquals(11, service.getPriority(MT("1997-12-01T23:00:00"), MT("1997-12-01T23:45:00"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:05:00"), MT("1265-02-15T13:35:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("1997-12-01T21:00:00"), MT("1997-12-01T23:35:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:05:00"), MT("1265-02-15T14:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("1997-12-01T18:45:00"), MT("1997-12-01T23:05:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T16:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("1997-12-01T13:15:00"), MT("1997-12-01T19:05:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T23:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-01T19:05:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T10:00:00"), MT("1265-02-15T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-02T19:05:00"), MSK));
		assertEquals( 6, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-09T01:25:00"), MSK));
		assertEquals( 6, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-10T20:35:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-18T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-11T00:05:00"), MSK));
		assertEquals( 5, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-18T00:05:00"), MSK));
		assertEquals( 5, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-21T00:05:00"), MSK));
		assertEquals( 5, service.getPriority(MT("1997-12-01T03:15:00"), MT("1997-12-31T00:05:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-22T13:05:00"), MSK));
	}

	@Test
	public void testPrioritizer_5Mins_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("1997-02-01T03:15:00"), MT("1997-12-04T00:05:00"), MSK));
		assertEquals( 4, service.getPriority(MT("1997-02-01T03:15:00"), MT("1997-12-08T00:05:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-03-15T13:05:00"), MSK));
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-06-15T13:05:00"), MSK));
	}

	@Test
	public void testPrioritizer_5Mins_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("1997-02-01T03:15:00"), MT("1998-12-04T00:05:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:00:00"), MT("1267-02-15T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("1902-01-01T00:05:00"), MT("1932-01-01T00:05:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:00:00"), MT("1275-02-15T13:05:00"), MSK));
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:00:00"), MT("1286-02-15T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("1602-01-01T00:05:00"), MT("1932-01-01T00:05:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:00:00"), MT("1365-02-15T13:05:00"), MSK));
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:00:00"), MT("1465-02-15T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_5Mins_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("1902-01-01T00:05:00"), MT("2005-01-01T00:05:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:00:00"), MT("3265-02-15T13:05:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevIsNotDefined() {
		assertEquals(13, service.getPriority(null, MT("2018-05-09T13:10:00"), MSK));
		
		assertEquals(13, service.getPriority(null, MT("1265-02-15T13:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevIsSameWithCurr() {
		assertEquals(13, service.getPriority(MT("2018-05-09T13:10:00"), MT("2018-05-09T13:10:00"), MSK));
		
		assertEquals(13, service.getPriority(MT("1265-02-15T13:40:00"), MT("1265-02-15T13:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInSame15Mins() {
		assertEquals(13, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T13:10:00"), MSK));
		
		assertEquals(13, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-15T13:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnother15Mins() {
		assertEquals(12, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T13:20:00"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-15T13:50:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnother30Mins() {
		assertEquals(11, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T13:40:00"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T13:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T14:40:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T14:40:00"), MSK));
	}

	@Test
	public void testPrioritizer_10Mins_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T15:40:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T15:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T20:40:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2018-05-09T03:00:00"), MT("2018-05-09T20:40:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T03:00:00"), MT("1265-02-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-10T20:40:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-18T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-22T20:40:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-25T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-07-09T20:40:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-03-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-09T13:00:00"), MT("2019-05-09T20:40:00"), MSK));

		assertEquals( 3, service.getPriority(MT("1265-02-15T13:00:00"), MT("1269-02-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-09T13:00:00"), MT("2028-05-09T20:40:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:00:00"), MT("1295-02-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-09T13:00:00"), MT("2218-05-09T20:40:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:00:00"), MT("1365-02-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_10Mins_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-09T13:00:00"), MT("3018-05-09T20:40:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:00:00"), MT("6265-02-15T20:40:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevIsNotDefined() {
		assertEquals(10, service.getPriority(null, MT("2018-05-09T13:00:00"), MSK));
		assertEquals(12, service.getPriority(null, MT("2018-05-09T13:15:00"), MSK));
		assertEquals(11, service.getPriority(null, MT("2018-05-09T13:30:00"), MSK));
		assertEquals(12, service.getPriority(null, MT("2018-05-09T13:45:00"), MSK));
		
		assertEquals(10, service.getPriority(null, MT("1265-02-15T13:00:00"), MSK));
		assertEquals(12, service.getPriority(null, MT("1265-02-15T13:15:00"), MSK));
		assertEquals(11, service.getPriority(null, MT("1265-02-15T13:30:00"), MSK));
		assertEquals(12, service.getPriority(null, MT("1265-02-15T13:45:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevIsSameWithCurr() {
		assertEquals(12, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-09T13:15:00"), MSK));
		assertEquals(12, service.getPriority(MT("2018-05-09T13:45:00"), MT("2018-05-09T13:45:00"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T13:15:00"), MSK));
		assertEquals(12, service.getPriority(MT("1265-02-15T13:45:00"), MT("1265-02-15T13:45:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInSame30Mins() {
		assertEquals(12, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T13:15:00"), MSK));
		
		assertEquals(12, service.getPriority(MT("1265-02-15T13:00:00"), MT("1265-02-15T13:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnother30Mins() {
		assertEquals(11, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-09T13:45:00"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T13:45:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("2018-05-09T13:45:00"), MT("2018-05-09T14:15:00"), MSK));
		assertEquals(10, service.getPriority(MT("2018-05-09T13:45:00"), MT("2018-05-09T14:30:00"), MSK));
		assertEquals(10, service.getPriority(MT("2018-05-09T13:45:00"), MT("2018-05-09T14:40:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T14:15:00"), MSK));
		assertEquals(10, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T14:30:00"), MSK));
		assertEquals(10, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T14:45:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-09T15:15:00"), MSK));
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-09T15:30:00"), MSK));
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-09T15:45:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T15:15:00"), MSK));
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T15:30:00"), MSK));
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T15:45:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-09T19:15:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2018-05-09T10:15:00"), MT("2018-05-09T19:15:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T10:15:00"), MT("1265-02-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-10T19:15:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-20T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-05-17T19:15:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-02-28T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-09T13:15:00"), MT("2018-07-09T19:15:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:15:00"), MT("1265-03-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-09T13:15:00"), MT("2019-05-09T19:15:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:15:00"), MT("1267-02-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-09T13:15:00"), MT("2058-05-09T19:15:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:15:00"), MT("1275-02-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-09T13:15:00"), MT("2118-05-09T19:15:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:15:00"), MT("1765-02-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_15Mins_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-09T13:15:00"), MT("3018-05-09T19:15:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:15:00"), MT("5265-02-15T20:15:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevIsNotDefined() {
		assertEquals(11, service.getPriority(null, MT("2018-05-09T13:30:00"), MSK));
		
		assertEquals(11, service.getPriority(null, MT("1265-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevIsSameWithCurr() {
		assertEquals(11, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-05-09T13:30:00"), MSK));
		
		assertEquals(11, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInSameHour() {
		// This case is not possible because each hour has only one such time point
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherHour() {
		assertEquals(10, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-05-09T14:30:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-15T14:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-05-09T16:30:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-15T16:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-05-09T18:30:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-15T18:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2018-05-09T10:30:00"), MT("2018-05-09T13:30:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-02-15T10:30:00"), MT("1265-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-05-10T13:30:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-20T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-05-25T13:30:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-02-25T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-09T13:30:00"), MT("2018-07-09T13:30:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-02-15T13:30:00"), MT("1265-12-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-09T13:30:00"), MT("2019-05-09T13:30:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-02-15T13:30:00"), MT("1266-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-09T13:30:00"), MT("2078-05-09T13:30:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-02-15T13:30:00"), MT("1295-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-09T13:30:00"), MT("2718-05-09T13:30:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-02-15T13:30:00"), MT("1365-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_30Mins_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-09T13:30:00"), MT("5018-05-09T13:30:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-02-15T13:30:00"), MT("2265-02-15T13:30:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevIsNotDefined() {
		assertEquals(10, service.getPriority(null, MT("2018-05-09T13:00:00"), MSK));
		
		assertEquals(10, service.getPriority(null, MT("1265-05-09T13:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevIsSameWithCurr() {
		assertEquals(10, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T13:00:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-05-09T13:00:00"), MT("1265-05-09T13:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInSame3Hours() {
		assertEquals(10, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T14:00:00"), MSK));
		
		assertEquals(10, service.getPriority(MT("1265-05-09T13:00:00"), MT("1265-05-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnother3Hours() {
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T16:00:00"), MSK));
		assertEquals( 9, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T17:00:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-05-09T13:00:00"), MT("1265-05-09T16:00:00"), MSK));
		assertEquals( 9, service.getPriority(MT("1265-05-09T13:00:00"), MT("1265-05-09T17:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T19:00:00"), MSK));
		assertEquals( 8, service.getPriority(MT("2018-05-09T13:00:00"), MT("2018-05-09T20:00:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-05-09T13:00:00"), MT("1265-05-09T19:00:00"), MSK));
		assertEquals( 8, service.getPriority(MT("1265-05-09T13:00:00"), MT("1265-05-09T20:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2018-05-09T10:00:00"), MT("2018-05-09T14:00:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-05-09T10:00:00"), MT("1265-05-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-02T10:00:00"), MT("2018-05-10T14:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-05-15T10:00:00"), MT("1265-05-20T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-09T10:00:00"), MT("2018-05-18T14:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-05-09T02:00:00"), MT("1265-05-30T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-09T10:00:00"), MT("2018-06-09T14:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-05-09T10:00:00"), MT("1265-11-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-09T10:00:00"), MT("2019-05-09T14:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-05-09T10:00:00"), MT("1269-05-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-09T10:00:00"), MT("2058-05-09T14:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-05-09T10:00:00"), MT("1275-05-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-09T10:00:00"), MT("2118-05-09T14:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-05-09T10:00:00"), MT("1565-05-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_1Hour_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-09T10:00:00"), MT("3018-05-09T14:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-05-09T10:00:00"), MT("5265-05-09T14:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevIsNotDefined() {
		assertEquals( 9, service.getPriority(null, MT("2018-05-09T15:00:00"), MSK));
		
		assertEquals( 9, service.getPriority(null, MT("1265-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevIsSameWithCurr() {
		assertEquals( 9, service.getPriority(MT("2018-05-09T15:00:00"), MT("2018-05-09T15:00:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-05-09T21:00:00"), MT("1265-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInSame6Hours() {
		assertEquals( 9, service.getPriority(MT("2018-05-09T12:00:00"), MT("2018-05-09T15:00:00"), MSK));
		
		assertEquals( 9, service.getPriority(MT("1265-05-09T18:00:00"), MT("1265-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnother6Hours() {
		assertEquals( 8, service.getPriority(MT("2018-05-09T15:00:00"), MT("2018-05-09T21:00:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1265-05-09T15:00:00"), MT("1265-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2018-05-09T03:00:00"), MT("2018-05-09T21:00:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1265-05-09T03:00:00"), MT("1265-05-09T15:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-02T15:00:00"), MT("2018-05-09T15:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1265-05-11T21:00:00"), MT("1265-05-20T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-09T15:00:00"), MT("2018-05-30T15:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1265-05-01T21:00:00"), MT("1265-05-25T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-09T15:00:00"), MT("2018-06-09T15:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1265-05-09T21:00:00"), MT("1265-11-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-09T15:00:00"), MT("2019-05-09T15:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1265-05-09T21:00:00"), MT("1269-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-09T15:00:00"), MT("2038-05-09T15:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1265-05-09T21:00:00"), MT("1295-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-09T15:00:00"), MT("2118-05-09T15:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1265-05-09T21:00:00"), MT("1765-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_3Hours_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-09T15:00:00"), MT("3018-05-09T15:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1265-05-09T21:00:00"), MT("4065-05-09T21:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevIsNotDefined() {
		assertEquals( 8, service.getPriority(null, MT("3018-05-09T18:00:00"), MSK));
		
		assertEquals( 8, service.getPriority(null, MT("4065-05-09T06:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevIsSameWithCurr() {
		assertEquals( 8, service.getPriority(MT("3018-05-09T18:00:00"), MT("3018-05-09T18:00:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("4065-05-09T06:00:00"), MT("4065-05-09T06:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInSame12Hours() {
		assertEquals( 8, service.getPriority(MT("2018-05-09T12:00:00"), MT("2018-05-09T18:00:00"), MSK));
		
		assertEquals( 8, service.getPriority(MT("1065-05-09T00:00:00"), MT("1065-05-09T06:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnother12Hours() {
		assertEquals( 7, service.getPriority(MT("2018-05-09T01:00:00"), MT("2018-05-09T18:00:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1065-05-09T05:00:00"), MT("1065-05-09T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-01T01:00:00"), MT("2018-05-09T18:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1065-05-12T05:00:00"), MT("1065-05-17T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-01T01:00:00"), MT("2018-05-29T18:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1065-05-09T05:00:00"), MT("1065-05-14T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-09T01:00:00"), MT("2018-06-09T18:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1065-05-09T05:00:00"), MT("1065-10-09T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-09T01:00:00"), MT("2019-05-09T18:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-05-09T05:00:00"), MT("1067-05-09T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-09T01:00:00"), MT("2038-05-09T18:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1065-05-09T05:00:00"), MT("1095-05-09T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-09T01:00:00"), MT("2118-05-09T18:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1065-05-09T05:00:00"), MT("1665-05-09T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_6Hours_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-09T01:00:00"), MT("3018-05-09T18:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1065-05-09T05:00:00"), MT("8065-05-09T18:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevIsNotDefined() {
		assertEquals( 7, service.getPriority(null, MT("2018-05-04T12:00:00"), MSK));
		
		assertEquals( 7, service.getPriority(null, MT("1065-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevIsSameWithCurr() {
		assertEquals( 7, service.getPriority(MT("2018-05-04T12:00:00"), MT("2018-05-04T12:00:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1065-02-01T12:00:00"), MT("1065-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInSameDay() {
		assertEquals( 7, service.getPriority(MT("2018-05-04T10:00:00"), MT("2018-05-04T12:00:00"), MSK));
		
		assertEquals( 7, service.getPriority(MT("1065-02-01T02:00:00"), MT("1065-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherDay() {
		assertEquals( 6, service.getPriority(MT("2018-05-04T10:00:00"), MT("2018-05-05T12:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1065-02-01T02:00:00"), MT("1065-02-09T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-05-04T10:00:00"), MT("2018-05-11T12:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1065-02-01T02:00:00"), MT("1065-02-28T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-05-04T10:00:00"), MT("2018-06-04T12:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1065-02-01T02:00:00"), MT("1065-12-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-05-04T10:00:00"), MT("2019-05-04T12:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-02-01T02:00:00"), MT("1068-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-05-04T10:00:00"), MT("2028-05-04T12:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1065-02-01T02:00:00"), MT("1085-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-05-04T10:00:00"), MT("2118-05-04T12:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1065-02-01T02:00:00"), MT("1465-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_12Hours_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-05-04T10:00:00"), MT("3018-05-04T12:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1065-02-01T02:00:00"), MT("5065-02-01T12:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevIsNotDefined() {
		assertEquals( 6, service.getPriority(null, MT("2018-03-04T00:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(null, MT("1065-02-02T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevIsSameWithCurr() {
		assertEquals( 6, service.getPriority(MT("2018-03-04T00:00:00"), MT("2018-03-04T00:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1065-02-02T00:00:00"), MT("1065-02-02T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevInSameDecadeOfMonth() {
		assertEquals( 6, service.getPriority(MT("2018-03-01T00:00:00"), MT("2018-03-04T00:00:00"), MSK));
		
		assertEquals( 6, service.getPriority(MT("1065-02-02T00:00:00"), MT("1065-02-05T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevInAnotherDecadeOfMonth() {
		assertEquals( 5, service.getPriority(MT("2018-03-04T00:00:00"), MT("2018-03-13T00:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1065-02-02T00:00:00"), MT("1065-02-27T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevOnAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-03-04T00:00:00"), MT("2018-04-04T00:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1065-02-02T00:00:00"), MT("1065-08-02T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-03-04T00:00:00"), MT("2019-03-04T00:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-02-02T00:00:00"), MT("1067-02-02T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-03-04T00:00:00"), MT("2058-03-22T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1065-02-02T00:00:00"), MT("1095-02-28T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-03-04T00:00:00"), MT("2118-03-04T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1065-02-02T00:00:00"), MT("1465-02-02T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Days_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-03-04T00:00:00"), MT("1018-03-04T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1065-02-02T00:00:00"), MT("3065-02-02T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevIsNotDefined() {
		assertEquals( 4, service.getPriority(null, MT("2018-03-01T00:00:00"), MSK));
		assertEquals( 5, service.getPriority(null, MT("2018-03-11T00:00:00"), MSK));
		assertEquals( 5, service.getPriority(null, MT("2018-03-21T00:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(null, MT("1065-02-01T00:00:00"), MSK));
		assertEquals( 5, service.getPriority(null, MT("1065-02-11T00:00:00"), MSK));
		assertEquals( 5, service.getPriority(null, MT("1065-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevIsSameWithCurr() {
		assertEquals( 5, service.getPriority(MT("2018-03-11T00:00:00"), MT("2018-03-11T00:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1065-02-21T00:00:00"), MT("1065-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevInSameMonth() {
		assertEquals( 5, service.getPriority(MT("2018-03-11T00:00:00"), MT("2018-03-21T00:00:00"), MSK));
		
		assertEquals( 5, service.getPriority(MT("1065-02-11T00:00:00"), MT("1065-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevInAnotherMonth() {
		assertEquals( 4, service.getPriority(MT("2018-03-11T00:00:00"), MT("2018-04-11T00:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1065-02-21T00:00:00"), MT("1065-08-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-03-11T00:00:00"), MT("2019-03-11T00:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-02-21T00:00:00"), MT("1068-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-03-11T00:00:00"), MT("2028-03-11T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1065-02-21T00:00:00"), MT("1085-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-03-11T00:00:00"), MT("2118-03-11T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1065-02-21T00:00:00"), MT("1865-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_DecadeOfMonth_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-03-11T00:00:00"), MT("3018-03-11T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1065-02-21T00:00:00"), MT("4065-02-21T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevIsNotDefined() {
		assertEquals( 4, service.getPriority(null, MT("2018-07-01T00:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(null, MT("1065-03-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevIsSameWithCurr() {
		assertEquals( 4, service.getPriority(MT("2018-07-01T00:00:00"), MT("2018-07-01T00:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1065-03-01T00:00:00"), MT("1065-03-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevInSameYear() {
		assertEquals( 4, service.getPriority(MT("2018-01-01T00:00:00"), MT("2018-09-01T00:00:00"), MSK));
		
		assertEquals( 4, service.getPriority(MT("1065-03-01T00:00:00"), MT("1065-12-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevInAnotherYear() {
		assertEquals( 3, service.getPriority(MT("2018-01-01T00:00:00"), MT("2019-09-01T00:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-03-01T00:00:00"), MT("1069-12-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-01-01T00:00:00"), MT("2028-09-01T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1065-03-01T00:00:00"), MT("1095-12-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-01-01T00:00:00"), MT("2118-09-01T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1065-03-01T00:00:00"), MT("1465-12-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Month_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-01-01T00:00:00"), MT("3018-09-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1065-03-01T00:00:00"), MT("6065-12-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Year_PrevIsNotDefined() {
		assertEquals( 3, service.getPriority(null, MT("2018-01-01T00:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(null, MT("1065-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Year_PrevIsSameWithCurr() {
		assertEquals( 3, service.getPriority(MT("2018-01-01T00:00:00"), MT("2018-01-01T00:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-01-01T00:00:00"), MT("1065-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Year_PrevInSameDecade() {
		assertEquals( 3, service.getPriority(MT("2018-01-01T00:00:00"), MT("2019-01-01T00:00:00"), MSK));
		
		assertEquals( 3, service.getPriority(MT("1065-01-01T00:00:00"), MT("1067-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Year_PrevInAnotherDecade() {
		assertEquals( 2, service.getPriority(MT("2018-01-01T00:00:00"), MT("2025-01-01T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1065-01-01T00:00:00"), MT("1097-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Year_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2018-01-01T00:00:00"), MT("2119-01-01T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1065-01-01T00:00:00"), MT("1467-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Year_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2018-01-01T00:00:00"), MT("3019-01-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1065-01-01T00:00:00"), MT("3067-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Decade_PrevIsNotDefined() {
		assertEquals( 2, service.getPriority(null, MT("2010-01-01T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(null, MT("1060-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Decade_PrevIsSameWithCurr() {
		assertEquals( 2, service.getPriority(MT("2010-01-01T00:00:00"), MT("2010-01-01T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1060-01-01T00:00:00"), MT("1060-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Decade_PrevInSameCentury() {
		assertEquals( 2, service.getPriority(MT("2010-01-01T00:00:00"), MT("2050-01-01T00:00:00"), MSK));
		
		assertEquals( 2, service.getPriority(MT("1060-01-01T00:00:00"), MT("1090-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Decade_PrevInAnotherCentury() {
		assertEquals( 1, service.getPriority(MT("2010-01-01T00:00:00"), MT("2120-01-01T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1060-01-01T00:00:00"), MT("1490-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Decade_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2010-01-01T00:00:00"), MT("3050-01-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1060-01-01T00:00:00"), MT("4090-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Century_PrevIsNotDefined() {
		assertEquals( 1, service.getPriority(null, MT("2100-01-01T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(null, MT("1100-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Century_PrevIsSameWithCurr() {
		assertEquals( 1, service.getPriority(MT("2100-01-01T00:00:00"), MT("2100-01-01T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1100-01-01T00:00:00"), MT("1100-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Century_PrevInSameMillenium() {
		assertEquals( 1, service.getPriority(MT("2100-01-01T00:00:00"), MT("2700-01-01T00:00:00"), MSK));
		
		assertEquals( 1, service.getPriority(MT("1100-01-01T00:00:00"), MT("1200-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Century_PrevInAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2100-01-01T00:00:00"), MT("3100-01-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1100-01-01T00:00:00"), MT("4100-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Millenium_PrevIsNotDefined() {
		assertEquals( 0, service.getPriority(null, MT("2000-01-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(null, MT("1000-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Millenium_PrevIsSameWithCurr() {
		assertEquals( 0, service.getPriority(MT("2000-01-01T00:00:00"), MT("2000-01-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1000-01-01T00:00:00"), MT("1000-01-01T00:00:00"), MSK));
	}
	
	@Test
	public void testPrioritizer_Millenium_PrevIsAnotherMillenium() {
		assertEquals( 0, service.getPriority(MT("2000-01-01T00:00:00"), MT("3000-01-01T00:00:00"), MSK));
		
		assertEquals( 0, service.getPriority(MT("1000-01-01T00:00:00"), MT("6000-01-01T00:00:00"), MSK));
	}

}
