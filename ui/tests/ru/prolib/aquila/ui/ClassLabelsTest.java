package ru.prolib.aquila.ui;

import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * $Id$
 */
public class ClassLabelsTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor_TwoArgs() {
		Properties props = new Properties();
		ClassLabels l = new ClassLabels("Test", props);
		assertEquals("Test", l.getName());
		assertEquals(props, l.getLabels());
	}
	
	@Test
	public void testConstructor_OneArg() {
		ClassLabels l = new ClassLabels("Test");
		assertEquals("Test", l.getName());
		IsInstanceOf.instanceOf(Properties.class).matches(l.getLabels());
	}
	
	@Test
	public void testGet_TextNotExists() {
		ClassLabels l = new ClassLabels("Test");
		assertEquals("TEST_LABEL", l.get("TEST_LABEL"));
	}
	
	@Test
	public void testGet_TextExists() {
		Properties p = new Properties();
		p.setProperty("TEST_LABEL", "Test label");
		ClassLabels l = new ClassLabels("Test", p);
		assertEquals("Test label", l.get("TEST_LABEL"));
	}

}
