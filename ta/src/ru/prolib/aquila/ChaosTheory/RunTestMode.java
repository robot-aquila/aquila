package ru.prolib.aquila.ChaosTheory;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.*;

import ru.prolib.aquila.stat.counter.AbsoluteDrawdown;
import ru.prolib.aquila.stat.counter.Count;
import ru.prolib.aquila.stat.counter.LastTradeClosePrice;
import ru.prolib.aquila.stat.counter.LastTradeOpenPrice;
import ru.prolib.aquila.stat.counter.LastTradeType;
import ru.prolib.aquila.stat.counter.ValidatorGtZero;
import ru.prolib.aquila.stat.counter.ValidatorLtZero;
import ru.prolib.aquila.stat.counter.CounterSet;
import ru.prolib.aquila.stat.counter.CounterSetImpl;
import ru.prolib.aquila.stat.counter.CsvCounterPrinter;
import ru.prolib.aquila.stat.counter.Equity;
import ru.prolib.aquila.stat.counter.LastTradeClose;
import ru.prolib.aquila.stat.counter.LastTradeOpen;
import ru.prolib.aquila.stat.counter.LastTradeYieldPoints;
import ru.prolib.aquila.stat.counter.LastTradeYieldRatio;
import ru.prolib.aquila.stat.counter.Max;
import ru.prolib.aquila.stat.counter.Min;
import ru.prolib.aquila.stat.counter.PointsToPrice;
import ru.prolib.aquila.stat.counter.Ratio;

public class RunTestMode implements Observer {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RunTestMode.class);
	private ServiceLocator locator;
	private CounterSet trades = new CounterSetImpl();
	private CsvCounterPrinter tradesFormat = new CsvCounterPrinter();

	public void run(String[] args) throws Exception {
		if ( args.length < 1 ) {
			System.err.println("Usage: <config> [log4j-config]");
			return;
		}
		if ( args.length >= 2 ) {
			PropertyConfigurator.configure(args[1]);
		} else {
			BasicConfigurator.configure();
			org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
		}
		locator = ServiceLocatorImpl.getInstance(new File(args[0]));
		locator.getTrackingTrades().startService(locator); // force load
		
		trades.add("type", new LastTradeType());
		trades.add("open", new LastTradeOpen());
		trades.add("close", new LastTradeClose());
		trades.add("openPrice", new LastTradeOpenPrice());
		trades.add("closePrice", new LastTradeClosePrice());
		LastTradeYieldPoints yield = new LastTradeYieldPoints();
		trades.add("yield", yield);
		trades.add("yield$", new PointsToPrice(yield));
		trades.add("yield%", new LastTradeYieldRatio());
		trades.addObserver(this);
		trades.startService(locator);
		tradesFormat.addHeader("type");
		tradesFormat.addHeader("open");
		tradesFormat.addHeader("close");
		tradesFormat.addHeader("openPrice");
		tradesFormat.addHeader("closePrice");
		tradesFormat.addHeader("yield", CsvCounterPrinter.FORMAT_DECIMAL2);
		tradesFormat.addHeader("yield$", CsvCounterPrinter.FORMAT_DECIMAL2);
		tradesFormat.addHeader("yield%", CsvCounterPrinter.FORMAT_DECIMAL4);
		tradesFormat.printHeaders(System.err);
		
		CounterSet counters = new CounterSetImpl();
		Equity equity = new Equity();
		Max equityMax = new Max(equity);
		Min equityMin = new Min(equity);
		AbsoluteDrawdown absDD = new AbsoluteDrawdown(equity);
		Ratio relDD = new Ratio(equityMax, absDD);
		Max absDDMax = new Max(absDD);
		Max relDDMax = new Max(relDD);
		Count winners = new Count(yield, new ValidatorGtZero());
		Count losers = new Count(yield, new ValidatorLtZero());
		Ratio efficiency = new Ratio(losers, winners);
		
		counters.add("equity", equity);
		counters.add("equityMax", equityMax);
		counters.add("equityMin", equityMin);
		counters.add("absDD", absDD);
		counters.add("relDD", relDD);
		counters.add("absDDMax", absDDMax);
		counters.add("relDDMax", relDDMax);
		counters.add("winners", winners);
		counters.add("losers", losers);
		counters.add("losers/winners", efficiency);
		counters.startService(locator);
		CsvCounterPrinter countersFormat = new CsvCounterPrinter();
		countersFormat.addHeader("equity", CsvCounterPrinter.FORMAT_DECIMAL2);
		countersFormat.addHeader("equityMin", CsvCounterPrinter.FORMAT_DECIMAL2);
		countersFormat.addHeader("equityMax", CsvCounterPrinter.FORMAT_DECIMAL2);
		countersFormat.addHeader("absDDMax", CsvCounterPrinter.FORMAT_DECIMAL2);
		countersFormat.addHeader("relDDMax", CsvCounterPrinter.FORMAT_DECIMAL4);
		countersFormat.addHeader("winners", CsvCounterPrinter.FORMAT_DECIMAL0);
		countersFormat.addHeader("losers", CsvCounterPrinter.FORMAT_DECIMAL0);
		countersFormat.addHeader("losers/winners", CsvCounterPrinter.FORMAT_DECIMAL4);
		
		Robot robot = new RobotImpl(locator);
		RobotRunner runner = new RobotRunnerStd(locator);
		runner.run(robot);
		
		countersFormat.printHeaders(System.out);
		countersFormat.print(counters, System.out);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new RunTestMode().run(args);
	}

	@Override
	public void update(Observable o, Object arg) {
		tradesFormat.print(trades, System.err);
	}

}
