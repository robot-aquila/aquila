package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SegmentMetaDataImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private SegmentMetaDataImpl data;

	@Before
	public void setUp() throws Exception {
		data = new SegmentMetaDataImpl();
	}
	
	@Test
	public void testSetPath() {
		assertSame(data, data.setPath("foo/path"));
		assertEquals("foo/path", data.getPath());
	}
	
	@Test
	public void testHashCode() {
		assertSame(data, data.setHashCode("zja7h723fas"));
		assertEquals("zja7h723fas", data.getHashCode());
	}
	
	@Test
	public void testSetUpdateTime() {
		assertSame(data, data.setUpdateTime(T("2017-09-12T00:07:00Z")));
		assertEquals(T("2017-09-12T00:07:00Z"), data.getUpdateTime());
	}

	@Test
	public void testSetNumberOfElements() {
		assertSame(data, data.setNumberOfElements(834L));
		assertEquals(834L, data.getNumberOfElements());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(data.equals(data));
		assertFalse(data.equals(null));
		assertFalse(data.equals(this));
	}
	
	@Test
	public void testEquals() {
		data.setPath("zulu/charlie/bar.dat")
			.setHashCode("xxxqqqaaa")
			.setNumberOfElements(250L)
			.setUpdateTime(T("2005-08-15T10:45:05Z"));
		Variant<String> vPath = new Variant<>("zulu/charlie/bar.dat", "foo/path.dat");
		Variant<String> vHash = new Variant<>(vPath, "xxxqqqaaa", "ggghhhxxx");
		Variant<Long> vNoE = new Variant<>(vHash, 250L, 400L);
		Variant<Instant> vTime = new Variant<>(vNoE, T("2005-08-15T10:45:05Z"), T("1996-09-12T00:00:00Z"));
		Variant<?> iterator = vTime;
		int foundCnt = 0;
		SegmentMetaDataImpl x, found = null;
		do {
			x = new SegmentMetaDataImpl()
					.setPath(vPath.get())
					.setHashCode(vHash.get())
					.setNumberOfElements(vNoE.get())
					.setUpdateTime(vTime.get());
			if ( data.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("zulu/charlie/bar.dat", found.getPath());
		assertEquals("xxxqqqaaa", found.getHashCode());
		assertEquals(250L, found.getNumberOfElements());
		assertEquals(T("2005-08-15T10:45:05Z"), found.getUpdateTime());
	}
	
	@Test
	public void testToString() {
		data.setPath("zulu/charlie/bar.dat")
			.setHashCode("xxxqqqaaa")
			.setNumberOfElements(250L)
			.setUpdateTime(T("2005-08-15T10:45:05Z"));
		
		assertEquals("SegmentMetaDataImpl[path=zulu/charlie/bar.dat "
				+ "hash=xxxqqqaaa "
				+ "numberOfElements=250 "
				+ "updateTime=2005-08-15T10:45:05Z]", data.toString());
	}

}
