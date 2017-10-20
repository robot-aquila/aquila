package ru.prolib.aquila.core.data;

/**
 * Интерфейс редактируемого списка модификаторов.
 * <p>
 * @param <T> - тип изменяемого субъекта
 * <p>
 * 2012-10-30<br>
 * $Id: MList.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public interface MList<T> extends S<T> {
	
	/**
	 * Добавить модификатор.
	 * <p>
	 * Добавляет в список готовый модификатор например, {@link MStd}.
	 * <p> 
	 * @param modifier экземпляр модификатора
	 * @return список (self-instance)
	 */
	public MList<T> add(S<T> modifier);
	
	/**
	 * Создать и добавить модификатор.
	 * <p>
	 * На основе пары геттер-сеттер создает модификатор типа {@link MStd}
	 * и добавляет его в конец списка модификаторов.
	 * <p>
	 * @param getter геттер
	 * @param setter сеттер
	 * @return список (self-instance)
	 */
	public MList<T> add(G<?> getter, S<T> setter);

}
