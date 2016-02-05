package ru.prolib.aquila.core.text;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.text.Messages;

public class MessagesTest {

	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@After
	public void tearDown() {
		Messages.removeLoader("ResourceTest");
		Messages.removeLoader("FirstTest");
	}
	
	@Test
	public void testCtor0() {
		Messages t = new Messages();
		assertEquals("en_US", t.getLang());
		assertEquals(new File("shared", "lang"), t.getRootFolder());
	}
	
	@Test
	public void testCtor1_String() {
		Messages t = new Messages("ru_RU");
		assertEquals("ru_RU", t.getLang());
		assertEquals(new File("shared", "lang"), t.getRootFolder());
	}
		
	@Test
	public void testCtor1_File() {
		Messages t = new Messages(new File("fixture", "lang"));
		assertEquals("en_US", t.getLang());
		assertEquals(new File("fixture", "lang"), t.getRootFolder());
	}
	
	@Test
	public void testCtor2() {
		Messages t = new Messages(new File("fixture", "lang"), "ru_RU");
		assertEquals("ru_RU", t.getLang());
		assertEquals(new File("fixture", "lang"), t.getRootFolder());
	}

	@Test
	public void testLoad_DefaultLocale() {
		Messages t = new Messages(new File("fixture", "lang"));
		assertEquals("Entry one", t.get(new MsgID("FirstTest", "ENT_ONE")));
		assertEquals("Entry two", t.get(new MsgID("FirstTest", "ENT_TWO")));
		assertEquals("Entry three", t.get(new MsgID("FirstTest", "ENT_THREE")));
		assertEquals("Some here", t.get(new MsgID("SecondTest", "SOME")));
	}
	
	@Test
	public void testLoad_RuLocale() {
		Messages t = new Messages(new File("fixture", "lang"), "ru_RU");
		assertEquals("Первая", t.get(new MsgID("FirstTest", "ENT_ONE")));
		assertEquals("Entry two", t.get(new MsgID("FirstTest", "ENT_TWO")));
		assertEquals("Третья", t.get(new MsgID("FirstTest", "ENT_THREE")));
		assertEquals("Some here", t.get(new MsgID("SecondTest", "SOME")));
		assertEquals("Другое", t.get(new MsgID("SecondTest", "ANOTHER")));
	}
	
	@Test
	public void testLoad_FromResource() {
		Messages.registerLoader("ResourceTest", getClass().getClassLoader());
		Messages messages = new Messages(new File("fixture", "lang"));
		assertEquals("Text 1", messages.get(new MsgID("ResourceTest", "MARKER1")));
		assertEquals("Text 2", messages.get(new MsgID("ResourceTest", "MARKER2")));
	}
	
	@Test
	public void testLoad_ResourceOverridenByFile() {
		Messages.registerLoader("FirstTest", getClass().getClassLoader());
		Messages messages = new Messages(new File("fixture", "lang"));
		assertNotEquals("Resource string 1", messages.get(new MsgID("FirstTest", "ENT_ONE")));
		assertNotEquals("Resource string 2", messages.get(new MsgID("FirstTest", "ENT_TWO")));
		assertNotEquals("Resource string 3", messages.get(new MsgID("FirstTest", "ENT_THREE")));
	}

}
