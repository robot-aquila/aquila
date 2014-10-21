package ru.prolib.aquila.core.data.finam.storage;

import java.io.*;
import java.util.*;
import org.joda.time.LocalDate;
import org.joda.time.format.*;

import ru.prolib.aquila.core.data.*;

public class StorageScanner {
	private static final DateTimeFormatter dateFormat, monthFormat, yearFormat;
	
	static {
		dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
		monthFormat = DateTimeFormat.forPattern("MM");
		yearFormat = DateTimeFormat.forPattern("yyyy");
	}
	
	public StorageScanner() {
		super();
	}
	
	/**
	 * Перечислить файлы внутридневных данных.
	 * <p>
	 * Внутридневные тиковые данные хранятся в файлах с именами PFXyyyyMMdd
	 * и расширением csv или csv.gz, где PFX - произвольный префикс (обычно
	 * идентификатор инструмента). Данный метод сканирует указанный каталог
	 * на предмет наличия таких файлов. Имена файлов преобразуются в даты,
	 * которые затем проверяются на вхождение в соответствие с условием по
	 * начальной дате. Если данные подходят, то формируется дескриптор файла. 
	 * <p>
	 * @param path путь к каталогу с файлами
	 * @param filePrefix префикс имени файла
	 * @param start дата, включительно с которой интересует наличие данных  
	 * @return список подходящих дескрипторов, отсортированный в порядке
	 * возрастания даты  
	 */
	public Aqiterator<FileEntry>
		findIntradayFiles(File path, final String filePrefix, LocalDate start)
	{
		List<FileEntry> result = new Vector<FileEntry>();
		int startFrom = filePrefix.length();
		for ( File file : path.listFiles() ) {
			if ( ! file.isFile() ) {
				continue;
			}
			String x = file.getName();
			if ( ! x.startsWith(filePrefix) ) {
				continue;
			}
			if ( x.endsWith(".csv") ) {
				x = x.substring(startFrom, x.length() - 4);
			} else if ( x.endsWith(".csv.gz") ) {
				x = x.substring(startFrom, x.length() - 7);
			} else {
				continue;
			}
			try {
				LocalDate date = dateFormat.parseLocalDate(x);
				if ( ! date.isBefore(start) ) {
					result.add(new FileEntry(file, date));
				}
			} catch ( IllegalArgumentException e ) {
				
			}
		}
		Collections.sort(result);
		return new SimpleIterator<FileEntry>(result);
	}
	
	/**
	 * Перечислить каталоги-группировки по месяцам.
	 * <p>
	 * Имена каталогов группировки по месяцам соответствуют номеру месяца в
	 * году, приведенному к строке в соответствии с шаблоном MM. Данный метод
	 * позволяет отобрать подходящие каталоги для последующего сканирования.
	 * <p>
	 * @param path путь к каталогу с файлами
	 * @param start номер месяца используется для фильтрации - каталоги, имена
	 * которых соответствуют более ранним месяцам отбрасываются. Номер дня
	 * месяца игнорируется.
	 * @return список подходящих дескрипторов, отсортированный в порядке
	 * возрастания даты. В качестве года устанавливается год даты-аргумента.
	 * В качестве дня месяца всегда устанавливаентся единица.
	 */
	public Aqiterator<FileEntry> findMonthlyDirs(File path, LocalDate start) {
		start = start.withDayOfMonth(1);
		List<FileEntry> result = new Vector<FileEntry>();
		for ( File file : path.listFiles() ) {
			if ( ! file.isDirectory() ) {
				continue;
			}
			try {
				LocalDate date = monthFormat.parseLocalDate(file.getName())
						.withYear(start.getYear())
						.withDayOfMonth(1);
				if ( ! date.isBefore(start) ) {
					result.add(new FileEntry(file, date));
				}
			} catch ( IllegalArgumentException e ) {
				
			}
		}
		Collections.sort(result);
		return new SimpleIterator<FileEntry>(result);
	}
	
	/**
	 * Перечислить каталоги-группировки по годам.
	 * <p>
	 * Данный метод позволяет отбирать подходящие каталоги для последующего
	 * сканирования.
	 * <p>
	 * @param path путь к каталогу с файлами
	 * @param start дата, год которой используется для фильтрации - каталоги,
	 * соответствующие более ранним месяцам, отбрасываются. Номера месяца и
	 * дня месяца игнорируются. 
	 * @return список подходящих дескрипторов, отсортированный в порядке
	 * возрастания даты. В качестве номера месяца и дня месяца всегда
	 * устанавливается единица.
	 */
	public Aqiterator<FileEntry> findYearlyDirs(File path, LocalDate start) {
		start = new LocalDate(start.getYear(), 1, 1);
		List<FileEntry> result = new Vector<FileEntry>();
		for ( File file : path.listFiles() ) {
			if ( ! file.isDirectory() ) {
				continue;
			}
			try {
				LocalDate date = yearFormat.parseLocalDate(file.getName())
						.withMonthOfYear(1)
						.withDayOfMonth(1);
				if ( ! date.isBefore(start) ) {
					result.add(new FileEntry(file, date));
				}
			} catch ( IllegalArgumentException e ) {
				
			}
		}
		Collections.sort(result);
		return new SimpleIterator<FileEntry>(result);
	}

}
