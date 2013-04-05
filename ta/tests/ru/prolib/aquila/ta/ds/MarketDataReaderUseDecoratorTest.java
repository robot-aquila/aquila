package ru.prolib.aquila.ta.ds;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;


public class MarketDataReaderUseDecoratorTest {
	IMocksControl control;
	DataSetIterator iterator;
	DataSetIteratorDecorator decorator;
	MarketDataReader realReader;
	MarketDataReaderUseDecorator reader;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		iterator = control.createMock(DataSetIterator.class);
		decorator = new DataSetIteratorDecorator();
		realReader = control.createMock(MarketDataReader.class);
		reader = new MarketDataReaderUseDecorator(realReader, decorator);
	}
	
	@Test
	public void testConstructor2() throws Exception {
		assertSame(realReader, reader.getReader());
		assertSame(decorator, reader.getDecorator());
	}
	
	@Test
	public void testConstructor1() throws Exception {
		reader = new MarketDataReaderUseDecorator(realReader);
		assertSame(realReader, reader.getReader());
		DataSetIteratorDecorator d = reader.getDecorator();
		assertNotNull(d);
	}
	
	@Test
	public void testPrepare() throws Exception {
		expect(realReader.prepare()).andReturn(iterator);
		control.replay();
		
		assertSame(decorator, reader.prepare());
		assertSame(iterator, decorator.getDataSetIterator());
		
		control.verify();
	}
	
	@Test
	public void testUpdate() throws Exception {
		expect(realReader.update()).andReturn(iterator);
		control.replay();
		
		assertSame(decorator, reader.update());
		assertSame(iterator, decorator.getDataSetIterator());
		
		control.verify();
	}

}
