package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class MoexSymbolUpdateReaderFactoryTest {
	private MoexSymbolUpdateReaderFactory factory;
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		factory = new MoexSymbolUpdateReaderFactory("fixture");
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
			.withToken(SecurityField.LOT_SIZE, CDecimalBD.of(1000L))
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of(1L))
			.withToken(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("1"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(69895L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(75721L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(72808L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5826"))
			.withToken(SecurityField.EXPIRATION_TIME, Instant.parse("2016-09-14T21:00:00Z"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-09-13T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(70424L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(76294L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(73359L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5870"))
			.buildUpdate());
		assertEquals(expected, actual);
	}

}
