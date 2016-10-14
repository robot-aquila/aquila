package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;
import static ru.prolib.aquila.data.storage.file.PtmlDeltaUpdateStorageCLI.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

public class PtmlDeltaUpdateWriterTest {
	private File file;
	private FileOutputStream stream;
	private PtmlDeltaUpdatePacker packer;
	private PtmlDeltaUpdateWriter writer;
	
	private PtmlDeltaUpdateWriter createWriter() throws IOException {
		if ( writer != null ) {
			writer.close();
		}
		stream = new FileOutputStream(file, true);
		packer = new PtmlDeltaUpdatePacker(new SampleConverter());
		return writer = new PtmlDeltaUpdateWriter(stream, packer);
	}

	@Before
	public void setUp() throws Exception {
		file = File.createTempFile("ptml-test-", ".tmp");
		file.deleteOnExit();
	}
	
	@After
	public void tearDown() throws Exception {
		IOUtils.closeQuietly(writer);
		IOUtils.closeQuietly(stream);
		writer = null;
	}

	@Test
	public void testWriteUpdate() throws Exception {
		createWriter();
		writer.writeUpdate(new DeltaUpdateBuilder().withTime(T("2017-12-01T00:00:00Z"))
				.withSnapshot(true)
				.withToken(1, "foo")
				.withToken(2, "test")
				.buildUpdate());
		writer.writeUpdate(new DeltaUpdateBuilder().withTime(T("2017-12-01T00:01:00Z"))
				.withSnapshot(false)
				.withToken(2, "zulu24")
				.buildUpdate());
		writer.writeUpdate(new DeltaUpdateBuilder().withTime(T("2017-12-01T00:02:00Z"))
				.withSnapshot(false)
				.withToken(1, "alpha")
				.withToken(2, "beta")
				.buildUpdate());
		writer.close();
		
		List<String> actual = FileUtils.readLines(file);
		
		List<String> expected = new ArrayList<>();
		expected.add("1:2017-12-01T00:00:00Z");
		expected.add("1:foo");
		expected.add("2:test");
		expected.add("");
		expected.add("0:2017-12-01T00:01:00Z");
		expected.add("2:zulu24");
		expected.add("");
		expected.add("0:2017-12-01T00:02:00Z");
		expected.add("1:alpha");
		expected.add("2:beta");
		expected.add("");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWriteUpdate_ExLockTheTail() throws Exception {
		List<String> fixture = new ArrayList<>();
		fixture.add("1:2017-12-01T00:00:00Z");
		fixture.add("1:foo");
		fixture.add("2:test");
		fixture.add("");
		fixture.add("0:2017-12-01T00:01:00Z");
		fixture.add("2:zulu24");
		fixture.add("");
		fixture.add("0:2017-12-01T00:02:00Z");
		fixture.add("1:alpha");
		fixture.add("2:beta");
		fixture.add("");
		FileUtils.writeLines(file, fixture);
		final long pos = file.length();
		
		createWriter();

		final List<String> threadResult = new ArrayList<>();
		final CountDownLatch threadFinished = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				FileInputStream is = null;
				try {
					is = new FileInputStream(file);
					FileChannel channel = is.getChannel();
					channel.lock(0, pos, true);
					threadResult.add("nose locked");
					try {
						channel.lock(pos, Long.MAX_VALUE - pos, true);
						threadResult.add("tail locked");
					} catch ( OverlappingFileLockException e ) {
						threadResult.add("lock the tail failed");
					}
					threadFinished.countDown();
				} catch ( IOException e ) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
		}.start();
		assertTrue(threadFinished.await(1, TimeUnit.SECONDS));
		List<String> expectedResult = new ArrayList<>();
		expectedResult.add("nose locked");
		expectedResult.add("lock the tail failed");
		assertEquals(expectedResult, threadResult);
	}

}
