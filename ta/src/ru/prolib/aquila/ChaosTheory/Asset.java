package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.util.Observable;

/**
 * Интерфейс актива.
 * Наблюдаемый объект. Уведомляет наблюдателей об изменениях парамеров актива.
 * Предусмотрены уведомления двух типов: {@link #EVENT_PRICE} при изменении
 * цены актива и {@link #EVENT_CLEARING} при изменении параметров, общих для
 * клирингового периода (расчетная цена, стоимость шага цены, ГО).
 * Тип уведомления передается в качестве второго аргумента метода
 * {@ling java.util.Observer#update(java.util.Observable, Object)}.
 */
public interface Asset extends Observable {
	public static final Integer EVENT_PRICE    = 0x01;
	public static final Integer EVENT_CLEARING = 0x02;
	
	/**
	 * Получить код актива.
	 * @return
	 */
	public String getAssetCode();
	
	/**
	 * Получить код секции биржи.
	 * @return
	 * @throws throws AssetException
	 */
	public String getClassCode() throws AssetException;
	
	/**
	 * Получить шаг цены.
	 * 
	 * Возвращает минимальный шаг цены в единицах измерения цены Например,
	 * для фьючерса на индекс RTS шаг цены равен 5 пунктам.
	 * 
	 * @return
	 * @throws throws AssetException
	 */
	public double getPriceStep() throws AssetException;
	
	/**
	 * Получить точность цены.
	 * 
	 * Возвращает количество значимых десятичных разрядов значения цены.
	 * Используется для округления. 
	 * 
	 * @return
	 * @throws throws AssetException
	 */
	public int getPriceScale() throws AssetException;
	
	/**
	 * Получить текущую цену актива.
	 * 
	 * Возвращает текущую цену актива в единицах измерения цены. Например,
	 * для фьючерса на индекс RTS будет возвращено значение цены в пунктах.
	 * 
	 * @return
	 * @throws throws AssetException
	 */
	public double getPrice() throws AssetException;
	
	/**
	 * Получить стоимость шага цены в деньгах.
	 * 
	 * @return
	 * @throws AssetException
	 */
	public double getPriceStepMoney() throws AssetException;
	
	/**
	 * Перевести цену актива в денежное выражение.
	 * 
	 * @param price цена в единицах измерения цены актива
	 * @return денежное выражение цены
	 * @throws AssetException
	 */
	public double priceToMoney(double price) throws AssetException;
	
	/**
	 * Округлить цену в соответствии с параметрами актива.
	 * 
	 * @param price неокругленная цена
	 * @return
	 * @throws AssetException
	 */
	public double roundPrice(double price) throws AssetException;
	
	/**
	 * Получить величину гарантийного обеспечения в деньгах.
	 * 
	 * @return
	 * @throws AssetException
	 */
	public double getInitialMarginMoney() throws AssetException;
	
	/**
	 * Получить расчетную цену.
	 * 
	 * @return
	 * @throws AssetException
	 */
	public double getEstimatedPrice() throws AssetException;
	
	/**
	 * Форматировать цену.
	 * 
	 * Формирует строку цены в соответствии с параметрами актива.
	 * В случае необходимости, выполняет округление.
	 *  
	 * @param price значение цены в единицах измерения цены актива
	 * @return
	 * @throws throws AssetException
	 */
	public String formatPrice(double price) throws AssetException;

}
