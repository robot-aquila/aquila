package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.data.storage.file.PtmlFactory;

public class MoexContractSymbolUpdateReaderTest {
	private MoexContractSymbolUpdateReader reader;
		
	private static CloseableIterator<DeltaUpdate> createReader() throws Exception {
		 return new PtmlFactory(new MoexContractPtmlConverter())
		 	.createReader(new File("fixture/03/Eu%2D9%2E16/Eu%2D9%2E16-moex-contract-details-daily.txt"));
	}

	@Before
	public void setUp() throws Exception {
		reader = new MoexContractSymbolUpdateReader(createReader());
	}
	
	@After
	public void tearDown() throws Exception {
		reader.close();
	}

	@Test
	public void testIterate() throws Exception {
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-08-23T15:45:00Z")
			.withToken(SecurityField.DISPLAY_NAME, "Eu-9.16")
			.withToken(SecurityField.LOT_SIZE, 1000)
			.withToken(SecurityField.TICK_SIZE, new FDecimal("1"))
			.withToken(SecurityField.TICK_VALUE, new FMoney("1", 2, "RUB"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("70469"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("76343"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("73406"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5874", 2, "RUB"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-24T11:00:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("70533"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("76411"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("73472"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5878", 2, "RUB"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-25T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("70454"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("76326"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("73390"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5872", 2, "RUB"))
			.buildUpdate());		
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-26T11:00:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, new FDecimal("70650"))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, new FDecimal("76538"))
			.withToken(SecurityField.SETTLEMENT_PRICE, new FDecimal("73594"))
			.withToken(SecurityField.INITIAL_MARGIN, new FMoney("5888", 2, "RUB"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-26T15:45:00Z")
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
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsAfterClose() throws Exception {
		reader.close();
		
		reader.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsAfterClose() throws Exception {
		reader.close();
		
		reader.item();
	}

}
