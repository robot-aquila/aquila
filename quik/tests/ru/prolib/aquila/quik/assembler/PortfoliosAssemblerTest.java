package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class PortfoliosAssemblerTest {
	private IMocksControl control;
	private Cache cache;
	private PortfolioAssembler portfolioAssembler;
	private PortfoliosAssembler assembler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(Cache.class);
		portfolioAssembler = control.createMock(PortfolioAssembler.class);
		assembler = new PortfoliosAssembler(cache, portfolioAssembler);
	}
	
	@Test
	public void testOnEvent() throws Exception {
		PortfolioFCache c1 = control.createMock(PortfolioFCache.class),
			c2 = control.createMock(PortfolioFCache.class),
			c3 = control.createMock(PortfolioFCache.class);
		List<PortfolioFCache> list = new Vector<PortfolioFCache>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		expect(cache.getAllPortfoliosF()).andReturn(list);
		portfolioAssembler.adjustByCache(same(c1));
		portfolioAssembler.adjustByCache(same(c2));
		portfolioAssembler.adjustByCache(same(c3));
		control.replay();
		
		assembler.onEvent(null);
		
		control.verify();
	}

	@Test
	public void testStart() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(cache.OnPortfoliosFCacheUpdate()).andReturn(type);
		type.addListener(same(assembler));
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(cache.OnPortfoliosFCacheUpdate()).andReturn(type);
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
		Variant<PortfolioAssembler> vAsm =
				new Variant<PortfolioAssembler>(vCache)
			.add(portfolioAssembler)
			.add(control.createMock(PortfolioAssembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		PortfoliosAssembler found = null, x = null;
		do {
			x = new PortfoliosAssembler(vCache.get(), vAsm.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(cache, found.getCache());
		assertSame(portfolioAssembler, found.getPortfolioAssembler());
	}

}
