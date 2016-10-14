package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;
import static ru.prolib.aquila.data.storage.file.PtmlDeltaUpdateStorageCLI.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

public class PtmlDeltaUpdateReaderTest {
	private File file;
	private FileInputStream stream;
	private PtmlDeltaUpdatePacker packer;
	private PtmlDeltaUpdateReader reader;
	
	private PtmlDeltaUpdateReader createReader() throws IOException {
		if ( reader != null ) {
			reader.close();
		}
		stream = new FileInputStream(file);
		packer = new PtmlDeltaUpdatePacker(new SampleConverter());
		return reader = new PtmlDeltaUpdateReader(stream, packer);
	}

	@Before
	public void setUp() throws Exception {
		file = File.createTempFile("ptml-test-", ".tmp");
		file.deleteOnExit();
	}

	@After
	public void tearDown() throws Exception {
		IOUtils.closeQuietly(reader);
		IOUtils.closeQuietly(stream);
		reader = null;
	}

	@Test
	public void testNext() throws Exception {
		createReader();
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
		
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();

		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder().withTime(T("2017-12-01T00:00:00Z"))
				.withSnapshot(true)
				.withToken(1, "foo")
				.withToken(2, "test")
				.buildUpdate());
		expected.add(new DeltaUpdateBuilder().withTime(T("2017-12-01T00:01:00Z"))
				.withSnapshot(false)
				.withToken(2, "zulu24")
				.buildUpdate());
		expected.add(new DeltaUpdateBuilder().withTime(T("2017-12-01T00:02:00Z"))
				.withSnapshot(false)
				.withToken(1, "alpha")
				.withToken(2, "beta")
				.buildUpdate());
		assertEquals(expected, actual);
	}

}
