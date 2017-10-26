package ru.prolib.aquila.utils.experimental.sst.sdp2;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.ZTFrame;

public class SDP2DataSliceFactoryImplTest {
	private IMocksControl control;
	private EventQueue queueMock;
	private SDP2DataSliceFactoryImpl<SDP2Key> factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		factory = new SDP2DataSliceFactoryImpl<>(queueMock);
	}

	@Test
	public void testProduce() {
		SDP2DataSliceImpl<SDP2Key> slice = (SDP2DataSliceImpl<SDP2Key>)
				factory.produce(new SDP2Key(ZTFrame.M10));
		
		assertNotNull(slice);
		assertSame(queueMock, slice.getEventQueue());
		assertEquals(new SDP2Key(ZTFrame.M10), slice.getKey());
	}

}
