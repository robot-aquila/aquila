package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class ServiceBuilderHelperImplTest {
	HierarchicalStreamReader reader;
	ServiceBuilderHelperImpl helper;

	@Before
	public void setUp() throws Exception {
		reader = createMock(HierarchicalStreamReader.class);
		helper = new ServiceBuilderHelperImpl();
	}
	
	@Test
	public void testGetAttribute_Ok() throws Exception {
		expect(reader.getAttribute("foo")).andReturn("bar");
		replay(reader);
		
		assertEquals("bar", helper.getAttribute("foo", reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderAttributeNotExistsException.class)
	public void testGetAttribute_ThrowsNotExists() throws Exception {
		expect(reader.getAttribute("bar")).andReturn(null);
		replay(reader);
		
		helper.getAttribute("bar", reader);
	}
	
	@Test
	public void testGetInt2_Ok() throws Exception {
		expect(reader.getAttribute("foo")).andReturn("100500");
		replay(reader);
		
		assertEquals(100500, helper.getInt("foo", reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderAttributeNotExistsException.class)
	public void testGetInt2_ThrowsNotFound() throws Exception {
		expect(reader.getAttribute("foo")).andReturn(null);
		replay(reader);
		
		helper.getInt("foo", reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetInt2_ThrowsFormatException() throws Exception {
		expect(reader.getAttribute("foo")).andReturn("param");
		replay(reader);
		
		helper.getInt("foo", reader);
	}
	
	@Test
	public void testGetInt3_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("123");
		replay(reader);
		
		assertEquals(123, helper.getInt("bar", 567, reader));
		
		verify(reader);
	}
	
	@Test
	public void testGetInt3_DefaultValue() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		assertEquals(555, helper.getInt("zulu", 555, reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetInt3_ThrowsFormatException() throws Exception {
		expect(reader.getAttribute("waw")).andReturn("param");
		replay(reader);
		
		helper.getInt("waw", 12345, reader);
	}
	
	@Test
	public void testGetInt1_Ok() throws Exception {
		expect(reader.getValue()).andReturn("555");
		replay(reader);
		
		assertEquals(555, helper.getInt(reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetInt1_ThrowsFormatException() throws Exception {
		expect(reader.getValue()).andReturn("zulu");
		replay(reader);
		
		helper.getInt(reader);
	}
	
	@Test
	public void testGetLong2_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("8827272727277211112");
		replay(reader);
		
		assertEquals(8827272727277211112L, helper.getLong("bar", reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderAttributeNotExistsException.class)
	public void testGetLong2_ThrowsNotFound() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		helper.getLong("zulu", reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetLong2_ThrowsFormatException() throws Exception {
		expect(reader.getAttribute("foo")).andReturn("param");
		replay(reader);
		
		helper.getLong("foo", reader);
	}
	
	@Test
	public void testGetLong3_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("-8827272727277211112");
		replay(reader);
		
		assertEquals(-8827272727277211112L, helper.getLong("bar", 567, reader));
		
		verify(reader);
	}
	
	@Test
	public void testGetLong3_DefaultValue() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		assertEquals(123, helper.getLong("zulu", 123, reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetLong3_ThrowsFormatException() throws Exception {
		expect(reader.getAttribute("waw")).andReturn("zuko");
		replay(reader);
		
		helper.getLong("waw", 12345, reader);
	}
	
	@Test
	public void testGetLong1_Ok() throws Exception {
		expect(reader.getValue()).andReturn("555");
		replay(reader);
		
		assertEquals(555, helper.getLong(reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetLong1_ThrowsFormatException() throws Exception {
		expect(reader.getValue()).andReturn("555.666");
		replay(reader);
		
		helper.getLong(reader);
	}


	@Test
	public void testGetDouble2_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("727.272");
		replay(reader);
		
		assertEquals(727.272d, helper.getDouble("bar", reader), 0.001d);
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderAttributeNotExistsException.class)
	public void testGetDouble2_ThrowsNotFound() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		helper.getDouble("zulu", reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetDouble2_ThrowsFormatException() throws Exception {
		expect(reader.getAttribute("foo")).andReturn("param.test");
		replay(reader);
		
		helper.getDouble("foo", reader);
	}
	
	@Test
	public void testGetDouble3_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("-88.213");
		replay(reader);
		
		assertEquals(-88.213, helper.getDouble("bar", 567, reader), 0.001d);
		
		verify(reader);
	}
	
	@Test
	public void testGetDouble3_DefaultValue() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		assertEquals(12.55d, helper.getDouble("zulu", 12.55d, reader), 0.001d);
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetDouble3_ThrowsFormatException() throws Exception {
		expect(reader.getAttribute("waw")).andReturn("param");
		replay(reader);
		
		helper.getDouble("waw", 12345, reader);
	}
	
	@Test
	public void testGetDouble1_Ok() throws Exception {
		expect(reader.getValue()).andReturn("555.123");
		replay(reader);
		
		assertEquals(555.123d, helper.getDouble(reader), 0.001d);
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderFormatException.class)
	public void testGetDouble1_ThrowsFormatException() throws Exception {
		expect(reader.getValue()).andReturn("+-=!555_666");
		replay(reader);
		
		helper.getDouble(reader);
	}

	@Test
	public void testGetString2_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("semaphore");
		replay(reader);
		
		assertEquals("semaphore", helper.getString("bar", reader));
		
		verify(reader);
	}
	
	@Test (expected=ServiceBuilderAttributeNotExistsException.class)
	public void testGetString2_ThrowsNotFound() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		helper.getString("zulu", reader);
	}
	
	@Test
	public void testGetString3_Ok() throws Exception {
		expect(reader.getAttribute("bar")).andReturn("kukaracha");
		replay(reader);
		
		assertEquals("kukaracha", helper.getString("bar", "default", reader));
		
		verify(reader);
	}
	
	@Test
	public void testGetString3_DefaultValue() throws Exception {
		expect(reader.getAttribute("zulu")).andReturn(null);
		replay(reader);
		
		assertEquals("test", helper.getString("zulu", "test", reader));
		
		verify(reader);
	}
	
	@Test
	public void testGetString1_Ok() throws Exception {
		expect(reader.getValue()).andReturn("kalimantan");
		replay(reader);
		
		assertEquals("kalimantan", helper.getString(reader));
		
		verify(reader);
	}
	
	@Test
	public void testGetProps_Ok() throws Exception {
		String[][] pairs = {
			{ "Name", "Bond, James Bond" },	
			{ "Planet", "Earth" },
			{ "Age", "100" },
			{ "State", "perfecto" }
		};
		for ( int i = 0; i < pairs.length; i ++ ) {
			expect(reader.hasMoreChildren()).andReturn(true);
			reader.moveDown();
			expect(reader.getNodeName()).andReturn(pairs[i][0]);
			expect(reader.getValue()).andReturn(pairs[i][1]);
			reader.moveUp();
		}
		expect(reader.hasMoreChildren()).andReturn(false);
		replay(reader);
		
		Props props = helper.getProps(reader);
		
		verify(reader);
		assertEquals(pairs.length, props.size());
		for ( int i = 0; i < pairs.length; i ++ ) {
			assertEquals(pairs[i][1], props.getString(pairs[i][0]));
		}
	}

}
