package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.data.CyclicReader;
import ru.prolib.aquila.data.FileReaderFactory;
import ru.prolib.aquila.data.TimeConverter;
import ru.prolib.aquila.data.replay.L1AbstractReplayService;
import ru.prolib.aquila.data.replay.L1AbstractReplayServiceTest;

public class L1FileReplayServiceTest extends L1AbstractReplayServiceTest {
	private L1FileReplayService service;
	private FileReaderFactory<L1Update> readerFactoryMock;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected L1AbstractReplayService createService(TimeConverter timeConverter) {
		readerFactoryMock = control.createMock(FileReaderFactory.class);
		service = new L1FileReplayService(timeConverter, readerFactoryMock);
		return service;
	}
	
	@Test
	public void testSetDataFile() {
		File file = new File("foo/bar");
		readerFactoryMock.setDataFile(file);
		control.replay();
		
		service.setDataFile(file);
		
		control.verify();
	}

	@Test
	public void testCreateReader() throws Exception {
		service.setRepeatCount(10);
		control.replay();
		
		CyclicReader<? extends TStamped> reader = (CyclicReader<? extends TStamped>) service.createReader();
		
		control.verify();
		assertEquals(readerFactoryMock, reader.getReaderFactory());
		assertEquals(10, reader.getRepeat());
	}

}
