package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class CommonTickHandlerTest {
	private IMocksControl control;
	private EditableSecurity security;
	private CommonTickHandler handler;
	

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		handler = new CommonTickHandler(security);
	}
	
	@Test
	public void testEquals() throws Exception {
		CommonTickHandler h2 =
			new CommonTickHandler(control.createMock(EditableSecurity.class));
		assertTrue(handler.equals(handler));
		assertTrue(handler.equals(new CommonTickHandler(security)));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(h2));
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
