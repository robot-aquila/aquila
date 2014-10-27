package ru.prolib.aquila.core.data.finam.storage;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataException;

/**
 * Сканер списка директорий.
 * <p>
 * Итератор, который последовательно сканирует каждый элемент входного списка
 * и представляет полученные результаты в виде единой последовательности
 * элементов. Например, такой сканер может быть использован для перебора всех
 * файлов из списка директорий.
 */
public class DirListScanner implements Aqiterator<FileEntry> {
	private final DirectoryScanner dirScanner;
	private final Aqiterator<FileEntry> dirList;
	private Aqiterator<FileEntry> subList;
	private boolean closed = false;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dirList итератор директорий для сканирования
	 * @param dirScanner сканер директорий
	 */
	public DirListScanner(Aqiterator<FileEntry> dirList, DirectoryScanner dirScanner) {
		super();
		this.dirScanner = dirScanner;
		this.dirList = dirList;
	}

	@Override
	public void close() {
		closed = true;
		dirList.close();
	}

	@Override
	public FileEntry item() throws DataException {
		if ( closed || subList == null ) {
			throw new DataException("No item under cursor");
		}
		return subList.item();
	}

	@Override
	public boolean next() throws DataException {
		if ( closed ) {
			return false;
		}
		if ( subList == null || ! subList.next() ) {
			if ( ! switchToNextSubList() ) {
				close();
				return false;
			}
		}
		return true;
	}
	
	private boolean switchToNextSubList() throws DataException {
		if ( subList != null ) {
			subList.close();
		}
		do {
			if ( ! dirList.next() ) {
				return false;
			}
			subList = dirScanner.makeScan(dirList.item());
		} while ( ! subList.next() );
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DirListScanner.class ) {
			return false;
		}
		DirListScanner o = (DirListScanner) other;
		return new EqualsBuilder()
			.append(dirList, o.dirList)
			.append(dirScanner, o.dirScanner)
			.isEquals();
	}

}
