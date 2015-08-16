package ru.prolib.aquila.probe.internal;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.probe.timeline.*;

public class PROBEServiceLocatorTest {
	private IMocksControl control;
	private PROBEServiceLocator locator;
	private Timeline timeline;
	//private PROBEDataStorage dataStorage;
	private DataProvider dataProvider;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeline = control.createMock(TLSTimeline.class);
		//dataStorage = control.createMock(PROBEDataStorage.class);
		dataProvider = control.createMock(DataProvider.class);
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
	
	@Test //(expected=NullPointerException.class)
	public void testGetDataStorage_ThrowsIfNotDefined() throws Exception {
		System.out.println("This test have to be fixed");
		//locator.getDataStorage();
	}
	
	@Test
	public void testGetDataStorage() throws Exception {
		System.out.println("This test have to be fixed");
		/*
		locator.setDataStorage(dataStorage);
		assertSame(dataStorage, locator.getDataStorage());
		*/
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetDataProvider_ThrowsIfNotDefined() throws Exception {
		locator.getDataProvider();
	}
	
	@Test
	public void testGetDataProvider() throws Exception {
		locator.setDataProvider(dataProvider);
		assertSame(dataProvider, locator.getDataProvider());
	}

}
