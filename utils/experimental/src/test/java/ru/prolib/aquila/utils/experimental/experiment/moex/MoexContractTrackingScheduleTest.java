package ru.prolib.aquila.utils.experimental.experiment.moex;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class MoexContractTrackingScheduleTest {
	private MoexContractTrackingSchedule schedule;
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		schedule = new MoexContractTrackingSchedule();
	}
	
	@Test
	public void testIsTrackingPeriod() {
		assertFalse(schedule.isTrackingPeriod(T("2016-08-15T07:29:59.999Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T07:30:00.000Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T07:45:00.000Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T08:29:59.999Z")));
		assertFalse(schedule.isTrackingPeriod(T("2016-08-15T08:30:00.000Z")));
		
		assertFalse(schedule.isTrackingPeriod(T("2016-08-15T11:34:59.999Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T11:35:00.000Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T11:50:00.000Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T12:34:59.999Z")));
		assertFalse(schedule.isTrackingPeriod(T("2016-08-15T12:35:00.000Z")));
		
		assertFalse(schedule.isTrackingPeriod(T("2016-08-15T16:29:59.999Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T16:30:00.000Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T16:45:00.000Z")));
		assertTrue(schedule.isTrackingPeriod(T("2016-08-15T17:29:59.999Z")));
		assertFalse(schedule.isTrackingPeriod(T("2016-08-15T17:30:00.000Z")));
	}

	@Test
	public void testIsMarketOpeningTrackingPeriod() {
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T07:29:59.999Z")));
		assertTrue(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T07:30:00.000Z")));
		assertTrue(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T07:45:00.000Z")));
		assertTrue(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T08:29:59.999Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T08:30:00.000Z")));
		
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T11:34:59.999Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T11:35:00.000Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T11:50:00.000Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T12:04:59.999Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T12:05:00.000Z")));
		
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T16:29:59.999Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T16:30:00.000Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T16:45:00.000Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T16:59:59.999Z")));
		assertFalse(schedule.isMarketOpeningTrackingPeriod(T("2016-08-15T17:00:00.000Z")));
	}
	
	@Test
	public void testIsIntradayClearingTrackingPeriod() {
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T07:29:59.999Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T07:30:00.000Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T07:45:00.000Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T07:59:59.999Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T08:00:00.000Z")));
		
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T11:34:59.999Z")));
		assertTrue(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T11:35:00.000Z")));
		assertTrue(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T11:50:00.000Z")));
		assertTrue(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T12:34:59.999Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T12:35:00.000Z")));
		
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T16:29:59.999Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T16:30:00.000Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T16:45:00.000Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T16:59:59.999Z")));
		assertFalse(schedule.isIntradayClearingTrackingPeriod(T("2016-08-15T17:00:00.000Z")));
	}
	
	@Test
	public void testIsEveningClearingTrackingPeriod() {
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T07:29:59.999Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T07:30:00.000Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T07:45:00.000Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T07:59:59.999Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T08:00:00.000Z")));
		
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T11:34:59.999Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T11:35:00.000Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T11:50:00.000Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T12:04:59.999Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T12:05:00.000Z")));
		
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T16:29:59.999Z")));
		assertTrue(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T16:30:00.000Z")));
		assertTrue(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T16:45:00.000Z")));
		assertTrue(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T17:29:59.999Z")));
		assertFalse(schedule.isEveningClearingTrackingPeriod(T("2016-08-15T17:30:00.000Z")));
	}

	@Test
	public void testWithMarketOpeningTime() {
		assertEquals(T("2016-08-18T07:00:00Z"), schedule.withMarketOpeningTime(T("2016-08-18T13:45:12.271Z")));
		assertEquals(T("2016-08-18T07:00:00Z"), schedule.withMarketOpeningTime(T("2016-08-18T06:22:36.276Z")));
	}
	
	@Test
	public void testWithIntradayClearingTime() {
		assertEquals(T("2016-08-18T11:00:00Z"), schedule.withIntradayClearingTime(T("2016-08-18T19:45:12.271Z")));
		assertEquals(T("2016-08-18T11:00:00Z"), schedule.withIntradayClearingTime(T("2016-08-18T04:12:27.172Z")));
	}
	
	@Test
	public void testWithEveningClearingTime() {
		assertEquals(T("2016-08-18T15:45:00Z"), schedule.withEveningClearingTime(T("2016-08-18T19:45:12.271Z")));
		assertEquals(T("2016-08-18T15:45:00Z"), schedule.withEveningClearingTime(T("2016-08-18T04:12:27.172Z")));
	}
	
	@Test
	public void testGetNextTrackingPeriodStart() {
		assertEquals(T("2016-08-18T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-17T21:00:00.000Z")));
		assertEquals(T("2016-08-18T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T00:00:00.000Z")));
		assertEquals(T("2016-08-18T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T07:00:00.000Z")));
		assertEquals(T("2016-08-18T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T07:29:59.999Z")));
		
		assertEquals(T("2016-08-18T11:35:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T07:30:00.000Z")));
		assertEquals(T("2016-08-18T11:35:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T08:00:00.000Z")));
		assertEquals(T("2016-08-18T11:35:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T11:34:59.999Z")));
		
		assertEquals(T("2016-08-18T16:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T11:35:00.000Z")));
		assertEquals(T("2016-08-18T16:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T12:00:00.000Z")));
		assertEquals(T("2016-08-18T16:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T16:29:59.999Z")));
		
		assertEquals(T("2016-08-19T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T16:30:00.000Z")));
		assertEquals(T("2016-08-19T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T18:00:00.000Z")));
		assertEquals(T("2016-08-19T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T21:00:00.000Z")));
		assertEquals(T("2016-08-19T07:30:00Z"), schedule.getNextTrackingPeriodStart(T("2016-08-18T23:59:59.999Z")));
	}
	
	@Test
	public void testGetNextUpdateTime() {
		assertEquals(T("2016-08-18T14:32:18.765Z"), schedule.getNextUpdateTime(T("2016-08-18T14:31:18.765Z")));
		assertEquals(T("2016-08-18T15:45:00.000Z"), schedule.getNextUpdateTime(T("2016-08-18T15:44:00.000Z")));
	}

}
