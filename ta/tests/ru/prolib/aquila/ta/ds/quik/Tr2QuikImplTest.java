package ru.prolib.aquila.ta.ds.quik;


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Map;

import org.junit.*;


public class Tr2QuikImplTest {
	String[] tri = {
		"ACCOUNT=SPBFUTZZZZZ; CLIENT_CODE=SPBFUTJJJJJ; TYPE=L; TRANS_ID=264; CLASSCODE=SPBFUT; SECCODE=RIM1; ACTION=NEW_ORDER; OPERATION=S; PRICE=198145; QUANTITY=1;",
		"ACCOUNT=SPBFUTZZZZZ; CLIENT_CODE=SPBFUTJJJJJ; TYPE=L; TRANS_ID=265; CLASSCODE=SPBFUT; SECCODE=RIM1; ACTION=NEW_ORDER; OPERATION=B; PRICE=198225; QUANTITY=1;",
		"ACCOUNT=SPBFUTZZZZZ; CLIENT_CODE=SPBFUTJJJJJ; TYPE=L; TRANS_ID=266; CLASSCODE=SPBFUT; SECCODE=RIM1; ACTION=NEW_ORDER; OPERATION=B; PRICE=198225; QUANTITY=1;",
		"ACCOUNT=SPBFUTZZZZZ; CLIENT_CODE=SPBFUTJJJJJ; TYPE=L; TRANS_ID=267; CLASSCODE=SPBFUT; SECCODE=RIM1; ACTION=NEW_ORDER; OPERATION=S; PRICE=198170; QUANTITY=1;",
	};
	String tro[] = {
		"TRANS_ID=264;STATUS=0;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:40:59: Отправлена транзакция\";",
		"TRANS_ID=264;STATUS=3;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:00: Заявка, с биржевым номером 3468101718, успешно зарегистрирована.\"; ORDER_NUMBER=3468101718;",
		"TRANS_ID=265;STATUS=0;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:13: Отправлена транзакция\";",
		"TRANS_ID=265;STATUS=3;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:13: Заявка, с биржевым номером 3468103910, успешно зарегистрирована.\"; ORDER_NUMBER=3468103910;",
		"TRANS_ID=266;STATUS=0;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:16: Отправлена транзакция\";",
		"",
		"InvalidEntry",
		"TRANS_ID=267;STATUS=0;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:23: Отправлена транзакция\";",
		"TRANS_ID=267;STATUS=1;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:23: Транзакция принята сервером\";",
		// эта запись позволит определить, что номер транзы учитывается
		"TRANS_ID=266;STATUS=3;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:16: Заявка, с биржевым номером 3468104335, успешно зарегистрирована.\"; ORDER_NUMBER=3468104335;",
		"TRANS_ID=267;STATUS=3;TRANS_NAME=\"Ввод заявки\"; DESCRIPTION=\"25.03.2011 14:41:23: Заявка, с биржевым номером 3468106676, успешно зарегистрирована.\"; ORDER_NUMBER=3468106676;",
	};
	
	File target = new File(System.getProperty("java.io.tmpdir"), "test.tri");
	File report = new File(System.getProperty("java.io.tmpdir"), "test.tro");
	Tr2Quik tr;
	
	@After
	public void tearDown() throws Exception {
		if ( target.exists() ) {
			target.delete();
		}
		if ( report.exists() ) {
			report.delete();
		}
	}
	
	/**
	 * Сохраняет указанное количество строк массива в файл.
	 * @param lines
	 * @param filename
	 * @param count
	 * @throws Exception
	 */
	private void saveLines(String[] lines, File filename, int count)
		throws Exception
	{
		OutputStream stream = new FileOutputStream(filename, true);
		Writer writer = new OutputStreamWriter(stream, "cp1251");
		for ( int i = 0; i < count; i ++ ) {
			writer.write(lines[i] + "\n");
		}
		writer.close();
	}
	
	/**
	 * Сохраняет все строки массива файл.
	 * @param lines
	 * @param filename
	 * @throws Exception
	 */
	private void saveLines(String[] lines, File filename) throws Exception {
		saveLines(lines, filename, lines.length);
	}
	
