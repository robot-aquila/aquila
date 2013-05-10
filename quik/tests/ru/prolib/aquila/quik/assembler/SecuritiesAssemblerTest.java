package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class SecuritiesAssemblerTest {
	private IMocksControl control;
	private Cache cache;
	private SecurityAssembler securityAssembler;
	private SecuritiesAssembler assembler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(Cache.class);
		securityAssembler = control.createMock(SecurityAssembler.class);
		assembler = new SecuritiesAssembler(cache, securityAssembler);
	}
	
	@Test
	public void testOnEvent() throws Exception {
		SecurityCache c1 = control.createMock(SecurityCache.class),
			c2 = control.createMock(SecurityCache.class),
			c3 = control.createMock(SecurityCache.class);
		List<SecurityCache> list = new Vector<SecurityCache>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		expect(cache.getAllSecurities()).andReturn(list);
		securityAssembler.adjustByCache(same(c1));
		securityAssembler.adjustByCache(same(c2));
		securityAssembler.adjustByCache(same(c3));
		control.replay();
		
		assembler.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(cache.OnSecuritiesCacheUpdate()).andReturn(type);
		type.addListener(same(assembler));
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(cache.OnSecuritiesCacheUpdate()).andReturn(type);
		type.removeListener(same(assembler));
		control.replay();
		
		assembler.stop();
		
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
		Variant<Cache> vCache = new Variant<Cache>()
			.add(cache)
			.add(control.createMock(Cache.class));
		Variant<SecurityAssembler> vAsm = new Variant<SecurityAssembler>(vCache)
			.add(securityAssembler)
			.add(control.createMock(SecurityAssembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		SecuritiesAssembler found = null, x = null;
		do {
			x = new SecuritiesAssembler(vCache.get(), vAsm.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(cache, found.getCache());
		assertSame(securityAssembler, found.getSecurityAssembler());
	}

}
