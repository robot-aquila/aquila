package ru.prolib.aquila.core.data.finam.storage;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.SimpleIterator;

/**
 * Сканер уровня суточных данных.
 * <p>
 * Внутридневные тиковые данные хранятся в файлах с именами PFXyyyyMMdd
 * и расширением csv или csv.gz, где PFX - произвольный префикс (обычно
 * идентификатор инструмента). Данный класс сканирует указанный каталог
 * на предмет наличия таких файлов. Имена файлов преобразуются в даты,
 * которые затем проверяются на вхождение в соответствие с условием по
 * начальной дате. Если данные подходят, то формируется дескриптор файла. 
 */
public class DirectoryScannerD implements DirectoryScanner {
	private static final DateTimeFormatter dateFormat;
	
	static {
		dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
	}
	
	private final String filePrefix;
	
	public DirectoryScannerD(String filePrefix) {
		super();
		this.filePrefix = filePrefix;
	}
	
	/**
	 * Перечислить файлы внутридневных данных.
	 * <p>
	 * @param entry условия поиска. Файл - директория поиска, дата -
	 * фильтр по дате. Файлы, соответствующие более ранним датам будут отброшены
	 * @return список подходящих дескрипторов, отсортированный в порядке
	 * возрастания даты  
	 */
	@Override
	public Aqiterator<FileEntry> makeScan(FileEntry entry) {
		List<FileEntry> result = new Vector<FileEntry>();
		int startFrom = filePrefix.length();
		for ( File file : entry.getFile().listFiles() ) {
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
				if ( ! date.isBefore(entry.getDate()) ) {
					result.add(new FileEntry(file, date));
				}
			} catch ( IllegalArgumentException e ) {
				
			}
		}
		Collections.sort(result);
		return new SimpleIterator<FileEntry>(result);
	}

}
