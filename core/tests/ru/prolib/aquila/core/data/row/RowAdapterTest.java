package ru.prolib.aquila.core.data.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-15<br>
 * $Id$
 */
public class RowAdapterTest {
	private IMocksControl control;
	private Object source = new Object();
	private G<Object> adapter1,adapter2; 
	private Map<String, G<?>> adapters;
	private RowAdapter adapter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		adapter1 = control.createMock(G.class);
		adapter2 = control.createMock(G.class);
		adapters = new HashMap<String, G<?>>();
		adapters.put("foo", adapter1);
		adapters.put("bar", adapter2);
		adapter = new RowAdapter(source, adapters);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(source, adapter.getSource());
		assertEquals(adapters, adapter.getAdapters());
	}
	
	@Test
	public void testGet() throws Exception {
		expect(adapter1.get(same(source))).andReturn("zulu4");
		expect(adapter2.get(same(source))).andReturn(152);
		control.replay();
		
		assertEquals("zulu4", adapter.get("foo"));
		assertEquals(152, adapter.get("bar"));
		assertNull(adapter.get("unknown"));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(adapter.equals(adapter));
		assertFalse(adapter.equals(null));
		assertFalse(adapter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Map<String, G<?>> adapters1 = new HashMap<String, G<?>>();
		adapters1.put("foo", adapter1);
		Variant<Object> vSrc = new Variant<Object>()
			.add(source)
			.add(this);
		Variant<Map<String, G<?>>> vAdp = new Variant<Map<String, G<?>>>(vSrc)
			.add(adapters)
			.add(adapters1);
		Variant<?> iterator = vAdp;
		int foundCnt = 0;
		RowAdapter x = null, found = null;
		do {
			x = new RowAdapter(vSrc.get(), vAdp.get());
			if ( adapter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(source, found.getSource());
		assertEquals(adapters, found.getAdapters());
	}

}
