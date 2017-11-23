package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheSegmentReader;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheUtils;

public class CacheSegmentReaderTest {
	private BufferedReader readerStub;
	private CacheUtils utils;
	private CacheSegmentReader iterator;
	
	@Before
	public void setUp() throws Exception {
		readerStub = new BufferedReader(new StringReader(
				  "2017-01-01T10:00:00Z,100.0,120.01,100.0,115.63,1200\n"
				+ "2017-01-01T10:01:00Z,115.63,117.92,113.14,114.0,5500\n"
				+ "2017-01-01T10:05:00Z,107.13,108.01,105.0,106.0,2300\n"));
		utils = CacheUtils.getInstance();
		iterator = new CacheSegmentReader(readerStub, ZTFrame.M1, utils);
	}
	
	@Test
	public void testIterator() throws Exception {
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		expected.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-01-01T10:00:00Z")
				.withOpenPrice("100.0")
				.withHighPrice("120.01")
				.withLowPrice("100.0")
				.withClosePrice("115.63")
				.withVolume(1200L)
				.buildCandle());
		expected.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-01-01T10:01:00Z")
				.withOpenPrice("115.63")
				.withHighPrice("117.92")
				.withLowPrice("113.14")
				.withClosePrice("114.0")
				.withVolume(5500L)
				.buildCandle());
		expected.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-01-01T10:05:00Z")
				.withOpenPrice("107.13")
				.withHighPrice("108.01")
				.withLowPrice("105.0")
				.withClosePrice("106.0")
				.withVolume(2300L)
				.buildCandle());
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsIfClosed() throws Exception {
		iterator.close();
		
		iterator.next();
	}

	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		iterator.next();
		iterator.close();
		
		iterator.item();
	}

}
