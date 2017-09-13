package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CacheSourceDescriptorTest {
	private CacheSourceDescriptor descr;

	@Before
	public void setUp() throws Exception {
		descr = new CacheSourceDescriptor("X562jHas8h1d", "foo/bar/uri");
	}
	
	@Test
	public void testCtor2() {
		assertEquals("X562jHas8h1d", descr.getHashCode());
		assertEquals("foo/bar/uri", descr.getPath());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor2_ThrowsIfHashCodeHasSpaces() {
		new CacheSourceDescriptor("X562jHas 8h1d", "boo/bar/uri");
	}
	
	@Test
	public void testCtor1() {
		descr = new CacheSourceDescriptor("X562jHas8h1d foo/bar/uri");
		assertEquals("X562jHas8h1d", descr.getHashCode());
		assertEquals("foo/bar/uri", descr.getPath());
	}
	
	@Test
	public void testCtor1_MoreThanTwoSpacesOK() {
		descr = new CacheSourceDescriptor("X562jHas8h1d foo/bar in my mind/uri");
		assertEquals("X562jHas8h1d", descr.getHashCode());
		assertEquals("foo/bar in my mind/uri", descr.getPath());		
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor1_ThrowsIfNoOneSpace() {
		new CacheSourceDescriptor("X562jHas8h1dfoo/bar/uri");
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(descr.equals(descr));
		assertFalse(descr.equals(null));
		assertFalse(descr.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<String> vHash = new Variant<>("X562jHas8h1d", "Z172ksas9osd");
		Variant<String> vPath = new Variant<>(vHash, "foo/bar/uri", "zoo/buz/gee");
		Variant<?> iterator = vPath;
		int foundCnt = 0;
		CacheSourceDescriptor x, found = null;
		do {
			x = new CacheSourceDescriptor(vHash.get(), vPath.get());
			if ( descr.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("X562jHas8h1d", found.getHashCode());
		assertEquals("foo/bar/uri", found.getPath());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(1197, 1375)
				.append("X562jHas8h1d")
				.append("foo/bar/uri")
				.toHashCode(), descr.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals("X562jHas8h1d foo/bar/uri", descr.toString());
	}

}
