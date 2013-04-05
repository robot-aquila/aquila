package ru.prolib.aquila.ta.SignalSource;

import org.junit.*;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import ru.prolib.aquila.ta.*;

public class CompositeSignalSourceTest {
	private IMocksControl control;
	private ISignalSource ss1,ss2,ss3;
	private CompositeSignalSource css;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		ss1 = control.createMock(ISignalSource.class);
		ss2 = control.createMock(ISignalSource.class);
		ss3 = control.createMock(ISignalSource.class);
		css = new CompositeSignalSource(new ISignalSource[]{ss1,ss2,ss3});
	}
	
	@Test
	public void testAccessors() {
		assertArrayEquals(new ISignalSource[]{ss1,ss2,ss3}, css.getSignalSources());
	}
	
	@Test
	public void testAnalyze() throws Exception {
		ISignalTranslator t = control.createMock(ISignalTranslator.class);
		ss1.analyze(same(t));
		ss2.analyze(same(t));
		ss3.analyze(same(t));
		control.replay();
		
		css.analyze(t);
		
		control.verify();
	}

}
