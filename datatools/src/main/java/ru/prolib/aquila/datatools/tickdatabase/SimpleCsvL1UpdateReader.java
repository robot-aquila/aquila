package ru.prolib.aquila.datatools.tickdatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SimpleCsvL1UpdateReader implements L1UpdateReader {
	private final BufferedReader reader;
	private final SimpleCsvL1UpdatePacker packer;
	private L1Update lastUpdate;
	private long lineNumber = 0;
	
	public SimpleCsvL1UpdateReader(BufferedReader reader, SimpleCsvL1UpdatePacker packer) {
		super();
		this.reader = reader;
		this.packer = packer;
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
	public boolean nextUpdate() throws IOException {
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
	public L1Update getUpdate() throws IOException {
		return lastUpdate;
	}

}
