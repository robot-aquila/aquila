package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class MoexSusReaderFactoryTest {
	private MoexSusReaderFactory factory;
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		factory = new MoexSusReaderFactory("fixture");
	}

	@Test
	public void testCreateReader() throws Exception {
		List<DeltaUpdate> actual = new ArrayList<>();
		try ( CloseableIterator<DeltaUpdate> dummy =
				factory.createReader(new Symbol("Eu-9.16"), T("2016-08-26T15:45:00.001Z")) )
		{
			while ( dummy.next() ) {
				actual.add(dummy.item());
			}
		}
		
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-08-26T15:45:00.001Z")
			.withToken(SecurityField.DISPLAY_NAME, "Eu-9.16")
			.withToken(SecurityField.LOT_SIZE, 1000)
			.withToken(SecurityField.TICK_SIZE, 1.0d)
			.withToken(SecurityField.SCALE, 0)
			.withToken(SecurityField.TICK_VALUE, 1.0d)
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 69895.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 75721.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 72808.0d)
			.withToken(SecurityField.INITIAL_MARGIN, 5826.0d)
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-09-13T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70424.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76294.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73359.0d)
			.withToken(SecurityField.INITIAL_MARGIN, 5870.0d)
			.buildUpdate());
		assertEquals(expected, actual);
	}

}
