package ru.prolib.aquila.data.flex;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File input stream for reading shared files.
 * <p>
 * This class is for safe reading from a file which can be extended with time.
 * This works by acquiring locks for whole file including new parts which were
 * added between IO operations. The writers may acquire an exclusive lock at the
 * end of the file and add new data part. This operation must be done asap.
 * Otherwise this reader may be locked for a long time. This approach is
 * suitable for the files which may be extended at the end but will not work for
 * random access files.
 */
public class FlexInputStream extends InputStream {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FlexInputStream.class);
	}
	
	private final Lock lock;
	private final FileInputStream input;
	private final FileChannel channel;
	private final List<FileLock> fileLocks;
	private long currentLockSize = 0;
	
	public FlexInputStream(FileInputStream input) {
		this.lock = new ReentrantLock();
		this.input = input;
		this.channel = input.getChannel();
		this.fileLocks = new ArrayList<FileLock>();
		logger.debug("Channel: {}", channel);
	}

	@Override
	public int available() throws IOException {
		logger.debug("-> available");
		checkLockRange();
		int result = input.available();
		logger.debug("<- available: return={}", result);
		return result;
	}
	
	@Override
	public void close() throws IOException {
		logger.debug("-> close");
		lock.lock();
		try {
			for ( int i = fileLocks.size() - 1; i >= 0; i -- ) {
				fileLocks.get(i).release();
			}
			fileLocks.clear();
			currentLockSize = 0;
		} finally {
			lock.unlock();
		}
		input.close();
		logger.debug("<- close");
	}
	
	@Override
	public void mark(int readLimit) {
		logger.debug("-> mark: readLimit={}", readLimit);
		input.mark(readLimit);
		logger.debug("<- mark");
	}
	
	@Override
	public boolean markSupported() {
		logger.debug("-> markSupported");
		boolean result = input.markSupported();
		logger.debug("<- markSupported: return={}", result);
		return result;
	}
	
	@Override
	public int read() throws IOException {
		logger.debug("-> read(0)");
		checkLockRange();
		int result = input.read();
		logger.debug("<- read(0): return={}", result);
		return result;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		logger.debug("-> read(1): array.length={}", b.length);
		checkLockRange();
		int result = input.read(b);
		logger.debug("<- read(1): return={}", result);
		return result;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		logger.debug("-> read(3): off={}, len={}", off, len);
		int result = 0;
		checkLockRange();
		long requestedSize = channel.position() + len;
		if ( requestedSize > currentLockSize ) {
			long newLockSize = requestedSize - currentLockSize;
			long newLockPos = currentLockSize;
			logger.debug("Requested length is more than locked. Making a temporary lock: pos={} size={}...", newLockPos, newLockSize);
			FileLock tempLock = channel.lock(newLockPos, newLockSize, true);
			logger.debug("Lock acquired");
			try {
				result = input.read(b, off, len);
			} finally {
				tempLock.release();
			}
		} else {
			result = input.read(b, off, len);
		}
		logger.debug("<- read(3): return={}", result);
		return result;
	}
	
	@Override
	public void reset() throws IOException {
		logger.debug("-> reset");
		checkLockRange();
		input.reset();
		logger.debug("<- reset");
	}
	
	@Override
	public long skip(long n) throws IOException {
		logger.debug("skip(1): n={}", n);
		checkLockRange();
		long result = input.skip(n);
		logger.debug("skip(1): return={}", result);
		return result;
	}
	
	private void checkLockRange() throws IOException {
		lock.lock();
		try {
			long currentFileSize = channel.size();
			if ( currentFileSize > currentLockSize ) {
				long newLockPos = currentLockSize;
				long newLockSize = currentFileSize - currentLockSize;
				fileLocks.add(channel.lock(newLockPos, newLockSize, true));
				currentLockSize = currentFileSize;
				logger.debug("New segment locked: pos={} size={} total={}",
					new Object[] { newLockPos, newLockSize, currentLockSize} );
			}
		} finally {
			lock.unlock();
		}
	}

}
