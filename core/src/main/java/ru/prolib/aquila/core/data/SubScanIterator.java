package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Итератор с подсканированием.
 * <p>
 * Данный класс обеспечивает последовательную обработку элементов входного
 * списка и предоставляет полученные результаты в виде единой последовательности
 * однотипных элементов. Алгоритм обработки элементов входного списка
 * должен имлементировать интерфейс {@link SubScanner}. Таким образом, путем
 * спецификации только подсканера можно выполнять задачи наподобии сканирования
 * списка каталогов и другие, где типы входных и выходных данных совпадают.   
 * <p> 
 * @param <T> - тип элемента
 */
public class SubScanIterator<T> implements Aqiterator<T> {
	private final Aqiterator<T> list;
	private final SubScanner<T> scanner;
	private Aqiterator<T> subList;
	private boolean closed = false;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param list входящий список, элементы которого будут служить основанием
	 * для сканирования
	 * @param scanner подсканер, реализующий механизм преобразования входящего
	 * элемента в список исходящих
	 */
	public SubScanIterator(Aqiterator<T> list, SubScanner<T> scanner) {
		super();
		this.list = list;
		this.scanner = scanner;
	}

	@Override
	public void close() {
		if ( ! closed ) {
			if ( subList != null ) {
				subList.close();
				subList = null;
			}
			list.close();
			closed = true;
		}
	}

	@Override
	public T item() throws DataException {
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
			if ( ! list.next() ) {
				return false;
			}
			subList = scanner.makeScan(list.item());
		} while ( ! subList.next() );
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SubScanIterator.class ) {
			return false;
		}
		SubScanIterator<?> o = (SubScanIterator<?>) other;
		return new EqualsBuilder()
			.append(list, o.list)
			.append(scanner, o.scanner)
			.isEquals();
	}

}
