package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import static org.junit.Assert.*;
import static java.time.LocalTime.of;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class MTFLabelMapperImplTest {	
	private ZoneId MSK, UTC;
	private List<MTFLabel> labels1, labels2;
	private MTFLabelMapperImpl service;

	@Before
	public void setUp() throws Exception {
		MSK = ZoneId.of("Europe/Moscow");
		UTC = ZoneId.of("UTC");
		labels1 = new ArrayList<>();
		labels1.add(new MTFLabel(of(10,  0), "10h", true));
		labels1.add(new MTFLabel(of(10,  5), ":05", false));
		labels1.add(new MTFLabel(of(10, 10), ":10", false));
		labels1.add(new MTFLabel(of(10, 15), ":15", false));
		labels2 = new ArrayList<>();
		labels2.add(new MTFLabel(of(0,  0),  "0h", true));
		labels2.add(new MTFLabel(of(0, 15), ":15", false));
		labels2.add(new MTFLabel(of(0, 30), ":30", false));
		labels2.add(new MTFLabel(of(0, 45), ":45", false));
		service = null;
	}
	
	private Instant atMSK(String timeString) {
		return ZonedDateTime.of(LocalDateTime.parse(timeString), MSK).toInstant();
	}
	
	@Test
	public void testConvertToLabel() {
		service = new MTFLabelMapperImpl(MSK, labels1);
		
		assertNull(service.convertToLabel(atMSK("2018-02-01T00:00:00")));
		assertEquals(labels1.get(0), service.convertToLabel(atMSK("2018-02-01T10:00:00")));
		assertEquals(labels1.get(1), service.convertToLabel(atMSK("2018-02-17T10:05:00")));
		assertNull(service.convertToLabel(atMSK("2018-02-17T10:05:01")));
		assertEquals(labels1.get(2), service.convertToLabel(atMSK("2018-02-17T10:10:00")));
		assertEquals(labels1.get(1), service.convertToLabel(atMSK("2018-01-01T10:05:00")));
		assertEquals(labels1.get(3), service.convertToLabel(atMSK("2018-01-02T10:15:00")));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		service = new MTFLabelMapperImpl(MSK, labels1);
		
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		service = new MTFLabelMapperImpl(MSK, labels1);
		Variant<ZoneId> vZone = new Variant<>(MSK, UTC);
		Variant<List<MTFLabel>> vLabels = new Variant<>(vZone, labels1, labels2);
		Variant<?> iterator = vLabels;
		int foundCnt = 0;
		MTFLabelMapperImpl x, found = null;
		do {
			x = new MTFLabelMapperImpl(vZone.get(), vLabels.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(MSK, found.getZoneID());
		assertEquals(labels1, found.getLabels());
	}

}
