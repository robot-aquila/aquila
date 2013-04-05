package ru.prolib.aquila.core.data;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolver;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-17<br>
 * $Id: GOrderTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class GOrderTest {
	private static IMocksControl control;
	private static OrderResolver resolver;
	private static EditableOrder order;
	private static G<Long> gTransId;
	private static G<Long> gId;
	private static Row row;
	private static GOrder getter;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() {
		control = createStrictControl();
		resolver = control.createMock(OrderResolver.class);
		order = control.createMock(EditableOrder.class);
		gTransId = control.createMock(G.class);
		gId = control.createMock(G.class);
		row = control.createMock(Row.class);
		getter = new GOrder(resolver, gTransId, gId);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(resolver, getter.getOrderResolver());
		assertSame(gTransId, getter.getTransIdGetter());
		assertSame(gId, getter.getIdGetter());
	}
	
	@Test
	public void testGet_NullIfNoId() throws Exception {
		expect(gId.get(same(row))).andReturn(null);
		expect(gTransId.get(same(row))).andReturn(100L);
		control.replay();
		
		assertNull(getter.get(row));
		
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		expect(gId.get(same(row))).andReturn(123L);
		expect(gTransId.get(same(row))).andReturn(456L);
		expect(resolver.resolveOrder(123L, 456L)).andReturn(order);
		control.replay();
		
		assertSame(order, getter.get(row));
		
		control.verify();
	}
	
	@Test
	public void testGet_IfOrderIdIsNull() throws Exception {
		expect(gId.get(same(row))).andReturn(null);
		expect(gTransId.get(same(row))).andReturn(456L);
		control.replay();
		
		assertNull(getter.get(row));
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<OrderResolver> vRes = new Variant<OrderResolver>()
			.add(null)
			.add(resolver)
			.add(control.createMock(OrderResolver.class));
		Variant<G<Long>> vTransId = new Variant<G<Long>>(vRes)
			.add(null)
			.add(gTransId)
			.add(control.createMock(G.class));
		Variant<G<Long>> vId = new Variant<G<Long>>(vTransId)
			.add(null)
			.add(gId)
			.add(gTransId);
		Variant<?> iterator = vId;
		int foundCnt = 0;
		GOrder found = null;
		do {
			GOrder actual = new GOrder(vRes.get(), vTransId.get(), vId.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(resolver, found.getOrderResolver());
		assertSame(gTransId, found.getTransIdGetter());
		assertSame(gId, found.getIdGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertFalse(getter.equals(this));
		assertFalse(getter.equals(null));
		assertTrue(getter.equals(getter));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, /*0*/64401)
			.append(resolver)
			.append(gTransId)
			.append(gId)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
