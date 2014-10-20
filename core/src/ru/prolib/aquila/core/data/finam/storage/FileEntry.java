package ru.prolib.aquila.core.data.finam.storage;

import java.io.File;

import org.joda.time.LocalDate;

/**
 * Дескриптор файла данных.
 * <p>
 * Инкапсулирует характерные атрибуты файлов исторических данных. 
 */
public class FileEntry implements Comparable<FileEntry> {
	private final File file;
	private final LocalDate date;
	
	public FileEntry(File file, LocalDate date) {
		super();
		this.file = file;
		this.date = date;
	}
	
	public File getFile() {
		return file;
	}
	
	public LocalDate getDate() {
		return date;
	}

	@Override
	public int compareTo(FileEntry o) {
		if ( o == null ) {
			return 1;
		}
		int x = date.compareTo(o.date);
		if ( x != 0 ) {
			return x;
		}
		return file.compareTo(o.file);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FileEntry.class ) {
			return false;
		}
		return compareTo((FileEntry) other) == 0;
	}
	
	@Override
	public String toString() {
		return getClass()
				.getSimpleName() + "[date=" + date + " file=" + file + "]";
	}

}
