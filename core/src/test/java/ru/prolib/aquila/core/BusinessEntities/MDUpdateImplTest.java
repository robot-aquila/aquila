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
		source.addRecord(Tick.ofAsk(time2, CDecimalBD.of("12.34"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		source.addRecord(Tick.ofBid(time3, CDecimalBD.of("14.29"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		source.addRecord(Tick.ofAsk(time2, CDecimalBD.of("12.35"), CDecimalBD.of(200L)), MDTransactionType.REPLACE);
		
		MDUpdate actual = source.withTime(time4);
		
		expected = new MDUpdateImpl(new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time4, new Symbol("GAZP")));
		expected.addRecord(Tick.ofAsk(time4, CDecimalBD.of("12.34"), CDecimalBD.of(100L)), MDTransactionType.ADD);
		expected.addRecord(Tick.ofBid(time4, CDecimalBD.of("14.29"), CDecimalBD.of(800L)), MDTransactionType.ADD);
		expected.addRecord(Tick.ofAsk(time4, CDecimalBD.of("12.35"), CDecimalBD.of(200L)), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}

}
