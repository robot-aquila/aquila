package ru.prolib.aquila.core.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OptionProviderMLTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private KVWritableStore kvs_cmdl, kvs_file, kvs_defs;
	private OptionProvider op_cmdl, op_file, op_defs, opMock1, opMock2, opMock3;
	private List<OptionProvider> providers_stub;
	private OptionProviderML service, serviceWithMocks;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		opMock1 = control.createMock(OptionProvider.class);
		opMock2 = control.createMock(OptionProvider.class);
		opMock3 = control.createMock(OptionProvider.class);
		op_cmdl = new OptionProviderKvs(kvs_cmdl = new KVStoreHash());
		op_file = new OptionProviderKvs(kvs_file = new KVStoreHash());
		op_defs = new OptionProviderKvs(kvs_defs = new KVStoreHash());
		service = new OptionProviderML(providers_stub = new ArrayList<>());
		service.addLayer(op_cmdl);
		service.addLayer(op_file);
		service.addLayer(op_defs);
		serviceWithMocks = new OptionProviderML();
		serviceWithMocks.addLayer(opMock1);
		serviceWithMocks.addLayer(opMock2);
		serviceWithMocks.addLayer(opMock3);
	}
	
	@Test
	public void testAddLayer() {
		service = new OptionProviderML(providers_stub = new ArrayList<>());
		assertEquals(0, service.addLayer(op_cmdl));
		assertEquals(1, service.addLayer(op_file));
		assertEquals(2, service.addLayer(op_defs));
		
		List<OptionProvider> expected = new ArrayList<>();
		expected.add(op_cmdl);
		expected.add(op_file);
		expected.add(op_defs);
		assertEquals(expected, providers_stub);
	}
	
	@Test
	public void testGetLayer() {
		service = new OptionProviderML(providers_stub = new ArrayList<>());
		providers_stub.add(op_cmdl);
		providers_stub.add(op_file);
		providers_stub.add(op_defs);
		
		assertSame(op_cmdl, service.getLayer(0));
		assertSame(op_file, service.getLayer(1));
		assertSame(op_defs, service.getLayer(2));
	}
	
	@Test
	public void testHasOption() throws Exception {
		kvs_cmdl.add("foo", "1");
		kvs_file.add("bar", "2");
		kvs_defs.add("buz", "3");
		
		assertTrue(service.hasOption("foo"));
		assertTrue(service.hasOption("bar"));
		assertTrue(service.hasOption("buz"));
		assertFalse(service.hasOption("kappa"));
	}
	
	@Test
	public void testGetInteger1() throws Exception {
		kvs_cmdl.add("foo", "1");
		kvs_file.add("foo", "2");
		kvs_defs.add("foo", "3");
		assertEquals(Integer.valueOf(1), service.getInteger("foo"));
		
		kvs_file.add("bar", "5");
		kvs_defs.add("bar", "6");
		assertEquals(Integer.valueOf(5), service.getInteger("bar"));
		
		kvs_defs.add("buz", "8");
		assertEquals(Integer.valueOf(8), service.getInteger("buz"));
		
		assertNull(service.getInteger("kappa"));
	}
	
	@Test
	public void testGetInteger2() throws Exception {
		assertEquals(Integer.valueOf(2), service.getInteger("foo", 2));
		
		kvs_defs.add("foo", "5");
		assertEquals(Integer.valueOf(5), service.getInteger("foo", 2));
		
		kvs_file.add("foo", "1");
		assertEquals(Integer.valueOf(1), service.getInteger("foo", 2));
		
		kvs_cmdl.add("foo", "7");
		assertEquals(Integer.valueOf(7), service.getInteger("foo", 2));
		
		kvs_cmdl.add("bar", null);
		assertEquals(Integer.valueOf(9), service.getInteger("bar", 9));
	}
	
	@Test
	public void testGetIntegerPositive1() throws Exception {
		assertNull(service.getIntegerPositive("foo"));
		
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositive("foo"));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositive("foo"));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositive("foo"));
	}
	
	@Test
	public void testGetIntegerPositive1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositive("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositive2() throws Exception {
		assertEquals(Integer.valueOf(9), service.getIntegerPositive("foo", 9));
		
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositive("foo", 9));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositive("foo", 9));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositive("foo", 9));
	}
	
	@Test
	public void testGetIntegerPositive2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositive("foo", 45));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1() throws Exception {
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositiveNotNull("foo"));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositiveNotNull("foo"));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositiveNotNull("foo"));
	}
	
	@Test
	public void testGetIntegerPositiveNotNull_WithMocks1() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositiveNotNull("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositiveNotNull("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNotNull1_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getIntegerPositiveNotNull("foo");
	}

	@Test
	public void testGetIntegerPositiveNotNull2() throws Exception {
		assertEquals(Integer.valueOf(9), service.getIntegerPositiveNotNull("foo", 9));
		
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositiveNotNull("foo", 9));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositiveNotNull("foo", 9));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositiveNotNull("foo", 9));
	}
	
	@Test
	public void testGetIntegerPositiveNotNull2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositiveNotNull("foo", 45));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNotNull2_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");

		service.getIntegerPositiveNotNull("foo", null);
	}
	
	@Test
	public void testGetIntegerPositiveNonZero1() throws Exception {
		assertNull(service.getIntegerPositiveNonZero("foo"));
		
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositiveNonZero("foo"));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositiveNonZero("foo"));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositiveNonZero("foo"));
	}

	@Test
	public void testGetIntegerPositiveNonZero1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositiveNonZero("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNonZero1_ThrowsIfZero() throws Exception {
		kvs_cmdl.add("foo", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");
		
		service.getIntegerPositiveNonZero("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2() throws Exception {
		assertEquals(Integer.valueOf(7), service.getIntegerPositiveNonZero("foo", 7));
		
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositiveNonZero("foo", 7));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositiveNonZero("foo", 7));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositiveNonZero("foo", 7));
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositiveNonZero("foo", 13));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNonZero2_ThrowsIfZero() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");
		
		service.getIntegerPositiveNonZero("foo", 0);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1() throws Exception {
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositiveNonZeroNotNull("foo"));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositiveNonZeroNotNull("foo"));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositiveNonZeroNotNull("foo"));
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositiveNonZeroNotNull("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfZero() throws Exception {
		kvs_cmdl.add("foo", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");
		
		service.getIntegerPositiveNonZeroNotNull("foo");
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull1_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getIntegerPositiveNonZeroNotNull("foo");
	}

	@Test
	public void testGetIntegerPositiveNonZeroNotNull2() throws Exception {
		assertEquals(Integer.valueOf(12), service.getIntegerPositiveNonZeroNotNull("foo", 12));
		
		kvs_defs.add("foo", "4");
		assertEquals(Integer.valueOf(4), service.getIntegerPositiveNonZeroNotNull("foo", 12));

		kvs_file.add("foo", "6");
		assertEquals(Integer.valueOf(6), service.getIntegerPositiveNonZeroNotNull("foo", 12));
		
		kvs_cmdl.add("foo", "8");
		assertEquals(Integer.valueOf(8), service.getIntegerPositiveNonZeroNotNull("foo", 12));
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getIntegerPositive("foo")).andReturn(27);
		control.replay();
		
		assertEquals(Integer.valueOf(27), serviceWithMocks.getIntegerPositiveNonZeroNotNull("foo"), 26);
		
		control.verify();
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfZero() throws Exception {
		kvs_cmdl.add("foo", "0");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be greater than zero but: 0");
		
		service.getIntegerPositiveNonZeroNotNull("foo", 0);
	}
	
	@Test
	public void testGetIntegerPositiveNonZeroNotNull2_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getIntegerPositiveNonZeroNotNull("foo", null);
	}
	
	@Test
	public void testGetString1() throws Exception {
		assertNull(service.getString("foo"));
		
		kvs_defs.add("foo", "halo");
		assertEquals("halo", service.getString("foo"));
		
		kvs_file.add("foo", "baka");
		assertEquals("baka", service.getString("foo"));
		
		kvs_cmdl.add("foo", "coma");
		assertEquals("coma", service.getString("foo"));
	}
	
	@Test
	public void testGetString1_WithMocks() {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getString("foo")).andReturn("karamba");
		control.replay();
		
		assertEquals("karamba", serviceWithMocks.getString("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetString2() throws Exception {
		assertEquals("kang", service.getString("foo", "kang"));
		
		kvs_defs.add("foo", "halo");
		assertEquals("halo", service.getString("foo", "kang"));
		
		kvs_file.add("foo", "baka");
		assertEquals("baka", service.getString("foo", "kang"));
		
		kvs_cmdl.add("foo", "coma");
		assertEquals("coma", service.getString("foo", "kang"));
	}
	
	@Test
	public void testGetString2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getString("foo")).andReturn("karamba");
		control.replay();
		
		assertEquals("karamba", serviceWithMocks.getString("foo", "kang"));
		
		control.verify();
	}
	
	@Test
	public void testGetStringNotNull2() throws Exception {
		assertEquals("kang", service.getStringNotNull("foo", "kang"));
		
		kvs_defs.add("foo", "halo");
		assertEquals("halo", service.getStringNotNull("foo", "kang"));
		
		kvs_file.add("foo", "baka");
		assertEquals("baka", service.getStringNotNull("foo", "kang"));
		
		kvs_cmdl.add("foo", "coma");
		assertEquals("coma", service.getStringNotNull("foo", "kang"));
	}
	
	@Test
	public void testGetStringNotNull2_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getStringNotNull("foo", null);
	}
	
	@Test
	public void testGetStringOfList() throws Exception {
		assertNull(service.getStringOfList("foo", "one", "two", "boo"));
		
		kvs_defs.add("foo", "two");
		assertEquals("two", service.getStringOfList("foo", "one", "two", "boo"));
		
		kvs_file.add("foo", "one");
		assertEquals("one", service.getStringOfList("foo", "one", "two", "boo"));
		
		kvs_cmdl.add("foo", "boo");
		assertEquals("boo", service.getStringOfList("foo", "one", "two", "boo"));
	}
	
	@Test
	public void testGetStringOfList_WithMocks() throws Exception {
		expect(opMock1.getStringOfList("foo", "one", "two", "boo")).andReturn(null);
		expect(opMock2.getStringOfList("foo", "one", "two", "boo")).andReturn(null);
		expect(opMock3.getStringOfList("foo", "one", "two", "boo")).andReturn("one");
		control.replay();
		
		assertEquals("one", serviceWithMocks.getStringOfList("foo", "one", "two", "boo"));
		
		control.verify();
	}
	
	@Test
	public void testGetStringOfList_ThrowsIfNotListed() throws Exception {
		kvs_defs.add("foo", "zooma");
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be one of list value [true, false] but: zooma");
		
		service.getStringOfList("foo", "true", "false");
	}
	
	@Test
	public void testGetBoolean1() throws Exception {
		assertFalse(service.getBoolean("foo"));
		
		kvs_defs.add("foo", "1");
		assertTrue(service.getBoolean("foo"));
		
		kvs_file.add("foo", "false");
		assertFalse(service.getBoolean("foo"));
		
		kvs_cmdl.add("foo", "true");
		assertTrue(service.getBoolean("foo"));
	}
	
	@Test
	public void testGetBoolean1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getBoolean("foo")).andReturn(true);
		control.replay();
		
		assertTrue(serviceWithMocks.getBoolean("foo"));
		
		control.verify();
	}

	@Test
	public void testGetBoolean2() throws Exception {
		assertTrue(service.getBoolean("foo", true));
		
		kvs_defs.add("foo", "false");
		assertFalse(service.getBoolean("foo", true));
		
		kvs_file.add("foo", "1");
		assertTrue(service.getBoolean("foo", true));
		
		kvs_cmdl.add("foo", "0");
		assertFalse(service.getBoolean("foo", true));
	}
	
	@Test
	public void testGetBoolean2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getBoolean("foo")).andReturn(true);
		control.replay();
		
		assertTrue(serviceWithMocks.getBoolean("foo", false));
		
		control.verify();
	}
	
	@Test
	public void testGetInstant1() throws Exception {
		kvs_defs.add("foo", "");
		kvs_file.add("bar", "2018-04-22T05:15:45Z");
		kvs_cmdl.add("zoo", null);
		
		assertNull(service.getInstant("zoo"));
		assertNull(service.getInstant("foo"));
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstant("bar"));
	}
	
	@Test
	public void testGetInstant1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getInstant("foo")).andReturn(T("2019-07-19T00:00:00Z"));
		control.replay();
		
		assertEquals(T("2019-07-19T00:00:00Z"), serviceWithMocks.getInstant("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetInstant2() throws Exception {
		kvs_defs.add("foo", "");
		kvs_file.add("bar", "2018-04-22T05:15:45Z");
		kvs_cmdl.add("zoo", null);
		
		assertEquals(T("2019-07-19T09:35:00Z"), service.getInstant("foo", T("2019-07-19T09:35:00Z")));
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstant("bar", T("2019-07-19T09:35:00Z")));
		assertEquals(T("2019-07-19T09:35:00Z"), service.getInstant("zoo", T("2019-07-19T09:35:00Z")));
	}
	
	@Test
	public void testGetInstant2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getInstant("foo")).andReturn(T("2019-07-19T00:00:00Z"));
		control.replay();
		
		assertEquals(T("2019-07-19T00:00:00Z"), serviceWithMocks.getInstant("foo", T("2019-07-19T09:35:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetInstantNotNull1() throws Exception {
		kvs_defs.add("foo", "2018-04-22T05:15:45Z");
		
		assertEquals(T("2018-04-22T05:15:45Z"), service.getInstantNotNull("foo"));
	}
	
	@Test
	public void testGetInstantNotNull1_WithMock() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getInstant("foo")).andReturn(T("2019-07-19T00:00:00Z"));
		control.replay();
		
		assertEquals(T("2019-07-19T00:00:00Z"), serviceWithMocks.getInstantNotNull("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetInstantNotNull1_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getInstantNotNull("foo");
	}
	
	@Test
	public void testGetInstantNotNull2() throws Exception {
		assertEquals(T("2014-07-12T01:15:25Z"), service.getInstantNotNull("foo", T("2014-07-12T01:15:25Z")));
		
		kvs_defs.add("foo", "1972-12-10T13:45:00Z");
		assertEquals(T("1972-12-10T13:45:00Z"), service.getInstantNotNull("foo", T("2014-07-12T01:15:25Z")));
		
		kvs_file.add("foo", "2020-01-01T00:00:00Z");
		assertEquals(T("2020-01-01T00:00:00Z"), service.getInstantNotNull("foo", T("2014-07-12T01:15:25Z")));
		
		kvs_cmdl.add("foo", "1921-04-23T12:30:00Z");
		assertEquals(T("1921-04-23T12:30:00Z"), service.getInstantNotNull("foo", T("2014-07-12T01:15:25Z")));
	}
	
	@Test
	public void testGetInstantNotNull2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getInstant("foo")).andReturn(T("2019-07-19T00:00:00Z"));
		control.replay();
		
		assertEquals(T("2019-07-19T00:00:00Z"), serviceWithMocks.getInstantNotNull("foo", T("2014-07-12T01:15:25Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetInstantNotNull2_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getInstantNotNull("foo", null);
	}
	
	@Test
	public void testGetFile1() throws Exception {
		assertNull(service.getFile("foo"));
		
		kvs_defs.add("foo", "D:\\battle\\royal");
		assertEquals(new File("D:\\battle\\royal"), service.getFile("foo"));
		
		kvs_file.add("foo", "/root");
		assertEquals(new File("/root"), service.getFile("foo"));
		
		kvs_cmdl.add("foo", "/foo/bar");
		assertEquals(new File("/foo/bar"), service.getFile("foo"));
	}
	
	@Test
	public void testGetFile1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getFile("foo")).andReturn(new File("/foo/bar/buz"));
		control.replay();
		
		assertEquals(new File("/foo/bar/buz"), serviceWithMocks.getFile("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetFile2() throws Exception {
		assertEquals(new File("/kappa/sport"), service.getFile("foo", new File("/kappa/sport")));
		
		kvs_defs.add("foo", "D:\\battle\\royal");
		assertEquals(new File("D:\\battle\\royal"), service.getFile("foo", new File("/kappa/sport")));
		
		kvs_file.add("foo", "/root");
		assertEquals(new File("/root"), service.getFile("foo", new File("/kappa/sport")));
		
		kvs_cmdl.add("foo", "/foo/bar");
		assertEquals(new File("/foo/bar"), service.getFile("foo", new File("/kappa/sport")));
	}
	
	@Test
	public void testGetFile2_WithMock() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getFile("foo")).andReturn(new File("/foo/bar/buz"));
		control.replay();
		
		assertEquals(new File("/foo/bar/buz"), serviceWithMocks.getFile("foo", new File("/kappa/sport")));
		
		control.verify();
	}
	
	@Test
	public void testGetFileNotNull1() throws Exception {
		kvs_defs.add("foo", "/gamma/beta");
		assertEquals(new File("/gamma/beta"), service.getFileNotNull("foo"));
		
		kvs_file.add("foo", "/tutumb/r");
		assertEquals(new File("/tutumb/r"), service.getFileNotNull("foo"));
		
		kvs_cmdl.add("foo", "/kabucha/one");
		assertEquals(new File("/kabucha/one"), service.getFileNotNull("foo"));
	}
	
	@Test
	public void testGetFileNotNull1_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getFile("foo")).andReturn(new File("/foo/bar/buz"));
		control.replay();
		
		assertEquals(new File("/foo/bar/buz"), serviceWithMocks.getFileNotNull("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetFileNotNull1_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getFileNotNull("foo");
	}
	
	@Test
	public void testGetFileNotNull2() throws Exception {
		assertEquals(new File("zulu/24"), service.getFileNotNull("foo", new File("zulu/24")));
		
		kvs_defs.add("foo", "/gamma/beta");
		assertEquals(new File("/gamma/beta"), service.getFileNotNull("foo", new File("zulu/24")));
		
		kvs_file.add("foo", "/tutumb/r");
		assertEquals(new File("/tutumb/r"), service.getFileNotNull("foo", new File("zulu/24")));
		
		kvs_cmdl.add("foo", "/kabucha/one");
		assertEquals(new File("/kabucha/one"), service.getFileNotNull("foo", new File("zulu/24")));
	}
	
	@Test
	public void testGetFileNotNull2_WithMocks() throws Exception {
		expect(opMock1.hasOption("foo")).andReturn(false);
		expect(opMock2.hasOption("foo")).andReturn(false);
		expect(opMock3.hasOption("foo")).andReturn(true);
		expect(opMock3.getFile("foo")).andReturn(new File("/foo/bar/buz"));
		control.replay();
		
		assertEquals(new File("/foo/bar/buz"), serviceWithMocks.getFileNotNull("foo", new File("foo/bar")));
		
		control.verify();
	}
	
	@Test
	public void testGetFileNotNull2_ThrowsIfNull() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("foo option expected to be not null");
		
		service.getFileNotNull("foo", null);
	}
	
}
