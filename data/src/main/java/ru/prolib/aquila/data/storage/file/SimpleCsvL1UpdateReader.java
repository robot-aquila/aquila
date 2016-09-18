package ru.prolib.aquila.data.storage.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;

public class SimpleCsvL1UpdateReader implements CloseableIterator<L1Update> {
	private final BufferedReader reader;
	private final SimpleCsvL1UpdatePacker packer;
	private L1Update lastUpdate;
	private long lineNumber = 0;
	
	public SimpleCsvL1UpdateReader(BufferedReader reader, SimpleCsvL1UpdatePacker packer) {
		super();
		this.reader = reader;
		this.packer = packer;
	}
	
	public SimpleCsvL1UpdateReader(BufferedReader reader) {
		this(reader, new SimpleCsvL1UpdatePacker());
	}
	
	public SimpleCsvL1UpdateReader(File file) throws FileNotFoundException {
		this(new BufferedReader(new FileReader(file)), new SimpleCsvL1UpdatePacker());
	}
	
	private String readLine() throws IOException {
		try {
			String line = reader.readLine();
			return line == null ? null : line.trim();
		} catch ( IOException e ) {
			throw new IOException("Error reading stream. Line: " + lineNumber, e);
		} finally {
			lineNumber ++;
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public boolean next() throws IOException {
		String line = readLine();
		if ( line != null ) {
			try {
				lastUpdate = packer.unpack(line);
				return true;
			} catch ( SimpleCsvL1FormatException e ) {
				throw new IOException("Error parsing header. Line: " + lineNumber, e);
			}
		}
		return false;
	}

	@Override
	public L1Update item() throws IOException {
		return lastUpdate;
	}

}
