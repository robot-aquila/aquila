package ru.prolib.aquila.data.storage.file;

import ru.prolib.aquila.data.DifferenceTimeConverter;
import ru.prolib.aquila.data.TimeConverter;

public class SimpleCsvL1UpdateReplayService extends L1FileReplayService {

	public SimpleCsvL1UpdateReplayService(TimeConverter timeConverter) {
		super(timeConverter, new SimpleCsvL1UpdateReaderFactory(null, timeConverter));
	}
	
	public SimpleCsvL1UpdateReplayService() {
		this(new DifferenceTimeConverter());
	}
	
}
