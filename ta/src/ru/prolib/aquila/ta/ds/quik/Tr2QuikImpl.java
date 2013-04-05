package ru.prolib.aquila.ta.ds.quik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Драйвер подачи торговых поручений в терминал quik.
 * В целях снижения количества возможных вариантов, некондиция в файле
 * ответов игнорируется. В таких случаях драйвер будет продолжать ожидание
 * валидных данных. В конечном итоге, любые ошибки формата файла ответов
 * приведут к ситуации таймаута. Однако в отладочных целях информация
 * о возникновении подобных ситуаций регистрируется в журнале с пометкой error.
 *   
 * ВАЖНО! При Tr2QuikTimeoutException может возникнуть ситуация, когда в
 * будущем QUIK примет и обработает транзакцию, которая была снята с контроля
 * Tr2Quik.
 * 1. Надо проверить, как будет действовать QUIK, при запуске импорта в случае
 * наличия в tri-файле строк, которым нет соответствующих строк в tro-файле.
 * 2. Алгоритм торговли должен периодически (в подходящий для него момент)
 * отправлять транзакцию на снятие всех заявок.  
 */
public class Tr2QuikImpl implements Tr2Quik {
	static final Logger logger = LoggerFactory.getLogger(Tr2QuikImpl.class);
	private final int pause = 100; // пауза между чтением в ms
	private final int retries = 15000 / pause; // количество попыток чтения
 
	private final Writer target;
	private final BufferedReader report;
	private long lastId = 0;
	
	/**
	 * Конструктор.
	 * 
	 * Файлы tri и tro могут уже содержать строки от предыдущей работы. После
	 * открытия, tro-файл читается на предмет определения последнего TRANS_ID.
	 * Если какой то из указанных файлов не существует, будет выполнена попытка
	 * создания файла. Для обоих файлов указатель выставляется на конец файла.
	 * 
	 * @param target tri-файл с параметрами транзакций.
	 * @param report tro-файл ответов
	 * @throws Tr2QuikException
	 */
	public Tr2QuikImpl(File target, File report) throws Tr2QuikException {
		super();
		checkCreate(report);
		checkCreate(target);
		try {
			OutputStream os = new FileOutputStream(target, true);
			InputStream is = new FileInputStream(report);
			Reader streamReader = new InputStreamReader(is, "cp1251");
			this.target = new OutputStreamWriter(os, "cp1251");
			this.report = new BufferedReader(streamReader);
		} catch ( UnsupportedEncodingException e ) {
			throw new Tr2QuikException("cp1251 encoding not found???", e);
		} catch ( FileNotFoundException e ) {
			throw new Tr2QuikIoException("Impossible, but file not exists", e);
		}
		readFirst();
	}
	
