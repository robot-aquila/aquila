package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.cache.dde.TradesGateway;

public class TradesEntryTest {
	private IMocksControl control;
	private TradesGateway gateway;
	private QUIKTerminal terminal;
	private RowSet rs;
	private Trade trade;
	private TradesEntry entry;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gateway = control.createMock(TradesGateway.class);
		terminal = control.createMock(QUIKTerminal.class);
		rs = control.createMock(RowSet.class);
		trade = control.createMock(Trade.class);
		entry = new TradesEntry(gateway, rs, 100);
	}
	
	@Test
	public void testGetEntryTime() {
		entry = new TradesEntry(gateway, rs, 100);
		long d = Math.abs(new Date().getTime() - entry.getEntryTime().getTime());
		assertTrue(d <= 1000);
	}
	
	@Test
	public void testNext() throws Exception {
		expect(rs.next()).andReturn(true).times(3);
		expect(rs.next()).andReturn(false);
		control.replay();
		
		assertEquals(-1, entry.position());
		
		assertTrue(entry.next());
		assertEquals(0, entry.position());
		
		assertTrue(entry.next());
		assertEquals(1, entry.position());
		
		assertTrue(entry.next());
		assertEquals(2, entry.position());
		
		assertFalse(entry.next());
		assertEquals(-1, entry.position());
	}
	
	@Test
	public void testCount() throws Exception {
		assertEquals(100, entry.count());
	}
	
	@Test
	public void testAccess() throws Exception {
		expect(gateway.makeTrade(terminal, rs)).andReturn(trade).times(2);
		control.replay();
		
		assertEquals(0, entry.accessCount());
		assertSame(trade, entry.access(terminal));
		assertEquals(1, entry.accessCount());
		assertSame(trade, entry.access(terminal));
		assertEquals(2, entry.accessCount());
		
		control.verify();
	}

	@Test
	public void testNext_ResetAccessCounter() throws Exception {
		expect(gateway.makeTrade(terminal, rs)).andStubReturn(trade);
		expect(rs.next()).andReturn(true);
		control.replay();
		
		entry.access(terminal);
		entry.access(terminal);
		entry.next();
		assertEquals(0, entry.accessCount());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<TradesGateway> vGw = new Variant<TradesGateway>()
			.add(gateway)
			.add(control.createMock(TradesGateway.class));
		Variant<RowSet> vRs = new Variant<RowSet>(vGw)
			.add(rs)
			.add(control.createMock(RowSet.class));
		Variant<Integer> vCnt = new Variant<Integer>(vRs)
			.add(100)
			.add(10);
		Variant<?> iterator = vCnt;
		int foundCnt = 0;
		TradesEntry x, found = null;
		do {
			x = new TradesEntry(vGw.get(), vRs.get(), vCnt.get());
			if ( entry.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gateway, found.getGateway());
		assertSame(rs, found.getRowSet());
		assertEquals(100, found.count());
	}

}
