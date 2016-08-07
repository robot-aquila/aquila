package ru.prolib.aquila.data.storage.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.DeltaUpdate;

public class PtmlDeltaUpdateReader implements CloseableIterator<DeltaUpdate> {
	private final InputStream stream;
	private final BufferedReader reader;
	private final PtmlDeltaUpdatePacker packer;
	private DeltaUpdate lastUpdate;
	
	public PtmlDeltaUpdateReader(InputStream stream, PtmlDeltaUpdatePacker packer) {
		this.stream = stream;
		this.packer = packer;
		reader = new BufferedReader(new InputStreamReader(stream));
	}

	@Override
	public synchronized void close() throws IOException {
		IOUtils.closeQuietly(reader);
		IOUtils.closeQuietly(stream);
	}

	@Override
	public synchronized boolean next() throws IOException {
		lastUpdate = null;
		final StringBuffer buffer = new StringBuffer();
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			if ( line.equals("") ) {
				break;
			}
			buffer.append(line).append("\n");
		}
		if ( buffer.length() > 0 ) {
			try {
				lastUpdate = packer.toUpdate(buffer.toString());
				return true;
			} catch ( DataFormatException e ) {
				throw new IOException(e);
			}
		}
		return false;
	}

	@Override
	public synchronized DeltaUpdate item() throws IOException, NoSuchElementException {
		if ( lastUpdate == null ) {
			throw new NoSuchElementException();
		}
		return lastUpdate;
	}

}
