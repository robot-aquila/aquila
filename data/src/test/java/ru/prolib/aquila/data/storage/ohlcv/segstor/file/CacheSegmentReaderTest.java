package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheSegmentReader;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheUtils;

public class CacheSegmentReaderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private Interval interval1, interval2, interval3;
	private BufferedReader readerStub;
	private CacheUtils utils;
	private CacheSegmentReader iterator;
	
	@Before
	public void setUp() throws Exception {
		interval1 = Interval.of(T("2017-01-01T10:00:00Z"), Duration.ofMinutes(1));
		interval2 = Interval.of(T("2017-01-01T10:01:00Z"), Duration.ofMinutes(1));
		interval3 = Interval.of(T("2017-01-01T10:05:00Z"), Duration.ofMinutes(1));
		readerStub = new BufferedReader(new StringReader(
				  "2017-01-01T10:00:00Z,100.0,120.01,100.0,115.63,1200\n"
				+ "2017-01-01T10:01:00Z,115.63,117.92,113.14,114.0,5500\n"
				+ "2017-01-01T10:05:00Z,107.13,108.01,105.0,106.0,2300\n"));
		utils = CacheUtils.getInstance();
		iterator = new CacheSegmentReader(readerStub, TimeFrame.M1, utils);
	}
	
	@Test
	public void testIterator() throws Exception {
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		expected.add(new Candle(interval1, 100.0, 120.01, 100.0, 115.63, 1200));
		expected.add(new Candle(interval2, 115.63, 117.92, 113.14, 114.0, 5500));
		expected.add(new Candle(interval3, 107.13, 108.01, 105.0, 106.0, 2300));
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
