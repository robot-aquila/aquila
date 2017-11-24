package ru.prolib.aquila.web.utils.finam.datasim;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.file.FileConfig;
import ru.prolib.aquila.data.storage.file.SymbolFileStorage;
import ru.prolib.aquila.data.storage.file.SymbolFileStorageImpl;

public class FinamSeamlessL1UpdateReaderTest {
	private static final Symbol symbol = new Symbol("BR-3.17");
	private SymbolFileStorage storage;
	private FinamSeamlessL1UpdateReader reader;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		storage = new SymbolFileStorageImpl(new File("fixture"), "TEST", new FileConfig(".csv.gz", null));
	}
	
	@Test
	public void testIterate_AllData() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamSeamlessL1UpdateReader(storage, symbol, T("2016-01-01T00:00:00Z"), 2);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();
		
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol).withTrade();
		List<L1Update> expected = new ArrayList<>();
		expected.add(builder.withTime("2016-09-22T07:45:11Z")
			.withPrice("49.76")
			.withSize(1)
			.buildL1Update());
		expected.add(builder.withTime("2016-09-22T07:45:11Z")
			.withPrice("49.78")
			.withSize(3)
			.buildL1Update());
		expected.add(builder.withTime("2016-09-22T07:45:11Z")
			.withPrice("50.00")
			.withSize(1)
			.buildL1Update());
		expected.add(builder.withTime("2016-09-22T07:45:11Z")
			.withPrice("50.00")
			.withSize(42)
			.buildL1Update());
		expected.add(builder.withTime("2016-09-22T08:31:38Z")
			.withPrice("49.18")
			.withSize(1)
			.buildL1Update());
		expected.add(builder.withTime("2016-09-22T09:29:39Z")
			.withPrice("49.16")
			.withSize(2)
			.buildL1Update());
		expected.add(builder.withTime("2016-09-22T12:16:24Z")
			.withPrice("49.69")
			.withSize(2)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-04T07:43:10Z")
			.withPrice("52.16")
			.withSize(1)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-04T13:13:05Z")
			.withPrice("52.82")
			.withSize(1)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-04T13:25:12Z")
			.withPrice("52.97")
			.withSize(5)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-12T07:42:13Z")
			.withPrice("54.58")
			.withSize(2)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-12T13:15:29Z")
			.withPrice("54.00")
			.withSize(1)
			.buildL1Update());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_FromStartTime() throws Exception {
		List<L1Update> actual = new ArrayList<>();
		reader = new FinamSeamlessL1UpdateReader(storage, symbol, T("2016-10-04T10:00:00Z"), 2);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();

		List<L1Update> expected = new ArrayList<>();
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol).withTrade();
		expected.add(builder.withTime("2016-10-04T13:13:05Z")
			.withPrice("52.82")
			.withSize(1)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-04T13:25:12Z")
			.withPrice("52.97")
			.withSize(5)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-12T07:42:13Z")
			.withPrice("54.58")
			.withSize(2)
			.buildL1Update());
		expected.add(builder.withTime("2016-10-12T13:15:29Z")
			.withPrice("54.00")
			.withSize(1)
			.buildL1Update());
		assertEquals(expected, actual);
	}

	@Test (expected=IOException.class)
	public void testNext_ThrowsAfterClose() throws Exception {
		reader = new FinamSeamlessL1UpdateReader(storage, symbol, T("2016-10-04T10:00:00Z"), 2);
		reader.close();

		reader.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsAfterClose() throws Exception {
		reader = new FinamSeamlessL1UpdateReader(storage, symbol, T("2016-10-04T10:00:00Z"), 2);
		reader.close();
		
		reader.item();
	}
	
}
