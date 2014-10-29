package ru.prolib.aquila.core.data.finam.storage;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;

import org.easymock.IMocksControl;
import org.joda.time.LocalDate;
import org.junit.*;

import ru.prolib.aquila.core.data.Aqiterator;

public class FileEntry2FileIteratorTest {
	private IMocksControl control;
	private Aqiterator<FileEntry> decorated,decorated2;
	private FileEntry2FileIterator iterator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		decorated = control.createMock(Aqiterator.class);
		decorated2 = control.createMock(Aqiterator.class);
		iterator = new FileEntry2FileIterator(decorated);
	}
	
	@Test
	public void testClose() throws Exception {
		decorated.close();
		control.replay();
		
		iterator.close();
		
		control.verify();
	}
	
	@Test
	public void testItem() throws Exception {
		File file = new File("foo/bar");
		expect(decorated.item())
			.andReturn(new FileEntry(file, new LocalDate(2014, 10, 29)));
		control.replay();
		
		assertSame(file, iterator.item());
		
		control.verify();
	}
	
	@Test
	public void testNext() throws Exception {
		expect(decorated.next()).andReturn(true);
		expect(decorated.next()).andReturn(false);
		control.replay();
		
		assertTrue(iterator.next());
		assertFalse(iterator.next());
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(iterator.equals(iterator));
		assertTrue(iterator.equals(new FileEntry2FileIterator(decorated)));
		assertFalse(iterator.equals(new FileEntry2FileIterator(decorated2)));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}


}
