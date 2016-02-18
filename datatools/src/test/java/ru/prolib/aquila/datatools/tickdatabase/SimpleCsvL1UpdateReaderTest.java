package ru.prolib.aquila.datatools.tickdatabase;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.BufferedReader;
import java.io.IOException;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SimpleCsvL1UpdateReaderTest {
	private IMocksControl control;
	private BufferedReader readerMock;
	private SimpleCsvL1UpdatePacker packerMock;
	private SimpleCsvL1UpdateReader updateReader;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		readerMock = control.createMock(BufferedReader.class);
		packerMock = control.createMock(SimpleCsvL1UpdatePacker.class);
		updateReader = new SimpleCsvL1UpdateReader(readerMock, packerMock);
	}
	
	@Test
	public void testCtor() throws Exception {
		assertNull(updateReader.getUpdate());
	}
	
	@Test
	public void testClose() throws Exception {
		readerMock.close();
		control.replay();
		
		updateReader.close();
		
		control.verify();
	}
	
	@Test
	public void testNextUpdate_HasUpdate() throws Exception {
		L1Update update = control.createMock(L1Update.class);
		expect(readerMock.readLine()).andReturn("packed line");
		expect(packerMock.unpack("packed line")).andReturn(update);
		control.replay();
		
		assertTrue(updateReader.nextUpdate());
		
		control.verify();
		assertSame(update, updateReader.getUpdate());
	}
	
	@Test
	public void testNextUpdate_NoMoreUpdates() throws Exception {
		expect(readerMock.readLine()).andReturn(null);
		control.replay();
		
		assertFalse(updateReader.nextUpdate());
		
		control.verify();
	}

	@Test (expected=IOException.class)
	public void testNextUpdate_ThrowsPackerException() throws Exception {
		expect(readerMock.readLine()).andReturn("some data");
		expect(packerMock.unpack("some data")).andThrow(new SimpleCsvL1FormatException("test error"));
		control.replay();
		
		updateReader.nextUpdate();
	}

}
