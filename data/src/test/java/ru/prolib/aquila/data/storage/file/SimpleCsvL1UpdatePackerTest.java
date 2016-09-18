package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateImpl;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.data.storage.file.SimpleCsvL1FormatException;
import ru.prolib.aquila.data.storage.file.SimpleCsvL1UpdatePacker;

public class SimpleCsvL1UpdatePackerTest {
	private static Symbol symbol1 = new Symbol("F:RTS@SPBFUT:USD");
	private static Symbol symbol2 = new Symbol("S:AAPL@TX:USD");
	private SimpleCsvL1UpdatePacker packer;

	@Before
	public void setUp() throws Exception {
		packer = new SimpleCsvL1UpdatePacker();
	}
	
	@Test
	public void testPack() throws Exception {
		L1Update
			update1 = new L1UpdateImpl(symbol1, Tick.of(TickType.ASK,
				Instant.parse("2015-02-10T23:49:59.445Z"), 123400.0d, 100L)),
			update2 = new L1UpdateImpl(symbol2, Tick.of(TickType.BID,
				Instant.parse("2016-01-01T00:00:00.012Z"), 78.13d, 5L)),
			update3 = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE,
				Instant.parse("2016-02-18T11:31:15Z"), 132200.0d, 250L));
		String
			expected1 = "A,F:RTS@SPBFUT:USD,2015-02-10T23:49:59.445Z,123400.0,100",
			expected2 = "B,S:AAPL@TX:USD,2016-01-01T00:00:00.012Z,78.13,5",
			expected3 = "T,F:RTS@SPBFUT:USD,2016-02-18T11:31:15Z,132200.0,250";
		
		assertEquals(expected1, packer.pack(update1));
		assertEquals(expected2, packer.pack(update2));
		assertEquals(expected3, packer.pack(update3));
	}
	
	@Test (expected=SimpleCsvL1FormatException.class)
	public void testUnpack_ThrowsInvalidNumberOfFields() throws Exception {
		packer.unpack("one,two,three,four");
	}
	
	@Test (expected=SimpleCsvL1FormatException.class)
	public void testUnpack_ThrowsBadTickTypeCode() throws Exception {
		packer.unpack("X,AAPL,2016-02-18T11:39:00Z,10.0,10");
	}
	
	@Test (expected=SimpleCsvL1FormatException.class)
	public void testUnpack_ThrowsBadSymbolFormat() throws Exception {
		packer.unpack("A,AAPL:BAPL:MAPL:DAPL,2016-02-18T11:23:10Z,5.0,1");
	}
	
	@Test (expected=SimpleCsvL1FormatException.class)
	public void testUnpack_ThrowsBadTimestampFormat() throws Exception {
		packer.unpack("A,AAPL,foobar,5.0,1");
	}

	@Test (expected=SimpleCsvL1FormatException.class)
	public void testUnpack_ThrowsBadPriceFormat() throws Exception {
		packer.unpack("A,AAPL,2016-02-18T11:23:10Z,foo,1");
	}
	
	@Test (expected=SimpleCsvL1FormatException.class)
	public void testUnpack_ThrowsBadSizeFormat() throws Exception {
		packer.unpack("A,AAPL,2016-02-18T11:23:10Z,5.0,foo");
	}

	@Test
	public void testUnpack() throws Exception {
		String
			record1 = "A,F:RTS@SPBFUT:USD,2015-02-10T23:49:59.445Z,123400.0,100",
			record2 = "B,S:AAPL@TX:USD,2016-01-01T00:00:00.012Z,78.13,5",
			record3 = "T,F:RTS@SPBFUT:USD,2016-02-18T11:31:15Z,132200.0,250";
		L1Update
			expected1 = new L1UpdateImpl(symbol1, Tick.of(TickType.ASK,
				Instant.parse("2015-02-10T23:49:59.445Z"), 123400.0d, 100L)),
			expected2 = new L1UpdateImpl(symbol2, Tick.of(TickType.BID,
				Instant.parse("2016-01-01T00:00:00.012Z"), 78.13d, 5L)),
			expected3 = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE,
				Instant.parse("2016-02-18T11:31:15Z"), 132200.0d, 250L));
		
		assertEquals(expected1, packer.unpack(record1));
		assertEquals(expected2, packer.unpack(record2));
		assertEquals(expected3, packer.unpack(record3));
	}

}
