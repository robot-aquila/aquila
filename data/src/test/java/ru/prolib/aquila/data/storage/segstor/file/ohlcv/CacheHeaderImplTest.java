package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CacheHeaderImplTest {
	private List<CacheSourceDescriptor> descriptorsStub1, descriptorsStub2, descriptorsStub3;
	private CacheHeaderImpl header;

	@Before
	public void setUp() throws Exception {
		descriptorsStub1 = new ArrayList<>();
		descriptorsStub2 = new ArrayList<>();
		descriptorsStub3 = new ArrayList<>();
		header = new CacheHeaderImpl(descriptorsStub1);
	}
	
	@Test
	public void testGetNumberOfSourceDescriptors() {
		assertEquals(0, header.getNumberOfSourceDescriptors());
		
		descriptorsStub1.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub1.add(new CacheSourceDescriptor("bar bar/path"));
		
		assertEquals(2, header.getNumberOfSourceDescriptors());
	}
	
	@Test
	public void testGetNumberOfElements() {
		assertEquals(0, header.getNumberOfElements());
		
		assertSame(header, header.setNumberOfElements(800L));
		
		assertEquals(800, header.getNumberOfElements());
	}
	
	@Test
	public void testGetSourceDescriptors() {
		descriptorsStub1.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub1.add(new CacheSourceDescriptor("bar bar/path"));

		List<CacheSourceDescriptor> actual = header.getSourceDescriptors();
		
		List<CacheSourceDescriptor> expected = new ArrayList<>();
		expected.add(new CacheSourceDescriptor("foo foo/path"));
		expected.add(new CacheSourceDescriptor("bar bar/path"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddSourceDescriptor() {
		assertSame(header, header.addSourceDescriptor("foo foo/path"));
		assertSame(header, header.addSourceDescriptor("bar", "bar/path"));
		assertSame(header, header.addSourceDescriptor(new CacheSourceDescriptor("buz", "buz/path")));

		List<CacheSourceDescriptor> actual = header.getSourceDescriptors();
		
		List<CacheSourceDescriptor> expected = new ArrayList<>();
		expected.add(new CacheSourceDescriptor("foo foo/path"));
		expected.add(new CacheSourceDescriptor("bar bar/path"));
		expected.add(new CacheSourceDescriptor("buz buz/path"));
		assertEquals(expected, actual);
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(header.equals(header));
		assertFalse(header.equals(null));
		assertFalse(header.equals(this));
	}
	
	@Test
	public void testEquals() {
		header.setNumberOfElements(1000L);
		descriptorsStub1.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub1.add(new CacheSourceDescriptor("bar bar/path"));
		descriptorsStub2.add(new CacheSourceDescriptor("bar bar/path"));
		descriptorsStub2.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub3.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub3.add(new CacheSourceDescriptor("bar bar/path"));
		descriptorsStub3.add(new CacheSourceDescriptor("buz buz/path"));

		Variant<List<CacheSourceDescriptor>> vDesc = new Variant<List<CacheSourceDescriptor>>()
				.add(descriptorsStub1)
				.add(descriptorsStub2)
				.add(descriptorsStub3);
		Variant<Long> vNum = new Variant<>(vDesc, 1000L, 200L);
		Variant<?> iterator = vNum;
		int foundCnt = 0;
		CacheHeaderImpl x, found = null;
		do {
			x = new CacheHeaderImpl(vDesc.get());
			x.setNumberOfElements(vNum.get());
			if ( header.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(descriptorsStub1, found.getSourceDescriptors());
		assertEquals(1000L, found.getNumberOfElements());
	}
	
	@Test
	public void testToString() {
		header.setNumberOfElements(1000L);
		descriptorsStub1.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub1.add(new CacheSourceDescriptor("bar bar/path"));

		String expected = "2 1000 [foo foo/path, bar bar/path]";
		assertEquals(expected, header.toString());
	}
	
	@Test
	public void testGetSourceDescriptor() {
		descriptorsStub1.add(new CacheSourceDescriptor("foo foo/path"));
		descriptorsStub1.add(new CacheSourceDescriptor("bar bar/path"));
		descriptorsStub1.add(new CacheSourceDescriptor("buz buz/path"));

		assertEquals(new CacheSourceDescriptor("foo foo/path"), header.getSourceDescriptor(0));
		assertEquals(new CacheSourceDescriptor("bar bar/path"), header.getSourceDescriptor(1));
		assertEquals(new CacheSourceDescriptor("buz buz/path"), header.getSourceDescriptor(2));
	}

}
