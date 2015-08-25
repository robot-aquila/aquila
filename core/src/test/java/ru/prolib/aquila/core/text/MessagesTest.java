package ru.prolib.aquila.core.text;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.prolib.aquila.core.text.Messages;

public class MessagesTest {

	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Test
	public void testConstructor_WithArgs() {
		Messages t = new Messages("ru_RU");
		assertEquals("en_US", t.getDefLang());
		String[] path = t.getLocalesPath();
		assertEquals("shared", path[0]);
		assertEquals("lang", path[1]);
		assertEquals("ru_RU", t.getLang());
	}
	
	@Test
	public void testConstructor_WithoutArgs() {
		Messages t = new Messages();
		assertEquals("en_US", t.getDefLang());
		String[] path = t.getLocalesPath();
		assertEquals("shared", path[0]);
		assertEquals("lang", path[1]);
		assertNull(t.getLang());
		
	}

	@Test
	public void testLoad_DefaultLocale() {
		Messages t = new Messages();
		t.setLocalesPath(new String[] {"fixture", "lang"});
		t.load();
		assertEquals("Entry one", t.get(new MsgID("FirstTest", "ENT_ONE")));
		assertEquals("Entry two", t.get(new MsgID("FirstTest", "ENT_TWO")));
		assertEquals("Entry three", t.get(new MsgID("FirstTest", "ENT_THREE")));
		assertEquals("Some here", t.get(new MsgID("SecondTest", "SOME")));
	}
	
	@Test
	public void testLoad_RuLocale() {
		Messages t = new Messages("ru_RU");
		t.setLocalesPath(new String[] {"fixture", "lang"});
		t.load();
		assertEquals("Первая", t.get(new MsgID("FirstTest", "ENT_ONE")));
		assertEquals("Entry two", t.get(new MsgID("FirstTest", "ENT_TWO")));
		assertEquals("Третья", t.get(new MsgID("FirstTest", "ENT_THREE")));
		assertEquals("Some here", t.get(new MsgID("SecondTest", "SOME")));
		assertEquals("Другое", t.get(new MsgID("SecondTest", "ANOTHER")));
	}

}