	/**
	 * Прочитать все строки файла в массив.
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	private String[] readLines(File filename) throws Exception {
		InputStream stream = new FileInputStream(filename);
		Reader streamReader = new InputStreamReader(stream, "cp1251");
		BufferedReader reader = new BufferedReader(streamReader);
		LinkedList<String> lines = new LinkedList<String>();
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			lines.add(line);
		}
		reader.close();
		return (String[]) lines.toArray(new String[lines.size()]);
	}
	
	/**
	 * Запустить позднюю запись в файл результатов.
	 * @param pause
	 * @param str
	 * @throws Exception
	 */
	private void lateWrite(final long pause, final String str)
		throws Exception
	{
		final String[] lines = {str};
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(pause);
					saveLines(lines, report);
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					return;
				} catch ( Exception e ) {
					System.err.println("Exception in lateWrite");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Сравнить строки в формате файлов обмена данными о транзакциях.
	 * @param expected
	 * @param actual
	 */
	private void assertTransLine(String expected, String actual) {
		Map<String, String> expectedMap = Tr2QuikImpl.parseLine(expected);
		Map<String, String> actualMap = Tr2QuikImpl.parseLine(actual);
		assertEquals(expectedMap, actualMap);
	}
	
	/**
	 * Сравнить массивы строк формата файлов обмена данными о транзакциях.
	 * @param expected
	 * @param actual
	 */
	private void assertTransLines(String[] expected, String[] actual) {
		if ( expected.length != actual.length ) {
			for ( int i = 0;
				i < (expected.length < actual.length
						? expected.length : actual.length);
				i ++ )
			{
				if ( i < expected.length && i < actual.length
					&& expected[i].equals(actual[i]) )
				{
					System.out.println("Lines " + i + " equals");
				} else {
					System.out.println("Line " + i + " mismatch: " + expected[i]
					    + " != " + actual[i]);
				}
			}
			assertEquals("Array size mismatch", expected.length, actual.length);
		}
		for ( int i = 0; i < expected.length; i ++ ) {
			assertTransLine(expected[i], actual[i]);
		}
	}

	/**
	 * Тестируются ситуации:
	 * - отправка транзакции и чтение валидного результата
	 * - чтение начала файла результатов
	 * - запись выполняется в конец целевого файла
	 * - в ответе рассматриваются только записи с соответствующим TRANS_ID
	 * - невалидные строки игнорируются
	 * - пустые строки игнорируются
	 * - подставляется корректный TRANS_ID
	 * - ответы со неожидаемыми статусами игнорируются 
	 * @throws Exception
	 */
	@Test
	public void testTransaction_Ok() throws Exception {
		saveLines(tri, target, 3);	// Записали в исходный файл левые транзы,
									// следующая будет наша.
		saveLines(tro, report, 5);	// Записали в файл результата первые 5
									// которые нас не интересуют

		tr = new Tr2QuikImpl(target, report);
		assertEquals(266, tr.getLastTransactionId());
		lateWrite(200, tro[ 5]); // пустая строка
		lateWrite(300, tro[ 6]); // невалидная строка
		lateWrite(400, tro[ 7]); // строка искомой транзы но с другим статусом
		lateWrite(500, tro[ 8]); // строка искомой транзы но с другим статусом
		lateWrite(600, tro[ 9]);
		lateWrite(700, tro[10]);
		long start = System.currentTimeMillis();

		Tr2QuikResult result = tr.transaction("ACCOUNT=SPBFUTZZZZZ; " +
				"CLIENT_CODE=SPBFUTJJJJJ; TYPE=L; TRANS_ID=1234567; " +
			    "CLASSCODE=SPBFUT; SECCODE=RIM1; ACTION=NEW_ORDER; " +
			    "OPERATION=S; PRICE=198170; QUANTITY=1", 3);
		tr.close();
		long time = System.currentTimeMillis() - start;
		assertTrue(time > 150 && time < 750);
		assertEquals(267L, result.transId);
		assertEquals(3, result.status);
		assertEquals("\"25.03.2011 14:41:23: Заявка, с биржевым номером " +
				"3468106676, успешно зарегистрирована.\"", result.description);
		assertEquals(3468106676L, result.orderNumber);
		assertEquals(267, tr.getLastTransactionId());
		assertTransLines(tri, readLines(target));
		assertTransLines(tro, readLines(report));
	}
	
}
