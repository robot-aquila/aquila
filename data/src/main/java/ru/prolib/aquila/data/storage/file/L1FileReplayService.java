package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.data.CyclicReader;
import ru.prolib.aquila.data.FileReaderFactory;
import ru.prolib.aquila.data.TimeConverter;
import ru.prolib.aquila.data.replay.L1AbstractReplayService;

public class L1FileReplayService extends L1AbstractReplayService {
	protected final FileReaderFactory<? extends L1Update> readerFactory;
	protected int repeat = 1;
	
	public L1FileReplayService(TimeConverter timeConverter,
			FileReaderFactory<? extends L1Update> readerFactory)
	{
		super(timeConverter);
		this.readerFactory = readerFactory;
	}
	
	public void setDataFile(File dataFile) {
		lock.lock();
		try {
			readerFactory.setDataFile(dataFile);
		} finally {
			lock.unlock();
		}
	}
	
	public void setRepeatCount(int repeat) {
		lock.lock();
		try {
			this.repeat = repeat;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public CloseableIterator<? extends TStamped> createReader() throws IOException {
		lock.lock();
		try {
			return new CyclicReader<L1Update>(readerFactory, repeat);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setStartTimeL1(Symbol symbol, Instant time) {
		throw new UnsupportedOperationException();
	}

}
