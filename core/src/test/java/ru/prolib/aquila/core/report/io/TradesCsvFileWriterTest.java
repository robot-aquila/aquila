package ru.prolib.aquila.core.report.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import com.csvreader.CsvReader;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.*;
import ru.prolib.aquila.core.report.trades.EditableTradeReport;

public class TradesCsvFileWriterTest {
	private static final String TIME = "TIME";
	private static final String SEC_CODE = "SEC_CODE";
	private static final String DIR = "DIR";
	private static final String PRICE = "PRICE";
	private static final String QTY = "QTY";
	private static final String VOL = "VOL";
	private static final String ID = "ID";
	private static final String ORD_ID = "ORD_ID";
	private static final String DIR_BUY = "B";
	private static DateTimeFormatter timeFormat;

	private EventSystem es;
	private EventQueue queue;
	private EditableTradeReport report;
	private File target;
	private TradesCsvFileWriter writer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		queue = es.getEventQueue();
		queue.start();
		report = new ReportBuilder().createReport(es);
		report.start();
		target = File.createTempFile("csv-writer-test", ".csv");
		target.deleteOnExit();
		writer = new TradesCsvFileWriter(report, target);
	}
	
	@After
	public void tearDown() throws Exception {
		if ( writer != null ) {
			writer.stop();
		}
		if ( queue.started() ) {
			queue.stop();
			queue.join(1000L);
		}
	}
	
	/**
	 * Сформировать дескриптор инструмента.
	 * <p>
	 * @param reader источник данных
	 * @return дескриптор инструмента
	 */
	private Symbol getSymbol(CsvReader reader) throws Exception {
		return new Symbol(reader.get(SEC_CODE), "TEST", "USD", SymbolType.FUTURE);
	}
	
	/**
	 * Получить полный путь к файлу фикстуры.
	 * <p>
	 * @param filename имя файла
	 * @return полный путь к файлу
	 */
	private String getFullPath(String filename) {
		String s = System.getProperty("file.separator");
		return System.getProperty("user.dir") + s + "fixture" + s + filename;
	}
	
	/**
	 * Загрузить сделки из файла.
	 * <p>
	 * @param filename имя файла с описанием сделок
	 * @return список загруженных сделок
	 */
	private List<Trade> loadTrades(String filename) throws Exception {
		CsvReader reader = new CsvReader(getFullPath(filename));
		List<Trade> list = new Vector<Trade>();
		reader.readHeaders();
		while ( reader.readRecord() ) {
			Trade trade = new Trade(null);
			trade.setDirection(DIR_BUY.equals(reader.get(DIR))
					? Direction.BUY : Direction.SELL);
			trade.setId(Long.parseLong(reader.get(ID)));
			trade.setOrderId(Long.parseLong(reader.get(ORD_ID)));
			trade.setPrice(Double.parseDouble(reader.get(PRICE)));
			trade.setQty(Long.parseLong(reader.get(QTY)));
			trade.setSymbol(getSymbol(reader));
			trade.setTime(LocalDateTime.parse(reader.get(TIME), timeFormat).toInstant(ZoneOffset.UTC));
			trade.setVolume(Double.parseDouble(reader.get(VOL)));
			list.add(trade);
		}
		reader.close();
		return list;
	}
	
	/**
	 * Загрузить сделки из файла.
	 * <p>
	 * @param dest целевой экземпляр набора трейдов
	 * @param filename имя файла с описанием сделок
	 */
	private void loadTrades(EditableTradeReport dest, String filename)
		throws Exception
	{
		for ( Trade trade : loadTrades(filename) ) {
			dest.addTrade(trade);
		}
	}
	
	private String readFile(String filename) throws Exception {
		return readFile(new File(getFullPath(filename)));
	}
	
	private String readFile(File file) throws Exception {
		FileInputStream fs = new FileInputStream(file);
		byte b[] = new byte[(int) file.length()];
		fs.read(b);
		fs.close();
		return new String(b);
	}
	
	@Test
	public void testMain() throws Exception {
		writer.start();
		loadTrades(report, "csv-writer-trades.csv");
		queue.stop();
		assertTrue(queue.join(2000L));

		// to be done under linux and windows
		String expected = readFile("csv-writer-report.csv").replaceAll("\r", "");
		String actual = readFile(target).replaceAll("\r", "");
		
		assertEquals(expected, actual);
	}

}
