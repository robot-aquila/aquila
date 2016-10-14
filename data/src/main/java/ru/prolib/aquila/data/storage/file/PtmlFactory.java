package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.data.flex.Flex;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;

public class PtmlFactory {
	private final Flex flex;
	private final PtmlDeltaUpdatePacker packer;
	
	public PtmlFactory(PtmlDeltaUpdateConverter converter, Flex  flex) {
		this.flex = flex;
		this.packer = new PtmlDeltaUpdatePacker(converter);
	}
	
	public PtmlDeltaUpdatePacker getPacker() {
		return packer;
	}
	
	public PtmlFactory(PtmlDeltaUpdateConverter converter) {
		this(converter,  Flex.getInstance());
	}
	
	public DeltaUpdateWriter createWriter(File file) throws IOException {
		return new PtmlDeltaUpdateWriter(flex.createOutputStream(file, true), packer);
	}
	
	public CloseableIterator<DeltaUpdate> createReader(File file) throws IOException {
		return new PtmlDeltaUpdateReader(flex.createInputStream(file), packer);
	}

}
