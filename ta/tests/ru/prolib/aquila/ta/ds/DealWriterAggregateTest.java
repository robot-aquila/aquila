package ru.prolib.aquila.ta.ds;

import java.util.Date;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ta.DealImpl;
import ru.prolib.aquila.ta.ds.DealWriter;
import ru.prolib.aquila.ta.ds.DealWriterAggregate;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DealWriterAggregateTest {
	private IMocksControl control;
	private DealWriterAggregate writer;
	private DealWriter w1,w2,w3;
	private DealImpl deal;

	@Before
	public void setUp() throws Exception {
		deal = new DealImpl(new Date(), 123.45d, 100l);
		control = createControl();
		w1 = control.createMock(DealWriter.class);
		w2 = control.createMock(DealWriter.class);
		w3 = control.createMock(DealWriter.class);
		writer = new DealWriterAggregate();
	}
	
	@Test
	public void testAddDeal_FalseIfNoWriters() throws Exception {
		assertFalse(writer.addDeal(deal));
	}
	
	@Test
	public void testAddDeal_FalseIfAllWritersReturnFalse() throws Exception {
		expect(w1.addDeal(deal)).andReturn(false);
		expect(w2.addDeal(deal)).andReturn(false);
		expect(w3.addDeal(deal)).andReturn(false);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		control.replay();
		assertFalse(writer.addDeal(deal));
		control.verify();
	}
	
	@Test
	public void testAddDeal_TrueIfOneWritersReturnTrue() throws Exception {
		expect(w1.addDeal(deal)).andReturn(false);
		expect(w2.addDeal(deal)).andReturn(true);
		expect(w3.addDeal(deal)).andReturn(false);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		control.replay();
		assertTrue(writer.addDeal(deal));
		control.verify();
	}
	
	@Test
	public void testAddDeal_TrueIfFewWritersReturnTrue() throws Exception {
		expect(w1.addDeal(deal)).andReturn(false);
		expect(w2.addDeal(deal)).andReturn(true);
		expect(w3.addDeal(deal)).andReturn(true);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		control.replay();
		assertTrue(writer.addDeal(deal));
		control.verify();
	}
	
	@Test
	public void testFlush_FalseIfNoWriters() throws Exception {
		assertFalse(writer.flush());
	}
	
	@Test
	public void testFlush_FalseIfAllWritersReturnFalse() throws Exception {
		expect(w1.flush()).andReturn(false);
		expect(w2.flush()).andReturn(false);
		expect(w3.flush()).andReturn(false);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		control.replay();
		assertFalse(writer.flush());
		control.verify();
	}
	
	@Test
	public void testFlush_TrueIfOneOfWritersReturnTrue() throws Exception {
		expect(w1.flush()).andReturn(false);
		expect(w2.flush()).andReturn(false);
		expect(w3.flush()).andReturn(true);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		control.replay();
		assertTrue(writer.flush());
		control.verify();
	}
	
	@Test
	public void testFlush_TrueIfFewWritersReturnTrue() throws Exception {
		expect(w1.flush()).andReturn(true);
		expect(w2.flush()).andReturn(true);
		expect(w3.flush()).andReturn(false);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		control.replay();
		assertTrue(writer.flush());
		control.verify();
	}
	
	@Test
	public void testDetachWriter() throws Exception {
		expect(w1.addDeal(deal)).andReturn(false);
		expect(w3.addDeal(deal)).andReturn(false);
		expect(w1.flush()).andReturn(false);
		expect(w3.flush()).andReturn(true);
		writer.attachWriter(w1);
		writer.attachWriter(w2);
		writer.attachWriter(w3);
		writer.detachWriter(w2);
		control.replay();
		assertFalse(writer.addDeal(deal));
		assertTrue(writer.flush());
		control.verify();
	}

}
