package ru.prolib.aquila.core.config;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class OptionProviderKvsTest {
	private Map<String, String> data;
	private OptionProviderKvs service;
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		data = new HashMap<>();
		service = new OptionProviderKvs(new KVStoreHash(data));
	}
	
	@Test
	public void testGetStore() {
		KVStoreHash store = new KVStoreHash();
		service = new OptionProviderKvs(store);
		
		assertSame(store, service.getStore());
	}
	
	@Test
	public void testHasOption() throws Exception {
		data.put("zulu24", "true");
		data.put("foo", "bar");
		data.put("gamma", "1.5");
		
		assertFalse(service.hasOption("comanche"));
		assertTrue(service.hasOption("zulu24"));
		assertTrue(service.hasOption("foo"));
		assertTrue(service.hasOption("gamma"));
		assertFalse(service.hasOption("beta"));
	}
	
	@Test
	public void testGetInteger() throws Exception {
		data.put("gaz", null);
		data.put("cho", "");
		data.put("foo", "0");
		data.put("bar", "1");
		data.put("buz", "-15");
		data.put("zoo", "27712365");
		
		assertNull(service.getInteger("gaz"));
		assertNull(service.getInteger("cho"));
		assertEquals(Integer.valueOf(0), service.getInteger("foo"));
		assertEquals(Integer.valueOf(1), service.getInteger("bar"));
		assertEquals(Integer.valueOf(-15), service.getInteger("buz"));
		assertEquals(Integer.valueOf(27712365), service.getInteger("zoo"));
	}
	
	@Test
	public void testGetInteger1_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "240.78-foo");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: 240.78-foo");
		
		service.getInteger("foo");
	}
	
	@Test
	public void testGetInteger2() throws Exception {
		data.put("gaz", null);
		data.put("cho", "");
		data.put("foo", "0");
		data.put("bar", "1");
		data.put("buz", "-15");
		data.put("zoo", "27712365");
		
		assertEquals(Integer.valueOf(120), service.getInteger("gaz", 120));
		assertEquals(Integer.valueOf(190), service.getInteger("cho", 190));
		assertEquals(Integer.valueOf(0), service.getInteger("foo", 500));
		assertEquals(Integer.valueOf(1), service.getInteger("bar", 924));
		assertEquals(Integer.valueOf(-15), service.getInteger("buz", -200));
		assertEquals(Integer.valueOf(27712365), service.getInteger("zoo", -1));
	}
	
	@Test
	public void testGetInteger2_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "chimboorotta");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: chimboorotta");
		
		service.getInteger("foo", 256);
	}
	
	@Test
	public void testGetIntegerPositive1() throws Exception {
		data.put("gaz", null);
		data.put("cho", "");
		data.put("foo", "0");
		data.put("bar", "1");
		data.put("zoo", "27712365");
		
		assertNull(service.getIntegerPositive("gaz"));
		assertNull(service.getIntegerPositive("cho"));
		assertEquals(Integer.valueOf(0), service.getIntegerPositive("foo"));
		assertEquals(Integer.valueOf(1), service.getIntegerPositive("bar"));
		assertEquals(Integer.valueOf(27712365), service.getIntegerPositive("zoo"));
	}
	
	@Test
	public void testGetIntegerPositive1_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "kappa-12");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: kappa-12");
		
		service.getIntegerPositive("foo");
	}

	@Test
	public void testGetIntegerPositive1_ThrowsIfNegative() throws Exception {
		data.put("foo", "-12");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be a positive integer but: -12");
		
		service.getIntegerPositive("foo");
	}
	
	@Test
	public void testGetIntegerPositive2() throws Exception {
		data.put("gaz", null);
		data.put("cho", "");
		data.put("foo", "0");
		data.put("bar", "1");
		data.put("zoo", "27712365");
		
		assertNull(service.getIntegerPositive("cin-cin", null));
		assertEquals(Integer.valueOf(56), service.getIntegerPositive("zulu", 56));
		assertEquals(Integer.valueOf(440), service.getIntegerPositive("gaz", 440));
		assertEquals(Integer.valueOf(280), service.getIntegerPositive("cho", 280));
		assertEquals(Integer.valueOf(0), service.getIntegerPositive("foo", 112));
		assertEquals(Integer.valueOf(1), service.getIntegerPositive("bar", 586));
		assertEquals(Integer.valueOf(27712365), service.getIntegerPositive("zoo", 9928));

	}

	@Test
	public void testGetIntegerPositive2_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "kikorra");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: kikorra");

		service.getIntegerPositive("foo", 513);
	}
	
	@Test
	public void testGetIntegerPositive2_ThrowsIfNegative() throws Exception {
		data.put("foo", "-200");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be a positive integer but: -200");

		service.getIntegerPositive("foo", 513);
	}

	@Test
	public void testGetIntegerPositive2_ThrowsIfNegativeDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be a positive integer but: -513");

		service.getIntegerPositive("foo", -513);
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1() throws Exception {
		data.put("foo", "0");
		data.put("bar", "1");
		data.put("zoo", "27712365");

		assertEquals(Integer.valueOf(0), service.getIntegerPositiveNotNull("foo"));
		assertEquals(Integer.valueOf(1), service.getIntegerPositiveNotNull("bar"));
		assertEquals(Integer.valueOf(27712365), service.getIntegerPositiveNotNull("zoo"));
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "96.24");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: 96.24");

		service.getIntegerPositiveNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1_ThrowsIfNegative() throws Exception {
		data.put("foo", "-96");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be a positive integer but: -96");

		service.getIntegerPositiveNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1_ThrowsIfNotExists() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNotNull("foo");
	}

	@Test
	public void testGetIntegerPositiveNotNull1_ThrowsIfNull() throws Exception {
		data.put("foo", null);
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1_ThrowsIfEmptyString() throws Exception {
		data.put("foo", "");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNotNull("foo");
	}

	@Test
	public void testGetIntegerPositiveNotNull2() throws Exception {
		data.put("gaz", null);
		data.put("cho", "");
		data.put("foo", "0");
		data.put("bar", "1");
		data.put("zoo", "27712365");

		assertEquals(Integer.valueOf(615), service.getIntegerPositiveNotNull("xev", 615));
		assertEquals(Integer.valueOf(208), service.getIntegerPositiveNotNull("gaz", 208));
		assertEquals(Integer.valueOf(115), service.getIntegerPositiveNotNull("cho", 115));
		assertEquals(Integer.valueOf(0), service.getIntegerPositiveNotNull("foo", 2048));
		assertEquals(Integer.valueOf(1), service.getIntegerPositiveNotNull("bar", 901));
		assertEquals(Integer.valueOf(27712365), service.getIntegerPositiveNotNull("zoo", 412));
	}
	
	@Test
	public void testGetIntegerPositiveNotNull2_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "dolphin");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: dolphin");

		service.getIntegerPositiveNotNull("foo", 225);
	}

	@Test
	public void testGetIntegerPositiveNotNull2_ThrowsIfNegative() throws Exception {
		data.put("bar", "-24");
		eex.expect(ConfigException.class);
		eex.expectMessage("bar option expected to be a positive integer but: -24");

		service.getIntegerPositiveNotNull("bar", 56);
	}

	@Test
	public void testGetIntegerPositiveNotNull2_ThrowsIfNegativeDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("zulu option expected to be a positive integer but: -240");

		service.getIntegerPositiveNotNull("zulu", -240);
	}
	
	@Test
	public void testGetIntegerPositiveNotNull2_ThrowsIfNullDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("zulu option expected to be not null");

		service.getIntegerPositiveNotNull("zulu", null);

	}
	
	@Test
	public void testGetIntegerPositiveNonZero1() throws Exception {
		data.put("gaz", null);
		data.put("cho", "");
		data.put("bar", "1");
		data.put("zoo", "27712365");

		assertNull(service.getIntegerPositiveNonZero("xev"));
		assertNull(service.getIntegerPositiveNonZero("gaz"));
		assertNull(service.getIntegerPositiveNonZero("cho"));
		assertEquals(Integer.valueOf(1), service.getIntegerPositiveNonZero("bar"));
		assertEquals(Integer.valueOf(27712365), service.getIntegerPositiveNonZero("zoo"));
	}
	
	@Test
	public void testGetIntegerPositiveNonZero1_ThrowsIfBadNumber() throws Exception {
		data.put("hello", "world");
		eex.expect(ConfigException.class);
		eex.expectMessage("hello option expected to be an integer but: world");

		service.getIntegerPositiveNonZero("hello");
	}
	
	@Test
	public void testGetIntegerPositiveNonZero1_ThrowsIfNegative() throws Exception {
		data.put("hello", "-100");
		eex.expect(ConfigException.class);
		eex.expectMessage("hello option expected to be a positive integer but: -100");

		service.getIntegerPositiveNonZero("hello");
	}
	
	@Test
	public void testGetIntegerPositiveNonZero1_ThrowsIfZero() throws Exception {
		data.put("hello", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("hello option expected to be greater than zero but: 0");

		service.getIntegerPositiveNonZero("hello");
	}

	@Test
	public void testGetIntegerPositiveNonZero2() throws Exception {
		data.put("gaz", null);
		data.put("maz", "");
		data.put("baz", "25");
		
		assertEquals(Integer.valueOf(256), service.getIntegerPositiveNonZero("gaz", 256));
		assertEquals(Integer.valueOf(182), service.getIntegerPositiveNonZero("maz", 182));
		assertEquals(Integer.valueOf(25), service.getIntegerPositiveNonZero("baz", 440));
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2_ThrowsIfBadNumber() throws Exception {
		data.put("zuma", "foo");
		eex.expect(ConfigException.class);
		eex.expectMessage("zuma option expected to be an integer but: foo");

		service.getIntegerPositiveNonZero("zuma", 98);
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2_ThrowsIfNegative() throws Exception {
		data.put("zuma", "-1");
		eex.expect(ConfigException.class);
		eex.expectMessage("zuma option expected to be a positive integer but: -1");

		service.getIntegerPositiveNonZero("zuma", 98);
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2_ThrowsIfZero() throws Exception {
		data.put("zuma", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("zuma option expected to be greater than zero but: 0");

		service.getIntegerPositiveNonZero("zuma", 98);
	}

	@Test
	public void testGetIntegerPositiveNonZero2_ThrowsIfNegativeDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: -1");

		service.getIntegerPositiveNonZero("foo", -1);
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2_ThrowsIfZeroDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");

		service.getIntegerPositiveNonZero("foo", 0);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1() throws Exception {
		data.put("foo", "5");
		data.put("bar", "1");
		
		assertEquals(Integer.valueOf(5), service.getIntegerPositiveNonZeroNotNull("foo"));
		assertEquals(Integer.valueOf(1), service.getIntegerPositiveNonZeroNotNull("bar"));
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "bar");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: bar");

		service.getIntegerPositiveNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfNegative() throws Exception {
		data.put("foo", "-10");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be a positive integer but: -10");

		service.getIntegerPositiveNonZeroNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfZero() throws Exception {
		data.put("foo", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");

		service.getIntegerPositiveNonZeroNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfNotExists() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNonZeroNotNull("foo");
	}

	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfEmptyString() throws Exception {
		data.put("foo", "");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNonZeroNotNull("foo");

	}

	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfNull() throws Exception {
		data.put("foo", null);
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNonZeroNotNull("foo");
	}

	@Test
	public void testGetIntegerPositiveNonZeroNotNull2() throws Exception {
		data.put("bar", "24");
		data.put("zoo", null);

		assertEquals(Integer.valueOf(20), service.getIntegerPositiveNonZeroNotNull("foo", 20));
		assertEquals(Integer.valueOf(24), service.getIntegerPositiveNonZeroNotNull("bar", 20));
		assertEquals(Integer.valueOf(10), service.getIntegerPositiveNonZeroNotNull("zoo", 10));
	}

	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfBadNumber() throws Exception {
		data.put("foo", "bar");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be an integer but: bar");

		service.getIntegerPositiveNonZeroNotNull("foo", 100);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfNegative() throws Exception {
		data.put("foo", "-1");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be a positive integer but: -1");

		service.getIntegerPositiveNonZeroNotNull("foo", 100);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfNegativeDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: -100");

		service.getIntegerPositiveNonZeroNotNull("foo", -100);
	}

	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfNullDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getIntegerPositiveNonZeroNotNull("foo", null);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfZero() throws Exception {
		data.put("foo", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");
		
		service.getIntegerPositiveNonZeroNotNull("foo", 200);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfZeroDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: -100");

		service.getIntegerPositiveNonZeroNotNull("foo", -100);
	}
	
	@Test
	public void testGetString1() {
		data.put("foo", null);
		data.put("bar", "");
		data.put("buz", "284 buzz");
		
		assertNull(service.getString("zulu24"));
		assertNull(service.getString("foo"));
		assertNull(service.getString("bar"));
		assertEquals("284 buzz", service.getString("buz"));
	}
	
	@Test
	public void testGetString2() {
		data.put("foo", null);
		data.put("bar", "");
		data.put("buz", "284 buzz");

		assertEquals("ganza", service.getString("zulu24", "ganza"));
		assertEquals("earth", service.getString("foo", "earth"));
		assertEquals("gabucha", service.getString("bar", "gabucha"));
		assertEquals("284 buzz", service.getString("buz", "zelo"));
		assertNull(service.getString("zulu", ""));
		assertNull(service.getString("zulu", null));
	}
	
	@Test
	public void testGetStringNotNull2() throws Exception {
		data.put("foo", null);
		data.put("bar", "");
		data.put("buz", "284 buzz");

		assertEquals("ganza", service.getStringNotNull("zulu24", "ganza"));
		assertEquals("earth", service.getStringNotNull("foo", "earth"));
		assertEquals("gabucha", service.getStringNotNull("bar", "gabucha"));
		assertEquals("284 buzz", service.getStringNotNull("buz", "zelo"));
	}

	@Test
	public void testGetStringNotNull2_ThrowsIfEmptyStringDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getStringNotNull("foo", "");
	}

	@Test
	public void testGetStringNotNull2_ThrowsIfNullDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getStringNotNull("foo", null);
	}
	
	@Test
	public void testGetStringOfList() throws Exception {
		data.put("foo", null);
		data.put("bar", "");
		data.put("buz", "true");
		data.put("gaz", "false");
		
		assertNull(service.getStringOfList("foo", "true", "false"));
		assertNull(service.getStringOfList("bar", "true", "false"));
		assertEquals("true", service.getStringOfList("buz", "true", "false"));
		assertEquals("false", service.getStringOfList("gaz", "true", "false"));
	}

	@Test
	public void testGetStringOfList_ThrowsIfNotListed() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be one of list value [true, false] but: zooma");
		data.put("foo", "zooma");
		
		service.getStringOfList("foo", "true", "false");
	}
	
	@Test
	public void testGetBoolean1() throws Exception {
		data.put("one", null);
		data.put("foo", "");
		data.put("bar", "true");
		data.put("gap", "false");
		data.put("buz", "1");
		data.put("luz", "0");
		
		assertFalse(service.getBoolean("one"));
		assertFalse(service.getBoolean("foo"));
		assertTrue(service.getBoolean("bar"));
		assertFalse(service.getBoolean("gap"));
		assertTrue(service.getBoolean("buz"));
		assertFalse(service.getBoolean("luz"));
	}
	
	@Test
	public void testGetBoolean1_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be one of list value [true, false, 1, 0] but: zooma");
		data.put("foo", "zooma");
		
		service.getBoolean("foo");
	}
	
	@Test
	public void testGetBoolean2() throws Exception {
		data.put("one", null);
		data.put("foo", "");
		data.put("bar", "true");
		data.put("gap", "false");
		data.put("buz", "1");
		data.put("luz", "0");
		
		assertTrue(service.getBoolean("one", true));
		assertTrue(service.getBoolean("foo", true));
		assertTrue(service.getBoolean("bar", false));
		assertFalse(service.getBoolean("gap", true));
		assertTrue(service.getBoolean("buz", false));
		assertFalse(service.getBoolean("luz", true));
	}

	@Test
	public void testGetBoolean2_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be one of list value [true, false, 1, 0] but: zooma");
		data.put("foo", "zooma");
		
		service.getBoolean("foo", false);
	}
	
	@Test
	public void testGetInstant1() throws Exception {
		data.put("zoo", null);
		data.put("bar", "");
		data.put("foo", "2018-04-22T05:15:45Z");
		
		assertNull(service.getInstant("zoo"));
		assertNull(service.getInstant("bar"));
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstant("foo"));
	}
	
	@Test
	public void testGetInstant1_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be valid UTC time but: zooma");
		data.put("foo", "zooma");
		
		service.getInstant("foo");
	}
	
	@Test
	public void testGetInstant2() throws Exception {
		data.put("zoo", null);
		data.put("bar", "");
		data.put("foo", "2018-04-22T05:15:45Z");
		
		assertNull(service.getInstant("zoo", null));
		assertEquals(T("1998-01-01T12:30:00Z"), service.getInstant("bar", T("1998-01-01T12:30:00Z")));
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstant("foo", T("1998-01-01T12:30:00Z")));

	}

	@Test
	public void testGetInstant2_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be valid UTC time but: zooma");
		data.put("foo", "zooma");
		
		service.getInstant("foo", T("2018-04-20T00:00:00Z"));
	}
	
	@Test
	public void testGetInstantNotNull1() throws Exception {
		data.put("foo", "2018-04-22T05:15:45Z");
		
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstantNotNull("foo"));
	}

	@Test
	public void testGetInstantNotNull1_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be valid UTC time but: zooma");
		data.put("foo", "zooma");
		
		service.getInstantNotNull("foo");
	}
	
	@Test
	public void testGetInstantNotNull1_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getInstantNotNull("foo");
	}
	
	@Test
	public void testGetInstantNotNull1_ThrowsIfEmptyString() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		data.put("foo", "");
		
		service.getInstantNotNull("foo");
	}
	
	@Test
	public void testGetInstantNotNull2() throws Exception {
		data.put("zoo", null);
		data.put("bar", "");
		data.put("foo", "2018-04-22T05:15:45Z");
		
		assertEquals(T("1998-01-01T12:30:00Z"), service.getInstantNotNull("zoo", T("1998-01-01T12:30:00Z")));
		assertEquals(T("1998-01-01T12:30:00Z"), service.getInstantNotNull("bar", T("1998-01-01T12:30:00Z")));
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstantNotNull("foo", T("1998-01-01T12:30:00Z")));
	}

	@Test
	public void testGetInstantNotNull2_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be valid UTC time but: zooma");
		data.put("foo", "zooma");
		
		service.getInstantNotNull("foo", T("1998-01-01T12:30:00Z"));
	}
	
	@Test
	public void testGetInstantNotNull2_ThrowsIfNullDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getInstantNotNull("foo", null);
	}
	
	@Test
	public void testGetFile1() throws Exception {
		data.put("zoo", null);
		data.put("bar", "");
		data.put("foo", "foo/bar.dat");
		
		assertNull(service.getFile("gaz"));
		assertNull(service.getFile("zoo"));
		assertNull(service.getFile("bar"));
		assertEquals(new File("foo/bar.dat"), service.getFile("foo"));
	}
	
	@Test
	public void testGetFile2() throws Exception {
		data.put("zoo", null);
		data.put("bar", "");
		data.put("foo", "foo/bar.dat");
		
		assertNull(service.getFile("gaz", null));
		assertEquals(new File("test/best"), service.getFile("maz", new File("test/best")));
		assertEquals(new File("test/quest"), service.getFile("zoo", new File("test/quest")));
		assertEquals(new File("test/guest"), service.getFile("bar", new File("test/guest")));
		assertEquals(new File("foo/bar.dat"), service.getFile("foo", new File("test/west")));
	}
	
	@Test
	public void testGetFileNotNull1() throws Exception {
		data.put("foo", "test/best");
		
		assertEquals(new File("test/best"), service.getFileNotNull("foo"));
	}
	
	@Test
	public void testGetFileNotNull1_ThrowsIfEmptyString() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		data.put("foo", "");

		service.getFileNotNull("foo");
	}

	@Test
	public void testGetFileNotNull1_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getFileNotNull("foo");
	}
	
	@Test
	public void testGetFileNotNull2() throws Exception {
		data.put("foo", "test/best");
		data.put("bar", "");
		data.put("buz", null);
		
		assertEquals(new File("test/best"), service.getFileNotNull("foo", new File("gaz/baz")));
		assertEquals(new File("gaz/baz"), service.getFileNotNull("bar", new File("gaz/baz")));
		assertEquals(new File("gaz/baz"), service.getFileNotNull("buz", new File("gaz/baz")));
	}

	@Test
	public void testGetFileNotNull2_ThrowsIfNullDefaultValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getFileNotNull("foo", null);
	}

}
