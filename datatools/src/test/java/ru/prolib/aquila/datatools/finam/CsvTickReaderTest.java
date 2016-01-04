package ru.prolib.aquila.datatools.finam;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataException;

import com.csvreader.CsvReader;

public class CsvTickReaderTest {
	private IMocksControl control;
	private CsvReader csvReaderMock;
	private LocalDate date;
	private CsvTickReader reader;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		csvReaderMock = control.createMock(CsvReader.class);
		date = LocalDate.of(1998, 9, 1);
		reader = new CsvTickReader(csvReaderMock, date);
	}
	
	private InputStream createTestInput() {
		return createTestInput("<TIME>,<LAST>,<VOL>,<MILLISECONDS>\n"
			+ "100000,100.02,105,957\n"
			+ "100001,100.18,102,13\n"
			+ "100002,100.96,238,512\n");
	}
	
	private InputStream createTestInput(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}
	
	private CsvTickReader createTestReader(String data)
			throws IOException
	{
		return createTestReader(data, false);
	}
	
	private CsvTickReader createTestReader(String data, boolean skipHeader)
			throws IOException
	{
		CsvTickReader x = new CsvTickReader(createTestInput(data), date);
		if ( ! skipHeader ) {
			x.readHeader();
		}
		return x;
	}
	
	private CsvTickReader createTestReader()
			throws IOException
	{
		return createTestReader(false);
	}
	
	private CsvTickReader createTestReader(boolean skipHeader)
			throws IOException
	{
		CsvTickReader x = new CsvTickReader(createTestInput(), date);
		if ( ! skipHeader ) {
			x.readHeader();
		}
		return x;
	}
	
	@Test
	public void testCtor2() throws Exception {
		assertSame(csvReaderMock, reader.getCsvReader());
		assertEquals(date, reader.getDate());
		assertFalse(reader.isClosed());
	}
	
	@Test
	public void testClose() throws Exception {
		csvReaderMock.close();
		control.replay();
		
		reader.close();
		
		control.verify();
		assertTrue(reader.isClosed());
	}
	
	@Test
	public void testReadHeader_SkipIfNoHeaders() throws Exception {
		expect(csvReaderMock.readHeaders()).andReturn(false);
		control.replay();
		
		reader.readHeader();
		
		control.verify();
		assertFalse(reader.hasMilliseconds());
	}
	
	@Test
	public void testReadHeader_WithMillisecondsColumn() throws Exception {
		String headers[] = {  "<CHARLIE>", "<MILLISECONDS>", "<ZULU>" }; 
		expect(csvReaderMock.readHeaders()).andReturn(true);
		expect(csvReaderMock.getHeaders()).andReturn(headers);
		control.replay();
		
		reader.readHeader();
		
		control.verify();
		assertTrue(reader.hasMilliseconds());
	}

	@Test
	public void testReadHeader_WithoutMillisecondsColumn() throws Exception {
		String headers[] = {  "<CHARLIE>", "<VOL>", "<ZULU>" }; 
		expect(csvReaderMock.readHeaders()).andReturn(true);
		expect(csvReaderMock.getHeaders()).andReturn(headers);
		control.replay();
		
		reader.readHeader();
		
		control.verify();
		assertFalse(reader.hasMilliseconds());
	}
	
	@Test
	public void testNext_FalseIfClosed() throws Exception {
		reader = createTestReader();
		reader.close();
		
		assertFalse(reader.next());
	}
	
	@Test
	public void testNext_FalseIfNoMoreRecords() throws Exception {
		reader = createTestReader();
		while ( reader.next() ) { }
		
		assertTrue(reader.isClosed());
		assertFalse(reader.next());
	}
	
	@Test
	public void testNext_Ok_WithoutMillis() throws Exception {
		reader = createTestReader("<TIME>,<LAST>,<VOL>\n"
				+ "100000,100.02,105\n"
				+ "100001,100.18,102\n"
				+ "100002,100.96,238\n");
		
		assertTrue(reader.next());
		Tick exp = Tick.of(date.atTime(LocalTime.of(10, 0, 0)), 100.02, 105); 
		assertEquals(exp, reader.item());
		
		assertTrue(reader.next());
		exp = Tick.of(date.atTime(LocalTime.of(10, 0, 1)), 100.18, 102); 
		assertEquals(exp, reader.item());
	}

	@Test
	public void testNext_Ok_WithMillis() throws Exception {
		reader = createTestReader("<TIME>,<LAST>,<VOL>,<MILLISECONDS>\n"
				+ "100000,100.02,105,999\n"
				+ "100001,100.18,102,21\n"
				+ "100002,100.96,238,4\n");
		
		assertTrue(reader.next());
		Tick exp = Tick.of(date.atTime(LocalTime.of(10, 0, 0, 999000000)), 100.02, 105); 
		assertEquals(exp, reader.item());
		
		assertTrue(reader.next());
		exp = Tick.of(date.atTime(LocalTime.of(10, 0, 1,  21000000)), 100.18, 102); 
		assertEquals(exp, reader.item());
	}
	
	@Test (expected=DataException.class)
	public void testNext_ReaderException() throws Exception {
		expect(csvReaderMock.readRecord()).andThrow(new IOException("test"));
		control.replay();
		
		reader.next();
	}
	
	@Test (expected=DataException.class)
	public void testNext_TimeParseException() throws Exception {
		reader = createTestReader("<TIME>,<LAST>,<VOL>,<MILLISECONDS>\n"
				+ "zzzxxx,100.02,105,999\n");
		
		reader.next();
	}
	
	@Test (expected=DataException.class)
	public void testNext_NumberParseException() throws Exception {
		reader = createTestReader("<TIME>,<LAST>,<VOL>,<MILLISECONDS>\n"
				+ "100000,foo,105,999\n");
		
		reader.next();
	}
	
	@Test (expected=DataException.class)
	public void testItem_ThrowsBeforeStart() throws Exception {
		reader = createTestReader();
		
		reader.item();
	}
	
	@Test (expected=DataException.class)
	public void testItem_ThrowsAfterEnd() throws Exception {
		reader = createTestReader();
		while ( reader.next() ) { }

		reader.item();
	}
	
	@Test (expected=DataException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		reader = createTestReader();
		reader.next();
		reader.close();
		
		reader.item();
	}
	
	@Test
	public void testItem_WithMillis() throws Exception {
		String data = "<TIME>,<LAST>,<VOL>,<MILLISECONDS>\n"
				+ "100000,100.02,105,957\n"
				+ "100001,100.18,102,13\n"
				+ "100002,100.96,238,512\n";
		reader = createTestReader(data);
		
		Tick expected[] = {
			Tick.of(date.atTime(LocalTime.of(10, 0, 0, 957000000)), 100.02, 105),
			Tick.of(date.atTime(LocalTime.of(10, 0, 1,  13000000)), 100.18, 102),
			Tick.of(date.atTime(LocalTime.of(10, 0, 2, 512000000)), 100.96, 238),
		};
		for ( Tick e : expected ) {
			assertTrue(reader.next());
			assertEquals(e, reader.item());
		}
		reader.close();
	}
	
	@Test
	public void testItem_WithoutMillis() throws Exception {
		String data = "<TIME>,<LAST>,<VOL>\n"
				+ "100000,100.02,105\n"
				+ "100001,100.18,102\n"
				+ "100002,100.96,238\n"
				+ "100005,100.42,500\n";
		reader = createTestReader(data);
		
		Tick expected[] = {
			Tick.of(date.atTime(LocalTime.of(10, 0, 0)), 100.02, 105),
			Tick.of(date.atTime(LocalTime.of(10, 0, 1)), 100.18, 102),
			Tick.of(date.atTime(LocalTime.of(10, 0, 2)), 100.96, 238),
			Tick.of(date.atTime(LocalTime.of(10, 0, 5)), 100.42, 500),
		};
		for ( Tick e : expected ) {
			assertTrue(reader.next());
			assertEquals(e, reader.item());
		}
		reader.close();
	}

}