	/**
	 * Проверить существование файла и попытаться создать, в случае отсутствия. 
	 * @param file
	 * @throws Tr2QuikException
	 */
	private void checkCreate(File file) throws Tr2QuikException {
		try {
			if ( ! file.exists() ) {
				file.createNewFile();
			}
		} catch ( IOException e ) {
			throw new Tr2QuikException("Error create file: " + file, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.Tr2Quik#getLastTransactionId()
	 */
	public long getLastTransactionId() {
		return lastId;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ta.ds.quik.Tr2Quik#close()
	 */
	public void close() {
		try {
			report.close();
		} catch ( IOException e ) {
			logger.warn("Close error", e);
		}
		try {
			target.close();
		} catch ( IOException e ) {
			logger.warn("Close error", e);
		}
	}
	
	/**
	 * Читает все строки файла отчета что бы сместиться в конец файла и
	 * определить наибольший идентификатор транзакции.
	 * @throws Tr2QuikException
	 */
	private void readFirst() throws Tr2QuikException {
		Tr2QuikResult result = null;
		while ( (result = readResult()) != null ) {
			if ( result.transId > lastId ) {
				lastId = result.transId;
			}
		}
	}
	
	@Override
	public Tr2QuikResult transaction(String command, int expectedStatus)
		throws Tr2QuikIoException, Tr2QuikTimeoutException,
			Tr2QuikRejectedException, InterruptedException
	{
		// replace or add TRANS_ID
		long transId = ++lastId;
		Map<String, String> map = parseLine(command);
		map.put("TRANS_ID", Long.toString(transId));
		command = formatLine(map);
		
		Tr2QuikResult result = null;
		try {
			target.write(command + "\n");
			target.flush();
			for ( int i = 0; i < retries; i ++ ) {
				Thread.sleep(pause);
				while ( (result = readResult()) != null ) {
					if ( result.transId == transId
					  && result.status == expectedStatus )
					{
						return result;
					} else {
						logger.debug("Expected transId={} status={}, actual {}",
							new Object[]{ transId, expectedStatus, result });
					}
				}
			}
		} catch ( IOException e ) {
			throw new Tr2QuikIoException(e.getMessage(), e);
		}
		throw new Tr2QuikTimeoutException();
	}
	
	/**
	 * Прочитать результат из файла отчета.
	 * @return результат или null, если нет данных или возникла ошибка формата
	 * @throws Tr2QuikIoException
	 */
	private Tr2QuikResult readResult() throws Tr2QuikIoException {
		try {
			String line = report.readLine();
			if ( line == null ) {
				return null;
			}
			Map<String, String> map = parseLine(line);
			if ( map == null ) {
				return null;
			}
			if ( ! map.containsKey("TRANS_ID") ) {
				logger.error("No TRANS_ID in line: {}", line);
				return null;
			}
			if ( ! map.containsKey("STATUS") ) {
				logger.error("No STATUS in line: {}", line);
				return null;
			}
			
			int status = -1;
			long transId = 0,orderNumber = 0;
			String descr = null;
			try {
				status = Integer.parseInt(map.get("STATUS"));
			} catch ( Exception e ) {
				logger.error("Bad STATUS in line: {}", line);
				return null;
			}
			try {
				transId = Long.parseLong(map.get("TRANS_ID"));
			} catch ( Exception e ) {
				logger.error("Bad TRANS_ID in line: {}", line);
				return null;
			}
			if ( map.containsKey("ORDER_NUMBER") ) {
				try {
					orderNumber = Long.parseLong(map.get("ORDER_NUMBER"));
				} catch ( Exception e ) {
					logger.error("Bad ORDER_NUMBER in line: {}", line);
					return null;
				}
			}
			if ( map.containsKey("DESCRIPTION") ) {
				descr = map.get("DESCRIPTION");
			}
			return new Tr2QuikResult(transId, status, descr, orderNumber);
		} catch ( IOException e ) {
			throw new Tr2QuikIoException(e);
		}
	}

	/**
	 * Конвертировать строку в набор ключ-значение.
	 * 
	 * @param line строка в формате предусмотренном quik для обмена
	 * информацией о транзакциях (см. Импорт транзакций, справка QUIK). 
	 * @return пары ключ-значения или null, если разобрать строку не удалось
	 */
	public static Map<String, String> parseLine(String line) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] pairs = line.split(";");
		for ( int i = 0; i < pairs.length; i ++ ) {
			String pair = pairs[i].trim();
			if ( pair.length() > 0 ) {
				String[] parts = pair.split("=");
				if ( parts.length != 2 ) {
					logger.error("Bad pair [{}] in line: {}", pair, line);
					return null;
				}
				map.put(parts[0].trim(), parts[1].trim());
			}
		}
		return map;
	}
	
	/**
	 * Конвертировать набор пар ключ-значение в строку.
	 * 
	 * @param map набор пар ключ-значение
	 * @return строку в формате, предусмотренном quik для обмена
	 * информацией о транзакциях (см. Импорт транзакций, справка QUIK). 
	 */
	public static String formatLine(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> i;
		String result = "";
		for ( i = map.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, String> e = i.next();
			result += e.getKey() + "=" + e.getValue() + "; ";
		}
		return result;
	}

}
