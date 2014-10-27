package ru.prolib.aquila.core.data.finam.storage;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.Aqiterator;

/**
 * Конструктор сканнера директорий.
 * <p>
 * Данный класс на основании записи {@link EntryFile} создает сканер списка
 * директорий типа {@link DirListScanner}. В свое работе использует два сканера:
 * первичный и вторичный сканер директории. Первичный сканер используется для
 * формирования на основании дескриптора {@link FileEntry} списка директорий для
 * сканирования. На основании списка директорий и вторичногол сканера
 * инстанцируется экземпляр класса {@link DirListScanner}.   
 */
public class DirListScannerBuilder implements DirectoryScanner {
	private final DirectoryScanner mainScanner, dirScanner;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param mainScanner первичный сканер
	 * @param dirScanner сканер директорий
	 */
	public DirListScannerBuilder(DirectoryScanner mainScanner,
								 DirectoryScanner dirScanner)
	{
		super();
		this.mainScanner = mainScanner;
		this.dirScanner = dirScanner;
	}
	
	@Override
	public Aqiterator<FileEntry> makeScan(FileEntry entry) {
		return new DirListScanner(mainScanner.makeScan(entry), dirScanner);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DirListScannerBuilder.class ) {
			return false;
		}
		DirListScannerBuilder o = (DirListScannerBuilder) other;
		return new EqualsBuilder()
			.append(mainScanner, o.mainScanner)
			.append(dirScanner, o.dirScanner)
			.isEquals();
	}

}
