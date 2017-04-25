package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class QFSessionScheduleTest {
	private QFSessionSchedule schedule;

	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@Before
	public void setUp() throws Exception {
		schedule = new QFSessionSchedule();
	}
	
	@Test
	public void testGetCurrentPeriod() {
		assertEquals(QFSessionSchedule.VPZ, schedule.getCurrentPeriod(T("2017-04-25T03:00:00Z")));
		assertEquals(QFSessionSchedule.VPZ, schedule.getCurrentPeriod(T("2017-04-25T06:59:59.999Z")));
		assertEquals(QFSessionSchedule.PCVM1_1, schedule.getCurrentPeriod(T("2017-04-25T07:00:00Z")));
		assertEquals(QFSessionSchedule.PCVM1_1, schedule.getCurrentPeriod(T("2017-04-25T10:59:59.999Z")));
		assertEquals(QFSessionSchedule.VP1, schedule.getCurrentPeriod(T("2017-04-25T11:00:00Z")));
		assertEquals(QFSessionSchedule.VP1, schedule.getCurrentPeriod(T("2017-04-25T11:03:59.999Z")));
		assertEquals(QFSessionSchedule.PIC, schedule.getCurrentPeriod(T("2017-04-25T11:04:00.000Z")));
		assertEquals(QFSessionSchedule.PIC, schedule.getCurrentPeriod(T("2017-04-25T11:04:59.999Z")));
		assertEquals(QFSessionSchedule.PCVM2, schedule.getCurrentPeriod(T("2017-04-25T11:05:00.000Z")));
		assertEquals(QFSessionSchedule.PCVM2, schedule.getCurrentPeriod(T("2017-04-25T12:00:00.000Z")));
		assertEquals(QFSessionSchedule.PCVM2, schedule.getCurrentPeriod(T("2017-04-25T15:44:59.999Z")));
		assertEquals(QFSessionSchedule.VP2, schedule.getCurrentPeriod(T("2017-04-25T15:45:00.000Z")));
		assertEquals(QFSessionSchedule.VP2, schedule.getCurrentPeriod(T("2017-04-25T15:50:00.000Z")));
		assertEquals(QFSessionSchedule.VP2, schedule.getCurrentPeriod(T("2017-04-25T15:58:59.999Z")));
		assertEquals(QFSessionSchedule.PMC, schedule.getCurrentPeriod(T("2017-04-25T15:59:00.000Z")));
		assertEquals(QFSessionSchedule.PMC, schedule.getCurrentPeriod(T("2017-04-25T15:59:59.999Z")));
		assertEquals(QFSessionSchedule.PCVM1_2, schedule.getCurrentPeriod(T("2017-04-25T16:00:00.000Z")));
		assertEquals(QFSessionSchedule.PCVM1_2, schedule.getCurrentPeriod(T("2017-04-25T20:49:59.999Z")));
		assertEquals(QFSessionSchedule.VP3, schedule.getCurrentPeriod(T("2017-04-25T20:50:00.00Z")));
		assertEquals(QFSessionSchedule.VP3, schedule.getCurrentPeriod(T("2017-04-25T20:59:59.999Z")));
	}
	
	@Test
	public void testGetCurrentProc() {
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T03:00:00Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T06:59:59.999Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T07:00:00Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T10:59:59.999Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T11:00:00Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T11:03:59.999Z")));
		assertEquals(QFSessionProc.MID_CLEARING, schedule.getCurrentProc(T("2017-04-25T11:04:00.000Z")));
		assertEquals(QFSessionProc.MID_CLEARING, schedule.getCurrentProc(T("2017-04-25T11:04:59.999Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T11:05:00.000Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T12:00:00.000Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T15:44:59.999Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T15:45:00.000Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T15:50:00.000Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T15:58:59.999Z")));
		assertEquals(QFSessionProc.CLEARING, schedule.getCurrentProc(T("2017-04-25T15:59:00.000Z")));
		assertEquals(QFSessionProc.CLEARING, schedule.getCurrentProc(T("2017-04-25T15:59:59.999Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T16:00:00.000Z")));
		assertEquals(QFSessionProc.UPDATE_BY_MARKET, schedule.getCurrentProc(T("2017-04-25T20:49:59.999Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T20:50:00.00Z")));
		assertEquals(null, schedule.getCurrentProc(T("2017-04-25T20:59:59.999Z")));
	}

	@Test
	public void testGetNextRunTime() {
		assertEquals(T("2017-04-25T07:00:00Z"), schedule.getNextRunTime(T("2017-04-25T03:00:00Z")));
		assertEquals(T("2017-04-25T07:00:00Z"), schedule.getNextRunTime(T("2017-04-25T06:59:59.999Z")));
		assertEquals(T("2017-04-25T07:00:05Z"), schedule.getNextRunTime(T("2017-04-25T07:00:00Z")));
		assertEquals(T("2017-04-25T07:00:10Z"), schedule.getNextRunTime(T("2017-04-25T07:00:05Z")));
		assertEquals(T("2017-04-25T11:04:00Z"), schedule.getNextRunTime(T("2017-04-25T10:59:59.999Z")));
		assertEquals(T("2017-04-25T11:04:00Z"), schedule.getNextRunTime(T("2017-04-25T11:00:00Z")));
		assertEquals(T("2017-04-25T11:04:00Z"), schedule.getNextRunTime(T("2017-04-25T11:03:59.999Z")));
		assertEquals(T("2017-04-25T11:05:00Z"), schedule.getNextRunTime(T("2017-04-25T11:04:00.000Z")));
		assertEquals(T("2017-04-25T11:05:00Z"), schedule.getNextRunTime(T("2017-04-25T11:04:59.999Z")));
		assertEquals(T("2017-04-25T11:05:05Z"), schedule.getNextRunTime(T("2017-04-25T11:05:00.000Z")));
		assertEquals(T("2017-04-25T11:05:10Z"), schedule.getNextRunTime(T("2017-04-25T11:05:05.000Z")));
		assertEquals(T("2017-04-25T12:00:05Z"), schedule.getNextRunTime(T("2017-04-25T12:00:00.000Z")));
		assertEquals(T("2017-04-25T15:59:00Z"), schedule.getNextRunTime(T("2017-04-25T15:44:59.999Z")));
		assertEquals(T("2017-04-25T15:59:00Z"), schedule.getNextRunTime(T("2017-04-25T15:45:00.000Z")));
		assertEquals(T("2017-04-25T15:59:00Z"), schedule.getNextRunTime(T("2017-04-25T15:50:00.000Z")));
		assertEquals(T("2017-04-25T15:59:00Z"), schedule.getNextRunTime(T("2017-04-25T15:58:59.999Z")));
		assertEquals(T("2017-04-25T16:00:00Z"), schedule.getNextRunTime(T("2017-04-25T15:59:00.000Z")));
		assertEquals(T("2017-04-25T16:00:00Z"), schedule.getNextRunTime(T("2017-04-25T15:59:59.999Z")));
		assertEquals(T("2017-04-25T16:00:05Z"), schedule.getNextRunTime(T("2017-04-25T16:00:00.000Z")));
		assertEquals(T("2017-04-25T16:00:10Z"), schedule.getNextRunTime(T("2017-04-25T16:00:05.000Z")));
		assertEquals(T("2017-04-25T20:49:55Z"), schedule.getNextRunTime(T("2017-04-25T20:49:50.000Z")));
		assertEquals(T("2017-04-26T07:00:00Z"), schedule.getNextRunTime(T("2017-04-25T20:49:59.999Z")));
		assertEquals(T("2017-04-26T07:00:00Z"), schedule.getNextRunTime(T("2017-04-25T20:50:00.00Z")));
		assertEquals(T("2017-04-26T07:00:00Z"), schedule.getNextRunTime(T("2017-04-25T20:59:59.999Z")));
	}

}
