package ru.prolib.aquila.core.data.finam.storage;

import ru.prolib.aquila.core.data.Aqiterator;

/**
 * Интерфейс сканера директории.
 */
public interface DirectoryScanner {
	
	/**
	 * Выполнить сканирование.
	 * <p>
	 * @param entry дескриптор, описывающий условия сканирования
 	 * @return список подходящих дескрипторов
	 */
	public Aqiterator<FileEntry> makeScan(FileEntry entry);

}
