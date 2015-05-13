package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

public class DataWriterFactoryImplTest {
	private IMocksControl control;
	private DataSegmentManager segmentManager;
	private DataWriterFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		segmentManager = control.createMock(DataSegmentManager.class);
		factory = new DataWriterFactoryImpl(segmentManager);
	}

	@Test
	public void testCreateWriter() throws Exception {
		SecurityDescriptor descr = new SecurityDescriptor("RTS", "FUT", "USD");
		DataWriterImpl segment = (DataWriterImpl) factory.createWriter(descr);
		assertNotNull(segment);
		assertEquals(descr, segment.getSecurityDescriptor());
		assertSame(segmentManager, segment.getDataSegmentManager());
	}

}
