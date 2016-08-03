package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class MDUpdateImplTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testWithTime() {
		Instant time1 = Instant.parse("1978-06-02T13:45:00Z"),
			time2 = Instant.parse("1934-01-19T00:00:00Z"),
			time3 = Instant.parse("1947-12-10T00:00:00Z"),
			time4 = Instant.parse("2015-03-24T10:40:01Z");
		MDUpdateImpl source, expected;
		source = new MDUpdateImpl(new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time1, new Symbol("GAZP")));
		source.addRecord(Tick.of(TickType.ASK, time2, 12.34d, 100), MDTransactionType.ADD);
		source.addRecord(Tick.of(TickType.BID, time3, 14.29d, 800), MDTransactionType.ADD);
		source.addRecord(Tick.of(TickType.ASK, time2, 12.35d, 200), MDTransactionType.REPLACE);
		
		MDUpdate actual = source.withTime(time4);
		
		expected = new MDUpdateImpl(new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time4, new Symbol("GAZP")));
		expected.addRecord(Tick.of(TickType.ASK, time4, 12.34d, 100), MDTransactionType.ADD);
		expected.addRecord(Tick.of(TickType.BID, time4, 14.29d, 800), MDTransactionType.ADD);
		expected.addRecord(Tick.of(TickType.ASK, time4, 12.35d, 200), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}

}
