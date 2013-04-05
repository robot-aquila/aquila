package ru.prolib.aquila.ib.getter;

import com.ib.client.Contract;

import ru.prolib.aquila.core.data.G;

/**
 * Заготовка геттера на основе экземпляра контракта.
 * <p>
 * @param <R> тип возвращаемого значения
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetContractAttr.java 433 2013-01-14 22:37:52Z whirlwind $
 */
abstract public class IBGetContractAttr<R> implements G<R> {
	
	/**
	 * Конструктор.
	 */
	public IBGetContractAttr() {
		super();
	}

	@Override
	public R get(Object source) {
		return source instanceof Contract
			? getContractAttr((Contract) source) : null;
	}
	
	/**
	 * Получить атрибут контракта.
	 * <p>
	 * @param contract контракт
	 * @return значение атрибута
	 */
	abstract protected R getContractAttr(Contract contract);

}
