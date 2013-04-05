package ru.prolib.aquila.core.data.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-15<br>
 * $Id$
 */
public class RowSetAdapterTest {
	private IMocksControl control;
	private RowSet source;
	private G<Object> adapter1,adapter2; 
	private Map<String, G<?>> adapters;
	private RowSetAdapter adapter;
	private List<Map<String, Object>> data;

	@Before
	public void setUp() throws Exception {
		data = new Vector<Map<String, Object>>();
		data.add(new HashMap<String, Object>());
		data.get(0).put("foo", 200);
		data.get(0).put("bar", 110);
		data.add(new HashMap<String, Object>());
		data.get(1).put("bar", 924);
		data.add(new HashMap<String, Object>());
		data.get(2).put("foo", 580);
		data.get(2).put("bar", 413);
		source = new ListOfMaps(data);
		adapter1 = new G<Object>() {
			@Override public Object get(Object source) {
				return ((RowSet) source).get("bar");
			}
		};
		adapter2 = new G<Object>() {
			@Override public Object get(Object source) {
				return ((RowSet) source).get("foo");
			}
		};
		adapters = new HashMap<String, G<?>>();
		adapters.put("foo", adapter1);
		adapters.put("bar", adapter2);
		adapter = new RowSetAdapter(source, adapters);
		control = createStrictControl();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(source, adapter.getSource());
		assertEquals(adapters, adapter.getAdapters());
	}
	
	@Test
	public void testReset() throws Exception {
		source = control.createMock(RowSet.class);
		expect(source.next()).andReturn(true);
		expect(source.get("bar")).andReturn(123);
		expect(source.get("foo")).andReturn(-45);
		source.reset();
		expect(source.get("bar")).andReturn(876);
		control.replay();
		
		adapter = new RowSetAdapter(source, adapters);
		adapter.next();
		assertEquals(123, adapter.get("foo"));
		assertEquals(-45, adapter.get("bar"));
		// cached
		assertEquals(123, adapter.get("foo"));
		assertEquals(-45, adapter.get("bar"));

		adapter.reset();
		// check cache cleared
		assertEquals(876, adapter.get("foo"));
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		source = control.createMock(RowSet.class);
		expect(source.next()).andReturn(true);
		expect(source.get("bar")).andReturn(123);
		expect(source.get("foo")).andReturn(-45);
		source.close();
		expect(source.get("bar")).andReturn(876);
		control.replay();
		
		adapter = new RowSetAdapter(source, adapters);
		adapter.next();
		assertEquals(123, adapter.get("foo"));
		assertEquals(-45, adapter.get("bar"));
		// cached
		assertEquals(123, adapter.get("foo"));
		assertEquals(-45, adapter.get("bar"));

		adapter.close();
		// check cache cleared
		assertEquals(876, adapter.get("foo"));
		
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
		Variant<RowSet> vSrc = new Variant<RowSet>()
			.add(new ListOfMaps(new Vector<Map<String, Object>>()))
			.add(new ListOfMaps(data));
		Variant<Map<String, G<?>>> vAdp = new Variant<Map<String, G<?>>>(vSrc)
			.add(adapters)
			.add(adapters1);
		Variant<?> iterator = vAdp;
		int foundCnt = 0;
		RowSetAdapter x = null, found = null;
		do {
			x = new RowSetAdapter(vSrc.get(), vAdp.get());
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
