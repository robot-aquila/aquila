package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import org.junit.*;

public class FileNameEncoderTest {
	private FileNameEncoder encoder; 

	@Before
	public void setUp() throws Exception {
		encoder = new FileNameEncoder();
	}

	@Test
	public void testEncode() throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(".", "%2E");
		map.put("-", "%2D");
		map.put("*", "%2A");
		map.put("_", "%5F");
		map.put(" ", "%20");
		
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			Entry<String, String> pair = it.next();
			String encoded = encoder.encode(pair.getKey());
			String decoded = URLDecoder.decode(encoded, "UTF-8");
			assertEquals("For " + pair.getKey(), pair.getValue(), encoded);
			assertEquals("For " + encoded, pair.getKey(), decoded);
		}
	}

}
