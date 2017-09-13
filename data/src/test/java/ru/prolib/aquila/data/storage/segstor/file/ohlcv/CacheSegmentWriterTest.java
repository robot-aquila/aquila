package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CacheSegmentWriterTest {
	private static final File temporary, committed;
	
	static {
		temporary = new File("fixture/tmp/cache.tmp");
		committed = new File("fixture/tmp/cache.dat");
	}
	
	private IMocksControl control;
	private Writer baseWriterMock;
	private CacheSegmentWriter writer;

	@Before
	public void setUp() throws Exception {
		FileUtils.write(temporary, "test data");
		control = createStrictControl();
		baseWriterMock = control.createMock(Writer.class);
		writer = new CacheSegmentWriter(temporary, committed, baseWriterMock);
	}
	
	@After
	public void tearDown() throws Exception {
		temporary.delete();
		committed.delete();
	}
	
	@Test
	public void testCtor() {
		assertEquals(temporary, writer.getTemporary());
		assertEquals(committed, writer.getCommitted());
	}
	
	@Test
	public void testAppend_1C() throws Exception {
		expect(baseWriterMock.append('2')).andReturn(baseWriterMock);
		control.replay();
		
		assertSame(writer, writer.append('2'));
		
		control.verify();
	}
	
	@Test
	public void testAppend_1CSq() throws Exception {
		CharSequence csqMock = control.createMock(CharSequence.class);
		expect(baseWriterMock.append(csqMock)).andReturn(baseWriterMock);
		control.replay();
		
		assertSame(writer, writer.append(csqMock));
		
		control.verify();
	}
	
	@Test
	public void testAppend_3CSqII() throws Exception {
		CharSequence csqMock = control.createMock(CharSequence.class);
		expect(baseWriterMock.append(csqMock, 100, 200)).andReturn(baseWriterMock);
		control.replay();
		
		assertSame(writer, writer.append(csqMock, 100, 200));
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		baseWriterMock.close();
		control.replay();
		
		writer.close();
		
		control.verify();
		assertFalse(temporary.exists());
		assertTrue(committed.exists());
		assertEquals("test data", FileUtils.readFileToString(committed));
	}
	
	@Test
	public void testClose_IfCommitedExists() throws Exception {
		FileUtils.write(committed, "existing data");
		baseWriterMock.close();
		control.replay();
		
		writer.close();
		
		control.verify();
		assertFalse(temporary.exists());
		assertTrue(committed.exists());
		assertEquals("test data", FileUtils.readFileToString(committed));
	}
	
	@Test
	public void testClose_ClosingTwiceHasNoEffect() throws Exception {
		baseWriterMock.close();
		control.replay();
		
		writer.close();
		writer.close();
		
		control.verify();
	}
	
	@Test
	public void testFlush() throws Exception {
		baseWriterMock.flush();
		control.replay();
		
		writer.flush();
		
		control.verify();
	}
	
	@Test
	public void testWrite_3CbII() throws Exception {
		char[] arg = new char[10];
		baseWriterMock.write(aryEq(arg), eq(100), eq(50));
		control.replay();
		
		writer.write(arg, 100, 50);
		
		control.verify();
	}
	
	@Test
	public void testWrite_1Cb() throws Exception {
		char[] arg = new char[15];
		baseWriterMock.write(aryEq(arg));
		control.replay();
		
		writer.write(arg);
		
		control.verify();		
	}
	
	@Test
	public void testWrite_1I() throws Exception {
		baseWriterMock.write(25);
		control.replay();
		
		writer.write(25);
		
		control.verify();
	}
	
	@Test
	public void testWrite_1S() throws Exception {
		baseWriterMock.write("zulu25");
		control.replay();
		
		writer.write("zulu25");
		
		control.verify();
	}
	
	@Test
	public void testWrite_3SII() throws Exception {
		baseWriterMock.write("foobar", 12, 24);
		control.replay();
		
		writer.write("foobar", 12, 24);
		
		control.verify();
	}

}
