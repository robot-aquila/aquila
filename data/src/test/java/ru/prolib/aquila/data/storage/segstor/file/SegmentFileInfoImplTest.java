package ru.prolib.aquila.data.storage.segstor.file;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SegmentFileInfoImplTest {
	private SegmentFileInfoImpl info;

	@Before
	public void setUp() throws Exception {
		info = new SegmentFileInfoImpl();
	}

	@Test
	public void testSetFullPath1() {
		assertSame(info, info.setFullPath(new File("foo/bar.dat")));
		
		assertEquals(new File("foo/bar.dat"), info.getFullPath());
		assertEquals(new File("foo"), info.getDirectory());
		assertEquals("bar.dat", info.getBaseName());
		assertEquals("", info.getNameSuffix());
	}
	
	@Test
	public void testSetFullPath3() {
		assertSame(info, info.setFullPath(new File("foo"), "bar", ".dat"));

		assertEquals(new File("foo/bar.dat"), info.getFullPath());
		assertEquals(new File("foo"), info.getDirectory());
		assertEquals("bar", info.getBaseName());
		assertEquals(".dat", info.getNameSuffix());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(info.equals(info));
		assertFalse(info.equals(null));
		assertFalse(info.equals(this));
	}
	
	@Test
	public void testEquals() {
		info.setFullPath(new File("foo"), "bar.dat", "");
		Variant<File> vDir = new Variant<>(new File("foo"), new File("buz"));
		Variant<String> vBase = new Variant<>(vDir, "bar.dat", "bar");
		Variant<String> vSuff = new Variant<>(vBase, "", ".dat");
		Variant<?> iterator = vSuff;
		int foundCnt = 0;
		SegmentFileInfoImpl x, found = null;
		do {
			x = new SegmentFileInfoImpl().setFullPath(vDir.get(), vBase.get(), vSuff.get());
			if ( info.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new File("foo/bar.dat"), found.getFullPath());
		assertEquals(new File("foo"), found.getDirectory());
		assertEquals("bar.dat", found.getBaseName());
		assertEquals("", found.getNameSuffix());
	}

}
