package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;
import static ru.prolib.aquila.data.storage.file.PtmlDeltaUpdateStorageCLI.*;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

public class PtmlIT {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PtmlIT.class);
	}
	
	static class Worker extends Thread {
		private final PtmlFactory ptmlFactory;
		private final File file;
		private final List<DeltaUpdate> updates;
		private final int readFirstAndWait;
		private final CountDownLatch signal;
		private boolean hasError = false;
		
		Worker(PtmlFactory ptmlFactory, File file, int readFirstAndWait, CountDownLatch signal) {
			this.ptmlFactory = ptmlFactory;
			this.file = file;
			this.readFirstAndWait = readFirstAndWait;
			this.signal = signal;
			updates = new ArrayList<>();
		}
		
		Worker(PtmlFactory storage, File file) {
			this(storage, file, 0, null);
		}
		
		public DeltaUpdate getLastUpdate() {
			synchronized ( updates ) {
				if ( updates.size() == 0 ) {
					throw new NoSuchElementException();
				}
				return updates.get(updates.size() - 1);
			}
		}
		
		public boolean hasError() {
			synchronized ( updates ) {
				return hasError;
			}
		}
		
		@Override
		public void run() {
			try ( CloseableIterator<DeltaUpdate> it = ptmlFactory.createReader(file) ) {
				int numRead = 0;
				boolean wait = false;
				while ( it.next() ) {
					synchronized ( updates ) {
						updates.add(it.item());
					}
					numRead ++;
					if ( readFirstAndWait > 0 && numRead >= readFirstAndWait ) {
						wait = true;
						break;
					}
				}
				if ( wait ) {
					if ( signal.await(3, TimeUnit.SECONDS) ) {
						while ( it.next() ) {
							synchronized ( updates ) {
								updates.add(it.item());
							}
						}
					}
				}
			} catch ( Exception e ) {
				logger.error("Unexpected exception: ", e);
				synchronized ( updates ) {
					hasError = true;
				}
			}
		}
		
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private PtmlFactory ptmlFactory;
	private File file;

	@Before
	public void setUp() throws Exception {
		ptmlFactory = new PtmlFactory(new SampleConverter());
		file = File.createTempFile("ptml-storage-it-", ".tmp");
		file.deleteOnExit();
	}

	@After
	public void tearDown() throws Exception {
		file.delete();
	}

	@Test
	@Ignore // TODO: Flex, flex, flex
	public void stress() throws Exception {
		setUp();
		for ( int i = 0; i < 100; i ++ ) {
			try {
				pass();				
			} catch ( Throwable t ) {
				logger.error("Exception at " + i + " pass", t);
				throw t;
			} finally {
				tearDown();				
			}
		}
	}

	private void pass() throws Exception {
		// I don't know how formulate a test which will be more reliable.
		Instant baseTime = T("2017-12-01T00:00:00Z");
		List<String> fixture = new ArrayList<>();
		fixture.add("1:" + baseTime);
		fixture.add("1:foo");
		fixture.add("2:test");
		fixture.add("");
		// Make the data greater than the BufferedReader cache size
		for ( int i = 0; i < 4000; i ++ ) {
			fixture.add("0:" + baseTime.plusSeconds(i));
			fixture.add("1:token1value#" + i);
			fixture.add("2:token2value#" + i);
			fixture.add("");
		}
		FileUtils.writeLines(file, fixture);

		RemoteAPI cli = new RemoteAPI(false, false);
		CountDownLatch signal = new CountDownLatch(1);
		
		List<Worker> primaryWorkers = new ArrayList<>();
		for ( int i = 1; i < 5000; i += 200 ) {
			Worker thread = new Worker(ptmlFactory, file, 1, signal);
			primaryWorkers.add(thread);
		}
		for ( Worker thread : primaryWorkers ) {
			thread.start();
		}
		
		cli.open(file);
		cli.write(new DeltaUpdateBuilder().withTime(T("2017-12-01T20:00:00Z"))
				.withSnapshot(false)
				.withToken(1, "gamma")
				.withToken(2, "kappa")
				.buildUpdate());
		cli.close();
		signal.countDown();
		cli.exit();

		Worker thread2 = new Worker(ptmlFactory,file);
		thread2.start();
		//for ( String dummy : cli.getOutputLines() ) {
		//	System.out.println("OUT> " + dummy);
		//}
		//for ( String dummy : cli.getErrorLines() ) {
		//	System.out.println("ERR> " + dummy);
		//}
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withTime(T("2017-12-01T20:00:00Z"))
			.withSnapshot(false)
			.withToken(1, "gamma")
			.withToken(2, "kappa")
			.buildUpdate();
		for ( Worker thread : primaryWorkers ) {
			thread.join(2000); assertFalse(thread.isAlive());
		}
		thread2.join(2000); assertFalse(thread2.isAlive());

		for ( Worker thread : primaryWorkers ) {
			assertFalse(thread.hasError());
		}
		assertFalse(thread2.hasError());
		assertEquals(expected, thread2.getLastUpdate());
	}

}
