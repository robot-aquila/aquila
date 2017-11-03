package ru.prolib.aquila.web.utils.moex;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
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
			.withToken(SecurityField.LOT_SIZE, CDecimalBD.of(1000L))
			.withToken(SecurityField.TICK_SIZE, CDecimalBD.of(1L))
			.withToken(SecurityField.TICK_VALUE, CDecimalBD.ofRUB2("1"))
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(70469L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(76343L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(73406L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5874"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-24T11:00:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(70533L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(76411L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(73472L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5878"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-25T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(70454L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(76326L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(73390L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5872"))
			.buildUpdate());		
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-26T11:00:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(70650L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(76538L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(73594L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5888"))
			.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-08-26T15:45:00Z")
			.withToken(SecurityField.LOWER_PRICE_LIMIT, CDecimalBD.of(69895L))
			.withToken(SecurityField.UPPER_PRICE_LIMIT, CDecimalBD.of(75721L))
			.withToken(SecurityField.SETTLEMENT_PRICE, CDecimalBD.of(72808L))
			.withToken(SecurityField.INITIAL_MARGIN, CDecimalBD.ofRUB2("5826"))
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
