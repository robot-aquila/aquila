package ru.prolib.aquila.core.data.finam.storage;

import java.io.*;
import java.util.*;
import org.joda.time.LocalDate;
import org.joda.time.format.*;

import ru.prolib.aquila.core.data.*;

/**
 * Сканер уровня группировки по годам.
 * <p>
 * Данный метод позволяет отбирать подходящие каталоги для последующего
 * сканирования.
 */
public class DirectoryScannerY implements DirectoryScanner {
	private static final DateTimeFormatter yearFormat;
	
	static {
		yearFormat = DateTimeFormat.forPattern("yyyy");
	}
	
	public DirectoryScannerY() {
		super();
	}
	
	/**
	 * Перечислить каталоги-группировки по годам.
	 * <p>
	 * @param entry условия поиска. Файл - директория поиска, дата -
	 * фильтр по дате. Директории, соответствующие более ранним годам будут
	 * отброшены
	 * @return список подходящих дескрипторов, отсортированный в порядке
	 * возрастания даты. В качестве номера месяца и дня месяца всегда
	 * устанавливается единица.
	 */
	public Aqiterator<FileEntry> makeScan(FileEntry entry) {
		LocalDate start = new LocalDate(entry.getDate().getYear(), 1, 1);
		List<FileEntry> result = new Vector<FileEntry>();
		for ( File file : entry.getFile().listFiles() ) {
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

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DirectoryScannerY.class ) {
			return false;
		}
		return true;
	}
}
