package ru.prolib.aquila.core.data.finam.storage;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.SimpleIterator;

public class StorageScanner {
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyyMMdd");
	}
	
	public StorageScanner() {
		super();
	}
	
	/**
	 * Перечисление файлов внутридневных данных.
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
		File files[] = path.listFiles(new FileFilter() {
			@Override public boolean accept(File pathname) {
				return pathname.isFile()
						&& pathname.getName().startsWith(filePrefix);
			}
		});
		int startFrom = filePrefix.length();
		for ( File file : files ) {
			String x = file.getName();
			if ( x.endsWith(".csv") ) {
				x = x.substring(startFrom, x.length() - 4);
			} else if ( x.endsWith(".csv.gz") ) {
				x = x.substring(startFrom, x.length() - 7);
			} else {
				continue;
			}
			try {
				LocalDate date = df.parseLocalDate(x);
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
