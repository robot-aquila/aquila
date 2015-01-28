package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Конструктор итератора с подсканом на базе двух сканеров.
 * <p>
 * Данный класс на основании входного элемента создает итератор с подсканом. В
 * своей работе использует два сканера. Первичный сканер используется для
 * формирования на основании входного элемента входного списка для
 * сканирования. Элементы входного списка служат основанием для обращения
 * к вторичному сканеру. Таким образом могут решаться задачи наподобие:
 * по указанному пути отобрать все подкаталоги и просканировать каждый
 * подкаталог на наличие файлов, результат выдать в виде единого списка файлов.
 */
public class SubScanIteratorBuilder<T> implements SubScanner<T> {
	private final SubScanner<T> mainScanner, subScanner;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param mainScanner первичный сканер
	 * @param dirScanner вторичный сканер
	 */
	public SubScanIteratorBuilder(SubScanner<T> mainScanner,
								 SubScanner<T> dirScanner)
	{
		super();
		this.mainScanner = mainScanner;
		this.subScanner = dirScanner;
	}
	
	@Override
	public Aqiterator<T> makeScan(T basis) throws DataException {
		return new SubScanIterator<T>(mainScanner.makeScan(basis), subScanner);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SubScanIteratorBuilder.class ) {
			return false;
		}
		SubScanIteratorBuilder<?> o = (SubScanIteratorBuilder<?>) other;
		return new EqualsBuilder()
			.append(mainScanner, o.mainScanner)
			.append(subScanner, o.subScanner)
			.isEquals();
	}

}
