package ru.prolib.aquila.core.data.finam.storage;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.*;

/**
 * Адаптер итератора к типу {@link java.io.File File}.
 * <p>
 * Данный класс адаптирует итератор по типу {@link FileEntry} к итератору по
 * типу {@link java.io.File File}.
 */
public class FileEntry2FileIterator implements Aqiterator<File> {
	private final Aqiterator<FileEntry> files;
	
	public FileEntry2FileIterator(Aqiterator<FileEntry> files) {
		super();
		this.files = files;
	}

	@Override
	public void close() {
		files.close();
	}

	@Override
	public File item() throws DataException {
		return files.item().getFile();
	}

	@Override
	public boolean next() throws DataException {
		return files.next();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FileEntry2FileIterator.class ) {
			return false;
		}
		FileEntry2FileIterator o = (FileEntry2FileIterator) other;
		return new EqualsBuilder()
			.append(files, o.files)
			.isEquals();
	}

}
