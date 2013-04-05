package ru.prolib.aquila.ChaosTheory;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PropsTest {
	Props props;

	@Before
	public void setUp() throws Exception {
		props = new PropsImpl();
	}
	
	@Test
	public void testGetInt1_Ok() throws Exception {
		props.setString("zulu", "12345");
		assertEquals(12345, props.getInt("zulu"));
	}
	
	@Test (expected=PropsNotExistsException.class)
	public void testGetInt1_ThrowsNotExists() throws Exception {
		props.getInt("zulu");
	}
	
	@Test (expected=PropsFormatException.class)
	public void testGetInt1_ThrowsFormatException() throws Exception {
		props.setString("zulu", "111.222");
		props.getInt("zulu");
	}
	
	@Test
	public void testGetInt2_Ok() throws Exception {
		props.setString("foo", "1234567");
		assertEquals(1234567, props.getInt("foo", 111));
	}
	
	@Test
	public void testGetInt2_Default() throws Exception {
		assertEquals(111, props.getInt("foo", 111));
	}
	
	@Test (expected=PropsFormatException.class)
	public void testGetInt2_ThrowsFormatException() throws Exception {
		props.setString("foo", "error");
		props.getInt("foo", 111);
	}
	
	@Test (expected=PropsNotExistsException.class)
	public void testGetDouble1_ThrowsNotExists() throws Exception {
		props.getDouble("foobar");
	}
	
	@Test (expected=PropsFormatException.class)
	public void testGetDouble1_ThrowsFormatException() throws Exception {
		props.setString("foobar", "herbert");
		props.getDouble("foobar");
	}
	
	@Test
	public void testGetDouble1_Ok() throws Exception {
		props.setString("foobar", "1.25d");
		assertEquals(1.25d, props.getDouble("foobar"), 0.01d);
	}
	
	@Test
	public void testGetDouble2_Ok() throws Exception {
		props.setString("zulu4", "12.34");
		assertEquals(12.34d, props.getDouble("zulu4", 22.33d), 0.01d);
	}
	
	@Test
	public void testGetDouble2_Default() throws Exception {
		assertEquals(18.34d, props.getDouble("gotcha", 18.34d), 0.01d);
	}
	
	@Test (expected=PropsFormatException.class)
	public void testGetDouble2_ThrowsFormatException() throws Exception {
		props.setString("buggi", "kappa");
		props.getDouble("buggi", 876.54d);
	}
	
	@Test
	public void testGetString1_Ok() throws Exception {
		props.setString("zulu", "charlie");
		assertEquals("charlie", props.getString("zulu"));
	}
	
	@Test (expected=PropsNotExistsException.class)
	public void testGetString1_ThrowsNotExists() throws Exception {
		props.getString("zulu");
	}
	
	@Test
	public void testGetString2_Ok() throws Exception {
		props.setString("bird", "thunder");
		assertEquals("thunder", props.getString("bird", "swallow"));
	}
	
	@Test
	public void testGetString2_Default() throws Exception {
		assertEquals("fly", props.getString("butter", "fly"));
	}

}
