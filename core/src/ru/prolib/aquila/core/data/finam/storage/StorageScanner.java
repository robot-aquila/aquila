package ru.prolib.aquila.core.data.finam.storage;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
	 * Внутридневные данные хранятся в файлах с именами S-N-C-T-yyyyMMdd
	 * и расширением csv или csv.gz. Данный метод сканирует указанный каталог
	 * на предмет наличия таких файлов. Имена файлов преобразуются в даты,
	 * которые, при условии подходящей даты, помещаются в список.
	 * <p>
	 * @param filePrefix префикс имени файла
	 * @param path путь к каталогу с файлами
	 * @param from дата, включительно с которой интересует наличие данных  
	 * @return отсортированный список подходящих дат  
	 */
	public List<LocalDate> findIntradayFiles(final String filePrefix, File path,
			LocalDate from)
	{
		List<LocalDate> result = new ArrayList<LocalDate>();
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
				if ( ! date.isBefore(from) ) {
					result.add(date);
				}
			} catch ( IllegalArgumentException e ) {
				
			}
		}
		Collections.sort(result);
		return result;
	}

}
