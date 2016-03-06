package ru.prolib.aquila.datatools.tickdatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.L1Update;

public class SimpleCsvL1UpdateWriter implements L1UpdateWriter {
	private final Writer writer;
	private final SimpleCsvL1UpdatePacker packer;
	
	public SimpleCsvL1UpdateWriter(Writer writer, SimpleCsvL1UpdatePacker packer) {
		super();
		this.writer = writer;
		this.packer = packer;
	}
	
	public SimpleCsvL1UpdateWriter(Writer writer) {
		this(writer, new SimpleCsvL1UpdatePacker());
	}
	
	public SimpleCsvL1UpdateWriter(File file) throws IOException {
		this(new BufferedWriter(new FileWriter(file)));
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void writeUpdate(L1Update update) throws IOException {
		writer.write(packer.pack(update));
		writer.write(System.lineSeparator());
	}

	@Override
	public void flush(Instant time) throws IOException {
		writer.flush();
	}

}
