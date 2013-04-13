package ru.prolib.aquila.ui;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * $Id$
 */
public class UiTextsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	/**
	 * Test method for {@link ru.prolib.aquila.ui.UiTexts#UiTexts(java.lang.String)}.
	 */
	@Test
	public void testConstructor_WithArgs() {
		UiTexts t = new UiTexts("ru_RU");
		assertEquals("en_US", t.getDefLang());
		String[] path = t.getLocalesPath();
		assertEquals("shared", path[0]);
		assertEquals("lang", path[1]);
		assertEquals("ru_RU", t.getLang());
	}
	
	/**
	 * Test method for {@link ru.prolib.aquila.ui.UiTexts#UiTexts()}.
	 */
	@Test
	public void testConstructor_WithoutArgs() {
		UiTexts t = new UiTexts();
		assertEquals("en_US", t.getDefLang());
		String[] path = t.getLocalesPath();
		assertEquals("shared", path[0]);
		assertEquals("lang", path[1]);
		assertNull(t.getLang());
		
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.UiTexts#load()}.
	 */
	@Test
	public void testLoad_DefaultLocale() {
		UiTexts t = new UiTexts();
		t.setLocalesPath(new String[] {"fixture", "lang"});
		t.load();
		Map<String, ClassLabels> lbs = t.getLabels();
		assertEquals(2, lbs.size());
		ClassLabels l = lbs.get("FirstTest");
		assertEquals("Entry one", l.get("ENT_ONE"));
		assertEquals("Entry two", l.get("ENT_TWO"));
		assertEquals("Entry three", l.get("ENT_THREE"));
		assertEquals("Some here", lbs.get("SecondTest").get("SOME"));
	}
	
	/**
	 * Test method for {@link ru.prolib.aquila.ui.UiTexts#load()}.
	 */
	@Test
	public void testLoad_RuLocale() {
		UiTexts t = new UiTexts("ru_RU");
		t.setLocalesPath(new String[] {"fixture", "lang"});
		t.load();
		Map<String, ClassLabels> lbs = t.getLabels();
		assertEquals(2, lbs.size());
		ClassLabels l = lbs.get("FirstTest");
		assertEquals("Первая", l.get("ENT_ONE"));
		assertEquals("Entry two", l.get("ENT_TWO"));
		assertEquals("Третья", l.get("ENT_THREE"));
		assertEquals("Some here", lbs.get("SecondTest").get("SOME"));
		assertEquals("Другое", lbs.get("SecondTest").get("ANOTHER"));
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.UiTexts#get(java.lang.String)}.
	 */
	@Test
	public void testGet() {
		UiTexts t = new UiTexts();
		t.setLocalesPath(new String[] {"fixture", "lang"});
		t.load();
		ClassLabels l = t.get("FirstTest");
		assertEquals("FirstTest", l.getName());
		assertEquals("Entry one", l.get("ENT_ONE"));
		
		l = t.get("Unexist");
		assertEquals("Unexist", l.getName());
		assertEquals("SOME_KEY", l.get("SOME_KEY"));
		
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.UiTexts#setClassLabels(java.lang.String, ru.prolib.aquila.ui.ClassLabels)}.
	 */
	@Test
	public void testSetClassLabels() {
		UiTexts t = new UiTexts();
		ClassLabels l = new ClassLabels("SomeName");
		t.setClassLabels("SomeName", l);
		assertEquals(l, t.get("SomeName"));
	}

}
