package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;

@SuppressWarnings("unchecked")
public class DataStorageImplTest {
	private IMocksControl control;
	private TickIteratorStorage iteratorStorage;
	private TickTemporalStorage temporalStorage;
	private DataStorageImpl storage;
	private Aqiterator<Tick> it;
	private Aqtemporal<Tick> tmp;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		it = control.createMock(Aqiterator.class);
		tmp = control.createMock(Aqtemporal.class);
		iteratorStorage = control.createMock(TickIteratorStorage.class);
		temporalStorage = control.createMock(TickTemporalStorage.class);
		storage = new DataStorageImpl();
	}
	
	@Test
	public void testGetIterator_Str() throws Exception {
		DateTime t = DateTime.now();
		expect(iteratorStorage.getIterator("foo", t)).andReturn(it);
		control.replay();
		storage.setIteratorStorage(iteratorStorage);
		
		assertSame(it, storage.getIterator("foo", t));
		
		control.verify();
	}
	
	@Test
	public void testGetIterator_Dsc() throws Exception {
		DateTime t = DateTime.now();
		SecurityDescriptor descr = new SecurityDescriptor("SBER", "EQBR", "RUR");
		expect(iteratorStorage.getIterator(descr, t)).andReturn(it);
		control.replay();
		storage.setIteratorStorage(iteratorStorage);
		
		assertSame(it, storage.getIterator(descr, t));
		
		control.verify();
	}
	
	@Test
	public void testGetTemporal_Str() throws Exception {
		expect(temporalStorage.getTemporal("bar")).andReturn(tmp);
		control.replay();
		storage.setTemporalStorage(temporalStorage);
		
		assertSame(tmp, storage.getTemporal("bar"));
		
		control.verify();
	}
	
	@Test
	public void testGetTemporal_Cur() throws Exception {
		CurrencyPair pair = new CurrencyPair("USD", "RUR");
		expect(temporalStorage.getTemporal(pair)).andReturn(tmp);
		control.replay();
		storage.setTemporalStorage(temporalStorage);
		
		assertSame(tmp, storage.getTemporal(pair));
		
		control.verify();
	}

}
