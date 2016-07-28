package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;
import org.junit.Test;

public class FilesetInfoImplTest {

	@Test
	public void testCtor2() {
		FilesetInfoImpl info = new FilesetInfoImpl("foo", "bar");
		
		assertEquals("foo", info.getRegularSuffix());
		assertEquals("bar", info.getTemporarySuffix());
	}

}
