package ru.prolib.aquila.ChaosTheory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ta.BarSequencePattern;
import ru.prolib.aquila.ta.BarSequencePatternBuilder;
import ru.prolib.aquila.ta.BarSequencePatternBuilderImpl;
import ru.prolib.aquila.ta.ds.MarketData;

public class RunFindPattern {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RunTestMode.class);
	private ServiceLocator locator;
	
	public void run(String[] args) throws Exception {
		if ( args.length < 1 ) {
			System.err.println("Usage: <config> [log4j-config]");
			return;
		}
		if ( args.length >= 2 ) {
			PropertyConfigurator.configure(args[1]);
		}
		locator = ServiceLocatorImpl.getInstance(new File(args[0]));
		
		Robot robot = new RobotStub(locator);
		RobotRunner runner = new RobotRunnerStd(locator);
		runner.run(robot);

		HashMap<BarSequencePattern, Integer> count = null;
		BarSequencePattern pattern = null;
		BarSequencePatternBuilder pb = new BarSequencePatternBuilderImpl();
		MarketData data = locator.getMarketData();
		
		count = new HashMap<BarSequencePattern, Integer>();
		
		int maxLength = 5;
		for ( int i = 0; i < data.getLength() - maxLength; i ++ ) {
			for ( int j = 2; j <= maxLength; j ++ ) {
				pattern = pb.buildBarSequencePattern(data, i, j);
				if ( count.containsKey(pattern) ) {
					count.put(pattern, count.get(pattern) + 1);
				} else {
					count.put(pattern, 1);
				}
			}
		}
		
		List<BarSequencePattern> keys = sortByValue(count);
		for ( int i = keys.size() - 1; i >= 0; i -- ) {
			pattern = keys.get(i);
			if ( count.get(pattern) > 1 ) {
				System.err.println(count.get(pattern) + " => " + pattern);
			}
	    }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new RunFindPattern().run(args);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List sortByValue(final Map m) {
        List keys = new ArrayList();
        keys.addAll(m.keySet());
        Collections.sort(keys, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = m.get(o1);
                Object v2 = m.get(o2);
                if (v1 == null) {
                    return (v2 == null) ? 0 : 1;
                }
                else if (v1 instanceof Comparable) {
                    return ((Comparable) v1).compareTo(v2);
                }
                else {
                    return 0;
                }
            }
        });
        return keys;
    }

}
