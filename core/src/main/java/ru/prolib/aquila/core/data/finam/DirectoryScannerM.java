package ru.prolib.aquila.core.data.finam;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.core.data.SimpleIterator;
import ru.prolib.aquila.core.data.SubScanner;

/**
 * Сканер уровня группировки по месяцам.
 * <p>
 * Имена каталогов группировки по месяцам соответствуют номеру месяца в
 * году, приведенному к строке в соответствии с шаблоном MM. Данный класс
 * позволяет отобрать подходящие каталоги для последующего сканирования.
 */
public class DirectoryScannerM implements SubScanner<FileEntry> {
	private static final DateTimeFormatter monthFormat;
	
	static {
		monthFormat = DateTimeFormat.forPattern("MM");
	}
	
	public DirectoryScannerM() {
		super();
	}
	
	/**
	 * Перечислить каталоги-группировки по месяцам.
	 * <p>
	 * @param entry условия поиска. Файл - директория поиска, дата -
	 * фильтр по дате. Директории, соответствующие более ранним месяцам года
	 * будут отброшены
	 * @return список подходящих дескрипторов, отсортированный в порядке
	 * возрастания даты. В качестве года устанавливается год даты-аргумента.
	 * В качестве дня месяца всегда устанавливаентся единица.
	 */
	@Override
	public Aqiterator<FileEntry> makeScan(FileEntry entry) throws DataException {
		LocalDate start = entry.getDate().withDayOfMonth(1);
		List<FileEntry> result = new Vector<FileEntry>();
		File dir = entry.getFile();
		if ( ! dir.exists() ) {
			throw new DataException("Path not exists: " + dir);
		}
		for ( File file : dir.listFiles() ) {
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

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DirectoryScannerM.class ) {
			return false;
		}
		return true;
	}

}
