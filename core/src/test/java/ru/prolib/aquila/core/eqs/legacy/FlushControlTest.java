package ru.prolib.aquila.core.eqs.legacy;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.eqs.legacy.FlushControl;
import ru.prolib.aquila.core.eqs.legacy.FlushController;

public class FlushControlTest {
	private IMocksControl control;
	private FlushController ctrlMock1, ctrlMock2, ctrlMock3;
	private List<FlushController> controllers;
	private FlushControl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		ctrlMock1 = control.createMock(FlushController.class);
		ctrlMock2 = control.createMock(FlushController.class);
		ctrlMock3 = control.createMock(FlushController.class);
		controllers = new ArrayList<>();
		service = new FlushControl(controllers);
	}

	@Test
	public void testCreateIndicator() {
		FlushIndicator actual = service.createIndicator();
		
		assertNotNull(actual);
		assertSame(actual, controllers.get(0));
		assertEquals(FlushController.class, actual.getClass());
	}
	
	@Test
	public void testCountUp() throws Exception {
		controllers.add(ctrlMock1);
		controllers.add(ctrlMock2);
		controllers.add(ctrlMock3);
		expect(ctrlMock1.countUp()).andReturn(100L);
		expect(ctrlMock2.countUp()).andReturn(100L);
		expect(ctrlMock3.countUp()).andReturn(100L);
		control.replay();
		
		service.countUp();
		
		control.verify();
	}
	
	@Test
	public void testCountDown() throws Exception {
		controllers.add(ctrlMock1);
		controllers.add(ctrlMock2);
		controllers.add(ctrlMock3);
		expect(ctrlMock1.countDown()).andReturn(false);
		expect(ctrlMock2.countDown()).andReturn(true);
		expect(ctrlMock3.countDown()).andReturn(false);
		control.replay();
		
		service.countDown();
		
		control.verify();
		List<FlushController> expected = new ArrayList<>();
		expected.add(ctrlMock1);
		expected.add(ctrlMock3);
		assertEquals(expected, controllers);
	}

}
