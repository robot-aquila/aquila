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
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
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
			.withToken(SecurityField.LOT_SIZE, 1000)
			.withToken(SecurityField.TICK_SIZE, new FDecimal("1"))
			.withToken(SecurityField.TICK_VALUE, new FMoney("1", 2, "RUB"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("69895"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("75721"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("72808"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5826", 2, "RUB"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-09-13T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("70424"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("76294"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("73359"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5870", 2, "RUB"))
			.buildUpdate());
		assertEquals(expected, actual);
	}

}
