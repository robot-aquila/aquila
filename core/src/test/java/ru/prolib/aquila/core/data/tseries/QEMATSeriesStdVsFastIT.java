package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class QEMATSeriesStdVsFastIT {
	private static EditableTSeries<CDecimal> source;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		source = new TSeriesImpl<>("foo", ZTFrame.M5);
		Instant time = Instant.parse("2018-04-06T00:00:00Z");
		for ( int i = 0; i < 10000; i ++ ) {
			source.set(time, CDecimalBD.of((long)(Math.random() * 100000)).divideExact(100L, 2));
			time = time.plusSeconds(300); // +5 minutes
		}
	}
	
	private QEMATSeries qema;
	private QEMATSeriesFast qemaFast;

	@Before
	public void setUp() throws Exception {
		qema = new QEMATSeries("QEMA", source, 14);
		qemaFast = new QEMATSeriesFast("QEMA-F", source, 14, 9);
	}
	
	private long testPass(TSeries<CDecimal> series, boolean printUsedTime) throws Exception {
		long started = System.currentTimeMillis();
		int length = series.getLength();
		for ( int i = 0; i < length; i ++ ) {
			@SuppressWarnings("unused")
			CDecimal x = series.get(i);
		}
		long used =  System.currentTimeMillis() - started;
		if ( printUsedTime ) {
			System.out.println("One pass time used: " + used + "ms for " + length + " elements in " + series.getId() + " series");
		}
		return used;
	}
	
	private void onePassTestRun() throws Exception {
		qemaFast.invalidate(0);
		List<TSeries<CDecimal>> list = new ArrayList<>();
		list.add(qema);
		list.add(qemaFast);
		Collections.shuffle(list);
		for ( TSeries<CDecimal> x : list ) {
			testPass(x, true);
		}
	}
	
	@Test
	public void testOnePass() throws Exception {
		onePassTestRun();
		onePassTestRun();
		onePassTestRun();
	}
	
	private void consecAccessTestRun() throws Exception {
		List<TSeries<CDecimal>> list = new ArrayList<>();
		list.add(qema);
		list.add(qemaFast);
		Collections.shuffle(list);
		for ( TSeries<CDecimal> x : list ) {
			long pass1 = testPass(x, false);
			long pass2 = testPass(x, false);
			long pass3 = testPass(x, false);
			long totalTime = pass1 + pass2 + pass3;
			System.out.println("Consecutive test total time: " + totalTime + "ms ("
					+ "p1=" + pass1 + "ms, "
					+ "p2=" + pass2 + "ms, "
					+ "p3=" + pass3 + "ms)"
					+ " for " + x.getLength() + " elements in " + x.getId() + " series");
		}
	}

	@Test
	public void testConsecutiveAccess() throws Exception {
		consecAccessTestRun();
		consecAccessTestRun();
		consecAccessTestRun();
	}
	
	private void randomAccess(List<Integer> indices, TSeries<CDecimal> series) throws Exception {
		long started = System.currentTimeMillis();
		for ( int i : indices ) {
			@SuppressWarnings("unused")
			CDecimal x = series.get(i);
		}
		long used = System.currentTimeMillis() - started;
		System.out.println("Random access time: " + used + " for " + series.getLength() + " elements in " + series.getId() + " series");
	}
	
	@Test
	public void testRandomAccess() throws Exception {
		List<Integer> indices = new ArrayList<>();
		int length = source.getLength();
		for ( int i = 0; i < 5000; i ++ ) {
			indices.add((int)(Math.random() * length));
		}
		List<TSeries<CDecimal>> list = new ArrayList<>();
		list.add(qema);
		list.add(qemaFast);
		Collections.shuffle(list);
		for ( TSeries<CDecimal> x : list ) {
			randomAccess(indices, x);
		}
	}

}
