package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class SecurityAssemblerTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EditableTerminal terminal;
	private EditableSecurity security;
	private Cache cache;
	private SecurityCache entry;
	private SecurityAssembler assembler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RIM3","SPBFUT","SUR",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		security = control.createMock(EditableSecurity.class);
		cache = control.createMock(Cache.class);
		entry = new SecurityCache(10, 150000.0d, 140000.0d,
				6.2188d, 10.0d, 0, 143870.0d, 151000.0d, // open
				150900.0d, "RTS-6.13", "RIM3", 143800.0d, 143900.0, // bid
				151500.0d, 143810.0d, descr);
		assembler = new SecurityAssembler(terminal, cache);
	}
	
	@Test
	public void testAdjustByCache_New() throws Exception {
		expect(terminal.getEditableSecurity(same(descr))).andReturn(security);
		security.setAskPrice(eq(143800.0d));
		security.setBidPrice(eq(143900.0d));
		security.setClosePrice(eq(150900.0d));
		security.setDisplayName(eq("RTS-6.13"));
		security.setHighPrice(eq(151500.0d));
		security.setLastPrice(eq(143870.0d));
		security.setLotSize(eq(10));
		security.setLowPrice(eq(143810.0d));
		security.setMaxPrice(eq(150000.0d));
		security.setMinPrice(eq(140000.0d));
		security.setMinStepPrice(eq(6.2188d));
		security.setMinStepSize(eq(10.0d));
		security.setOpenPrice(eq(151000.0d));
		security.setPrecision(eq(0));
		expect(security.isAvailable()).andReturn(false);
		cache.registerSecurityDescriptor(same(descr), eq("RIM3"));
		terminal.fireSecurityAvailableEvent(same(security));
		security.setAvailable(eq(true));
		security.resetChanges();
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustByCache_UpdateExisting() throws Exception {
		expect(terminal.getEditableSecurity(same(descr))).andReturn(security);
		security.setAskPrice(eq(143800.0d));
		security.setBidPrice(eq(143900.0d));
		security.setClosePrice(eq(150900.0d));
		security.setDisplayName(eq("RTS-6.13"));
		security.setHighPrice(eq(151500.0d));
		security.setLastPrice(eq(143870.0d));
		security.setLotSize(eq(10));
		security.setLowPrice(eq(143810.0d));
		security.setMaxPrice(eq(150000.0d));
		security.setMinPrice(eq(140000.0d));
		security.setMinStepPrice(eq(6.2188d));
		security.setMinStepSize(eq(10.0d));
		security.setOpenPrice(eq(151000.0d));
		security.setPrecision(eq(0));
		expect(security.isAvailable()).andReturn(true);
		security.fireChangedEvent();
		security.resetChanges();
		control.replay();
		
		assembler.adjustByCache(entry);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(assembler.equals(assembler));
		assertFalse(assembler.equals(null));
		assertFalse(assembler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<Cache> vCache = new Variant<Cache>(vTerm)
			.add(cache)
			.add(control.createMock(Cache.class));
		Variant<?> iterator = vCache;
		int foundCnt = 0;
		SecurityAssembler x = null, found = null;
		do {
			x = new SecurityAssembler(vTerm.get(), vCache.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(cache, found.getCache());
	}

}
