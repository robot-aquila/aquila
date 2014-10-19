package ru.prolib.aquila.core.data;

/**
 * Абстрактный поток тиков на основе набора файлов.
 * <p>
 * 
 */
public class TickReaderFromFiles implements TickReader {
	private final FileIterator fileset;
	private final TickReaderFactory factory;
	private TickReader currentReader;
	private boolean closed = false;
	
	public TickReaderFromFiles(FileIterator fileset,
			TickReaderFactory factory)
	{
		super();
		this.fileset = fileset;
		this.factory = factory;
	}

	@Override
	public void close() {
		fileset.close();
		closed = true;
	}

	@Override
	public Tick current() throws DataException {
		if ( currentReader == null || closed ) {
			throw new DataException("No data under cursor");
		}
		return currentReader.current();
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
			TickReader r = factory.createTickReader(fileset.current()
					.getAbsolutePath());
			if ( r.next() ) {
				currentReader = r;
				return true;
			}
		}
		return false;
	}

}
