package ru.prolib.aquila.ta.ds;

import ru.prolib.aquila.ta.Deal;

public interface DealWriter {

	/**
	 * Добавить информацию о сделке.
	 * 
	 * @param deal - сделка
	 * @return возвращает true, если информация была сохранена.
	 * @throws DealWriterException
	 */
	public boolean addDeal(Deal deal) throws DealWriterException;

	/**
	 * Сохранить несохраненные данные.
	 * 
	 * @return возвращает true, если информация была сохранена.
	 * @throws DealWriterException TODO
	 */
	public boolean flush() throws DealWriterException;

}