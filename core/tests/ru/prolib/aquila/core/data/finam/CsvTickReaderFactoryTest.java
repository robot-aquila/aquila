package ru.prolib.aquila.core.data.finam;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.data.*;

public class CsvTickReaderFactoryTest {
	private IMocksControl control;
	private Finam facade;
	private CsvTickReaderFactory factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(Finam.class);
		factory = new CsvTickReaderFactory(facade);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTickReader() throws Exception {
		Aqiterator<Tick> iterator = control.createMock(Aqiterator.class);
		expect(facade.createTickReader(eq("hello.csv"))).andReturn(iterator);
		control.replay();
		
		assertSame(iterator, factory.createTickReader("hello.csv"));
		
		control.verify();
	}

	@Test (expected=DataException.class)
	public void testCreateTickReader_ThrowsIfFacadeThrows() throws Exception {
		expect(facade.createTickReader(eq("boomba.csv")))
			.andThrow(new IOException("test error"));
		control.replay();
		
		factory.createTickReader("boomba.csv");
	}

}
