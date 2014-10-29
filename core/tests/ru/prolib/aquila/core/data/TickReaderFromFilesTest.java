package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.io.File;
import java.util.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.data.finam.FinamTickReaderFactory;

public class TickReaderFromFilesTest {
	private IMocksControl control;
	private Aqiterator<File> fileset;
	private Aqiterator<Tick> ticks;
	private TickReaderFactory factory;
	private TickReaderFromFiles reader;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		ticks = control.createMock(Aqiterator.class);
		fileset = control.createMock(Aqiterator.class);
		factory = control.createMock(TickReaderFactory.class);
		reader = new TickReaderFromFiles(fileset, factory);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testClose() throws Exception {
		fileset = control.createMock(Aqiterator.class);
		reader = new TickReaderFromFiles(fileset, factory);
		fileset.close();
		control.replay();
		
		reader.close();
		
		control.verify();
	}
	
	@Test
	public void testClose_ClosesCurrentReader() throws Exception {
		List<File> list = new Vector<File>();
		list.add(new File("/home/work/1.csv"));
		list.add(new File("/home/work/2.csv"));
		reader = new TickReaderFromFiles(new SimpleIterator<File>(list), factory);
		
		expect(factory.createTickReader(eq("/home/work/1.csv"))).andReturn(ticks);
		expect(ticks.next()).andReturn(true);
		ticks.close();
		control.replay();
		
		reader.next();
		reader.close();
		reader.close();
		
		control.verify();
	}
	
	@Test
	public void testIterate() throws Exception {
		List<File> list = new Vector<File>();
		final String basePath = "fixture/csv-storage/ticks/2014/";
		list.add(new File(basePath + "02/GAZP-EQBR-RUR-STK-20140201.csv"));
		list.add(new File(basePath + "10/GAZP-EQBR-RUR-STK-20141005.csv.gz"));
		list.add(new File(basePath + "10/GAZP-EQBR-RUR-STK-20141014.csv"));
		reader = new TickReaderFromFiles(new SimpleIterator<File>(list),
				new FinamTickReaderFactory());
		
		List<Tick> actual = new Vector<Tick>(),
				expected = new Vector<Tick>();
		expected.add(new Tick(new DateTime(2014,  2,  1, 10, 0, 0, 0), 143d, 1d));
		expected.add(new Tick(new DateTime(2014, 10,  5, 10, 0, 0, 0), 148d, 1d));
		expected.add(new Tick(new DateTime(2014, 10, 14, 10, 0, 1, 0), 145.7d, 1d));
		expected.add(new Tick(new DateTime(2014, 10, 14, 10, 0, 2, 0), 145.64d, 8d));
		
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		assertEquals(expected, actual);
	}

}
