package ru.prolib.aquila.data.storage.file;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;

/**
 * Plain-text, multiline delta-update writer.
 * <p>
 * This class allows append delta updates to the file. On construction the
 * writer locks the file from the current position to the infinity. Do write as
 * soon as possible then close the writer because it is critical to readers
 * which may wait while file locked.
 */
public class PtmlDeltaUpdateWriter implements DeltaUpdateWriter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PtmlDeltaUpdateWriter.class);
	}
	
	private final FileOutputStream stream;
	private final BufferedWriter writer;
	private final PtmlDeltaUpdatePacker packer;
	private final FileLock fileLock;

	public PtmlDeltaUpdateWriter(FileOutputStream stream, PtmlDeltaUpdatePacker packer)
			throws IOException
	{
		this.stream = stream;
		this.packer = packer;
		writer = new BufferedWriter(new OutputStreamWriter(stream));
		try {
			FileChannel channel = stream.getChannel();
			long pos = channel.position();
			fileLock = channel.lock(pos, Long.MAX_VALUE - pos, false);
		} catch ( IOException e ) {
			close();
			throw e;
		}
	}

	@Override
	public synchronized void close() throws IOException {
		if ( fileLock != null && fileLock.isValid() ) {
			try {
				fileLock.release();
			} catch ( IOException e ) {
				logger.error("Unexpected exception: ", e);
			}
		}
		IOUtils.closeQuietly(writer);
		IOUtils.closeQuietly(stream);
	}

	@Override
	public synchronized void writeUpdate(DeltaUpdate update)
			throws IOException, DataFormatException
	{
		writer.write(packer.toString(update));
		writer.write("\n");
	}

}
