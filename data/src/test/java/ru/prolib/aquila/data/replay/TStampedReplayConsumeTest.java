package ru.prolib.aquila.data.replay;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.TStamped;

public class TStampedReplayConsumeTest {
	private IMocksControl control;
	private TStampedReplay ownerMock;
	private TStamped objectMock;
	private TStampedReplayConsume consume;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		ownerMock = control.createMock(TStampedReplay.class);
		objectMock = control.createMock(TStamped.class);
		consume = new TStampedReplayConsume(ownerMock, 115L, objectMock);
	}
	
	@Test
	public void testCtor3() {
		assertSame(ownerMock, consume.getOwner());
		assertEquals(115L, consume.getSequenceID());
		assertSame(objectMock, consume.getObject());
	}
	
	@Test
	public void testRun() {
		ownerMock.consume(115, objectMock);
		control.replay();
		
		consume.run();
		
		control.verify();
	}
	
	@Test
	public void testToString() {
		expect(ownerMock.getServiceID()).andReturn("foo-service");
		control.replay();
		
		String actual = consume.toString();
		
		assertEquals("TStampedReplayConsume[srvID=foo-service seqID=115 " + objectMock + "]", actual);
	}

}
