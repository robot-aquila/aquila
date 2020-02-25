package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Segment writer will commit temporary file when closing.
 */
public class CacheSegmentWriter extends Writer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CacheSegmentWriter.class);
	}
	
	private final File temporary, committed;
	private final Writer writer;
	private boolean closed = false;
	
	CacheSegmentWriter(File temporary, File committed, Writer writer) throws IOException {
		this.temporary = temporary;
		this.committed = committed;
		this.writer = writer;
	}
	
	public CacheSegmentWriter(File target, File commit) throws IOException {
		this(target, commit, new BufferedWriter(new FileWriter(target)));
	}
	
	public File getTemporary() {
		return temporary;
	}
	
	public File getCommitted() {
		return committed;
	}
	
	@Override
	public Writer append(char c) throws IOException {
		writer.append(c);
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq) throws IOException {
		writer.append(csq);
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		writer.append(csq, start, end);
		return this;
	}

	@Override
	public synchronized void close() throws IOException {
		if ( ! closed ) {
			writer.close();
			committed.delete();
			FileUtils.moveFile(temporary, committed);
			closed = true;
			logger.debug("Cache renewed: {}", committed);
		}
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf, off, len);
	}
	
	@Override
	public void write(char[] cbuf) throws IOException {
		writer.write(cbuf);
	}
	
	@Override
	public void write(int c) throws IOException {
		writer.write(c);
	}
	
	@Override
	public void write(String str) throws IOException {
		writer.write(str);
	}
	
	@Override
	public void write(String str, int off, int len) throws IOException {
		writer.write(str, off, len);
	}

}
