package ru.prolib.aquila.data.flex;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.data.flex.FlexInputStream;

@Ignore
public class FlexTest {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FlexTest.class);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testSafeAppender() throws Exception {
		// Thread #1 (reader):
		// * open stream for reading
		// * wait until the second thread started and signaled to ready
		// * read stream until end and cache the data
		// - expected to be locked here until the thread #2 finished
		// * send signal - finished
		//
		// Thread #2 (reader and writer)
		// * open stream for writing and lock the end of file (file size + Long.MAX_VALUE)
		// * make a ready signal
		// * open stream for reading
		// * read stream until end, cache the data and close the input stream
		// * write some data after end of the file, release lock and close the output stream
		// * send signal - finished
		//
		// Expected result: thread #1 should read all data written by thread #2
		
		final File file = File.createTempFile("safe-appender-", ".test");
		file.deleteOnExit();
		List<String> existingLines = new ArrayList<>();
		existingLines.add("foo");
		existingLines.add("bar");
		existingLines.add("buzz");
		FileUtils.writeLines(file, existingLines);
		
		assertEquals(new File("fixture/dummy").getAbsolutePath(), new File("D:/work/aquila/data/fixture/dummy").getAbsolutePath());
		logger.debug(new File("D:/work/aquila/data/fixture/dummy").getAbsolutePath());
		logger.debug(new File("fixture/dummy").getAbsolutePath());
		
		final List<String> linesByThread1 = new Vector<String>();
		final List<String> linesByThread2 = new Vector<String>();
		final CountDownLatch thread2Ready = new CountDownLatch(1),
				thread1Finished = new CountDownLatch(1),
				thread2Finished = new CountDownLatch(1);
		Thread thread1 = new Thread("Thread#1") {
			@Override
			public void run() {
				FileInputStream is = null;
				BufferedReader reader = null;
				try {
					is = new FileInputStream(file);
					reader = new BufferedReader(new InputStreamReader(new FlexInputStream(is)), 8);
					linesByThread1.add(reader.readLine());
					if ( ! thread2Ready.await(1, TimeUnit.SECONDS) ) {
						return;
					}
					for ( ;; ) {
						logger.debug("reading the next line...");
						String line = reader.readLine();
						if ( line == null ) {
							break;
						}
						linesByThread1.add(line);
					}
					logger.debug("End of initial data segment reached. Stop reading.");
					thread1Finished.countDown();
				} catch ( Exception e ) {
					logger.error("Unexpected exception", e);
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
		};
		Thread thread2 = new Thread("Thread#2") {
			@Override
			public void run() {
				FileOutputStream os = null;
				BufferedWriter writer = null;
				try {
					os = new FileOutputStream(file, true);
					FileChannel osChannel = os.getChannel();
					logger.debug("Channel: {}", osChannel);
					long pos = osChannel.position(); // should be at the end of the file
					logger.debug("OutputStream current position: {}. Locking...", pos);
					osChannel.lock(pos, Long.MAX_VALUE - pos, false);
					logger.debug("Lock acquired. Sending ready signal...");
					thread2Ready.countDown();
					Thread.sleep(50);
					
					// TODO: read lines
					
					logger.debug("Start writing new data....");
					writer = new BufferedWriter(new OutputStreamWriter(os));
					//writer.write("zulu24\n");
					//writer.write("charlie\n");
					logger.debug("New data written.");
					thread2Finished.countDown();
					logger.debug("Thread finished");
				} catch ( Exception e ) {
					logger.error("Unexpected exception", e);
				} finally {
					IOUtils.closeQuietly(writer);
					IOUtils.closeQuietly(os);
				}
			}
		};
		
		thread1.start();
		thread2.start();
		assertTrue(thread1Finished.await(3, TimeUnit.SECONDS));
		assertTrue(thread2Finished.await(3, TimeUnit.SECONDS));
		
		System.out.println(linesByThread1);
	}

}
