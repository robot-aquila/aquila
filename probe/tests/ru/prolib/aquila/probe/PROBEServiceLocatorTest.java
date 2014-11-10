package ru.prolib.aquila.probe;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.timeline.*;

public class PROBEServiceLocatorTest {
	private IMocksControl control;
	private PROBEServiceLocator locator;
	private TLSTimeline timeline;
	private DataStorage dataStorage;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeline = control.createMock(TLSTimeline.class);
		dataStorage = control.createMock(DataStorage.class);
		locator = new PROBEServiceLocator();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetTimeline_ThrowsIfNotDefined() throws Exception {
		locator.getTimeline();
	}
	
	@Test
	public void testGetTimeline() throws Exception {
		locator.setTimeline(timeline);
		assertSame(timeline, locator.getTimeline());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetDataStorage_ThrowsIfNotDefined() throws Exception {
		locator.getDataStorage();
	}
	
	@Test
	public void testGetDataStorage() throws Exception {
		locator.setDataStorage(dataStorage);
		assertSame(dataStorage, locator.getDataStorage());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetDataIterator() throws Exception {
		SecurityDescriptor descr = new SecurityDescriptor("foo", "bar", "USD");
		Aqiterator<Tick> it = control.createMock(Aqiterator.class);
		DateTime time = DateTime.now();
		expect(dataStorage.getIterator(descr, time)).andReturn(it);
		control.replay();
		locator.setDataStorage(dataStorage);
		
		assertSame(it, locator.getDataIterator(descr, time));
		
		control.verify();
	}
	
	@Test
	public void testRegisterTimelineEvents() throws Exception {
		TLEventSource source = control.createMock(TLEventSource.class);
		timeline.registerSource(source);
		control.replay();
		locator.setTimeline(timeline);
		
		locator.registerTimelineEvents(source);
		
		control.verify();
	}

}
