package ru.prolib.aquila.core.data;

import java.io.File;

/**
 * Абстрактный поток тиков на основе набора файлов.
 */
public class TickReaderFromFiles implements Aqiterator<Tick> {
	private final Aqiterator<File> fileset;
	private final TickReaderFactory factory;
	private Aqiterator<Tick> currentReader;
	private boolean closed = false;
	
	public TickReaderFromFiles(Aqiterator<File> fileset,
			TickReaderFactory factory)
	{
		super();
		this.fileset = fileset;
		this.factory = factory;
	}

	@Override
	public void close() {
		if ( currentReader != null ) {
			currentReader.close();
			currentReader = null;
		}
		fileset.close();
		closed = true;
	}

	@Override
	public Tick item() throws DataException {
		if ( currentReader == null || closed ) {
			throw new DataException("No data under cursor");
		}
		return currentReader.item();
	}

	@Override
	public boolean next() throws DataException {
		if ( currentReader != null && currentReader.next() ) {
			return true;
		}
		currentReader = null;
		while ( currentReader == null ) {
			if ( ! fileset.next() ) {
				close();
				return false;
			}
			Aqiterator<Tick> r =
				factory.createTickReader(fileset.item().getAbsolutePath());
			if ( r.next() ) {
				currentReader = r;
				return true;
			}
		}
		return false;
	}

}
