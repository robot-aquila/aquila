package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerBuilderImpl.Label;

public class SWValueAxisRulerTest {
	private IMocksControl control;
	private SWValueAxisRulerBuilderImpl builderMock1, builderMock2;
	private ValueAxisDisplayMapper mapperMock1, mapperMock2;
	private List<Label> labels1, labels2;
	private SWValueAxisRuler ruler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		builderMock1 = control.createMock(SWValueAxisRulerBuilderImpl.class);
		builderMock2 = control.createMock(SWValueAxisRulerBuilderImpl.class);
		mapperMock1 = control.createMock(ValueAxisDisplayMapper.class);
		mapperMock2 = control.createMock(ValueAxisDisplayMapper.class);
		labels1 = new ArrayList<>();
		labels1.add(new Label(of("250.31"), "250,31", 10));
		labels1.add(new Label(of("495.29"), "495,29", 20));
		labels1.add(new Label(of("180.74"), "180,74", 30));
		labels2 = new ArrayList<>();
		labels2.add(new Label(of("164.18"), "---,-1", 15));
		labels2.add(new Label(of("112.86"), "---,-2", 30));
		ruler = new SWValueAxisRuler(builderMock1, mapperMock1, labels1);
	}
	
	@Test
	public void testDrawRuler() {
		fail();
	}
	
	@Test
	public void testDrawGrid() {
		fail();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		fail();
	}
	
	@Test
	public void testEquals() {
		fail();
	}

}
