package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import org.easymock.IMocksControl;
import org.junit.*;

public class PropsTest {
	private IMocksControl control;
	private Properties p;
	private Props props;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		p = new Properties();
		props = new Props(p);
	}
	
	@Test
	public void testGetters() throws Exception {
		p.setProperty("integer", "27");
		p.setProperty("double", "14.23");
		p.setProperty("string", "zulu24");
		
		assertEquals(new Integer(27), props.getInteger("integer"));
		assertEquals(new Double(14.23d), props.getDouble("double"));
		assertEquals("zulu24", props.getString("string"));
	}
	
	@Test
	public void testGettersWithDefaultValues() throws Exception {
		assertEquals(new Integer(43), props.getInteger("integer", 43));
		assertEquals(new Double(8.12d), props.getDouble("double", 8.12d));
		assertEquals("gamma", props.getString("string", "gamma"));
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testGetInteger_ThrowsIfNoValue() throws Exception {
		props.getInteger("integer");
	}
	
	@Test (expected=ValueFormatException.class)
	public void testGetInteger1_ThrowsFormatException() throws Exception {
		p.setProperty("gamma", "foo24");
		props.getInteger("gamma");
	}
	
	@Test (expected=ValueFormatException.class)
	public void testGetInteger2_ThrowsFormatException() throws Exception {
		p.setProperty("gamma", "zoo");
		props.getInteger("gamma", 115);
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testGetDouble_ThrowsIfNoValue() throws Exception {
		props.getDouble("double");
	}
	
	@Test (expected=ValueFormatException.class)
	public void testGetDouble1_ThrowsFormatException() throws Exception {
		p.setProperty("jungle", "24.13indigo");
		props.getDouble("jungle");
	}
	
	@Test (expected=ValueFormatException.class)
	public void testGetDouble2_ThrowsFormatException() throws Exception {
		p.setProperty("bakhta", "1.1bambarbiya");
		props.getDouble("bakhta", 59.12d);
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testGetString_ThrowsIfNoValue() throws Exception {
		props.getString("string");
	}
	
	@Test (expected=DataException.class)
	public void testLoad_RethrowsIOException() throws Exception {
		Reader reader = control.createMock(Reader.class);
		p = control.createMock(Properties.class);
		p.load(reader);
		expectLastCall().andThrow(new IOException("test error"));
		control.replay();
		props = new Props(p);
		
		props.load(reader);
	}

	@Test
	public void testLoad() throws Exception {
		Reader reader = control.createMock(Reader.class);
		p = control.createMock(Properties.class);
		p.load(reader);
		control.replay();
		props = new Props(p);
		
		props.load(reader);
		
		control.verify();
	}

}
