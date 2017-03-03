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
			.withToken(SecurityField.TICK_VALUE, new FMoney("1", "RUB"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70469.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76343.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73406.0d)
			.withToken(SecurityField.INITIAL_MARGIN, 5874.0d)
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-24T11:00:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70533.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76411.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73472.0d)
			.withToken(SecurityField.INITIAL_MARGIN, 5878.0d)
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-25T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70454.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76326.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73390.0d)
			.withToken(SecurityField.INITIAL_MARGIN, 5872.0d)
			.buildUpdate());		
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-26T11:00:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, 70650.0d)
			.withToken(SecurityField.UPPER_PRICE_LIMIT, 76538.0d)
			.withToken(SecurityField.SETTLEMENT_PRICE, 73594.0d)
			.withToken(SecurityField.INITIAL_MARGIN, 5888.0d)
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-26T15:45:00Z")
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
