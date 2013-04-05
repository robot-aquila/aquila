package test;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.quik.*;

/**
 * 2012-09-21<br>
 * $Id: Main.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class Main implements EventListener {
	private static final Logger logger;
	public static final String NULL = "<NULL>";
	private Terminal terminal;
	
	static {
		logger = LoggerFactory.getLogger(Main.class);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new Main().run();
	}
	
	private void run() throws Exception {
		BasicConfigurator.configure();
		
		QUIKConfigImpl config = new QUIKConfigImpl();
		config.allDeals = "deals";
		config.orders = "orders";
		config.portfoliosFUT = "portfolio.fut";
		config.portfoliosSTK = "portfolio";
		config.positionsFUT = "position.fut";
		config.positionsSTK = "position";
		config.securities = "securities";
		config.stopOrders = "stop-orders";
		config.serviceName = "AQUILA";
		config.quikPath = "C:\\quik\\finam\\Quik5";
		terminal = new QUIKFactory().createTerminal(config);
		terminal.OnSecurityAvailable().addListener(this);
		terminal.OnSecurityChanged().addListener(this);
		terminal.OnSecurityTrade().addListener(this);
		terminal.OnPortfolioAvailable().addListener(this);
		terminal.OnPortfolioChanged().addListener(this);
		terminal.OnPositionAvailable().addListener(this);
		terminal.OnPositionChanged().addListener(this);
		terminal.OnOrderAvailable().addListener(this);
		terminal.OnOrderChanged().addListener(this);
		terminal.OnStopOrderAvailable().addListener(this);
		terminal.OnStopOrderChanged().addListener(this);
		terminal.start();
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.OnSecurityTrade()) ) {
			//SecurityTradeEvent e = (SecurityTradeEvent) event;
			//print(e.getTrade() + "\n");
		} else if ( event.isType(terminal.OnSecurityAvailable()) ) {
			printSecurity(((SecurityEvent) event).getSecurity(), false);
		} else if ( event.isType(terminal.OnSecurityChanged()) ) {
			printSecurity(((SecurityEvent) event).getSecurity(), true);
		} else if ( event.isType(terminal.OnPortfolioAvailable()) ) {
			printPortfolio(((PortfolioEvent) event).getPortfolio(), false);
		} else if ( event.isType(terminal.OnPortfolioChanged()) ) {
			printPortfolio(((PortfolioEvent) event).getPortfolio(), true);
		} else if ( event.isType(terminal.OnPositionAvailable()) ) {
			printPosition(((PositionEvent) event).getPosition(), false);
		} else if ( event.isType(terminal.OnPositionChanged()) ) {
			printPosition(((PositionEvent) event).getPosition(), true);
		} else if ( event.isType(terminal.OnOrderAvailable()) ) {
			printOrder(((OrderEvent) event).getOrder(), false);
		} else if ( event.isType(terminal.OnOrderChanged()) ) {
			printOrder(((OrderEvent) event).getOrder(), true);
		} else if ( event.isType(terminal.OnStopOrderAvailable()) ) {
			printOrder(((OrderEvent) event).getOrder(), false);
		} else if ( event.isType(terminal.OnStopOrderChanged()) ) {
			printOrder(((OrderEvent) event).getOrder(), true);
		} else {
			print("Unknown event type: " + event + "\n");
		}
		//print("Num orders=" + terminal.getOrders().size() + "\n");
		//print("Num stop-orders=" + terminal.getStopOrders().size() + "\n");
	}
	
	private void printOrder(Order o, boolean isChanged) {
		print((isChanged ? "*" : "+"));
		print(" ");
		print(o);
		commitPrint();
	}
	
	private void printPosition(Position p, boolean isChanged) {
		print((isChanged ? "*" : "+"));
		print(" POS: acc=" + p.getAccount() + " "
				+ " sec=" + p.getSecurityDescriptor()
				+ " pos=" + p.getCurrQty()
				+ " VM=" + p.getVarMargin());
		commitPrint();
	}
	
	private void printPortfolio(Portfolio p, boolean isChanged) {
		print((isChanged ? "*" : "+"));
		print(" PRT: " + p.getAccount() + " cash: " + p.getCash()
				+ " balance: " + p.getBalance() 
				+ " var.margin: " + p.getVariationMargin());
		commitPrint();
	}
	
	private void printSecurity(Security s, boolean isChanged) {
		print((isChanged ? "*" : "+"));
		print(" SEC: " + s.getDescriptor());
		print(" Lot:" + s.getLotSize());
		print(" Min step:" + s.getMinStepSize());
		print(" Scale:" + s.getPrecision());
		print(" Step price:" + s.getMinStepPrice());
		print(" Max price:" + s.getMaxPrice());
		print(" Min price:" + s.getMinPrice());
		print(" Last price:" + s.getLastPrice());
		commitPrint();
	}

	private String printString = "";
	
	private void print(Object obj) {
		printString += obj;
	}
	
	private void commitPrint() {
		logger.debug(printString);
		printString = "";
	}
	
	/**
	 * Напечатать значение ячейки с учетом максимальной ширины.
	 * <p>
	 * @param cell значение ячейки
	 * @param colWidth ширина колонки
	 */
	private void printCell(Object cell, int colWidth) {
		if ( cell == null ) {
			cell = NULL;
		}
		int rem = colWidth - cell.toString().length();
		print(cell);
		for ( int i = 0; i < rem; i ++ ) {
			print(" ");
		}
		print(" ");
	}
	
	/**
	 * Расчитать ширину каждой колонки таблицы.
	 * <p>
	 * @param t таблица
	 * @return массив с максимальной шириной значения колонки
	 */
	private int[] getColumnsWidth(DDETable t) {
		int[] width = new int[t.getCols()];
		int len = 0;
		for ( int col = 0; col < width.length; col ++ ) {
			int max = 0;
			for ( int row = 0; row < t.getRows(); row ++ ) {
				Object cell = t.getCell(row, col);
				len = (cell == null ? NULL.length() : cell.toString().length());
				max = (len > max ? len : max);
			}
			width[col] = max;
		}
		return width;
	}

}
