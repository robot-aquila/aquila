package ru.prolib.aquila.probe.internal;

/**
 * Основные свойства инструмента.
 * <p>
 * Данный класс представляет свойства инструмента, которые не могут быть
 * расчитаны на основании тиковых данных. Такие атрибуты не изменяются на
 * протяжении всего процесса и могут быть использованы для расчета значений
 * других атрибутов (например, в процессе клиринга).
 */
public class SecurityProperties {
	private String displayName;
	private int lotSize, pricePrecision;
	private Double initMarginBase,minStepSize,stepPriceBase;
	
	public SecurityProperties() {
		super();
	}
	
	/**
	 * Получить полное наименование инструмента.
	 * <p>
	 * Полное наименование используется для человекопонятно идентификации
	 * инструмента. 
	 * <p>
	 * @return наименование
	 */
	public String getDisplayName() {
		return  displayName;
	}
	
	/**
	 * Установить полное наименование инструмента.
	 * <p>
	 * @param value наименование
	 */
	public void setDisplayName(String value) {
		this.displayName = value;
	}
	
	/**
	 * Получить размер лота.
	 * <p>
	 * @return размер лота
	 */
	public int getLotSize() {
		return lotSize;
	}
	
	/**
	 * Установить размер лота.
	 * <p>
	 * @param value размер лота
	 */
	public void setLotSize(int value) {
		this.lotSize = value;
	}
	
	/**
	 * Получить величину минимального шага цены.
	 * <p>
	 * @return минимальный шаг цены
	 */
	public Double getMinStepSize() {
		return minStepSize;
	}
	
	/**
	 * Установить минимальный шаг цены.
	 * <p>
	 * @param value минимальный шаг цены
	 */
	public void setMinStepSize(Double value) {
		this.minStepSize = value;
	}
	
	/**
	 * Получить основание для расчета стоимости минимального шага цены.
	 * <p>
	 * Способ использования данного значения специфичен и зависит от типа
	 * инструмента. Данное значение не используется для инструментов,
	 * валюта которых совпадает с базовой валютой. Для инструментов,
	 * котирующихся в валюте отличной от базовой (например, для некоторых
	 * фьючерсов FORTS), данное значение определяет долю от курсовой стоимости
	 * единицы соответствующей валюты, которая соответствует стоимости шага цены
	 * данного инструмента. Например, для фьючерса на индекс RTS данное значение
	 * будет 0.2, что указывает на 20% от курса USD/RUB. Для фьючерсов
	 * на золото или EUR/USD, данное значение будет 0.1, что соответствует
	 * 10% от курса доллара. Для случая эмуляции фьючерсов FORTS, данное
	 * значение используется при определении стоимости шага цены,  расчет
	 * которого выполняется каждый вечерний клиринг. 
	 * <p>
	 * @return основание для расчета
	 */
	public Double getStepPriceCalcBase() {
		return stepPriceBase;
	}
	
	/**
	 * Установить основание для расчета стоимости минимального шага цены.
	 * <p>
	 * @param value основание для расчета
	 */
	public void setStepPriceCalcBase(Double value) {
		this.stepPriceBase = value;
	}
	
	/**
	 * Получить основание для расчета начальной маржи.
	 * <p>
	 * Данное значение используется только для маржируемых инструментов.
	 * Способ использования данного значения специфичен и зависит от типа
	 * инструмента. Например, для фьючерсов FORTS данное значение определяет
	 * долю от расчетной цены, которая используется для вычисления начальной
	 * маржи.  
	 * <p>
	 * @return основание для расчета
	 */
	public Double getInitialMarginCalcBase() {
		return initMarginBase;
	}
	
	/**
	 * Установить основание для расчета начальной маржи.
	 * <p>
	 * @param value основание для расчета
	 */
	public void setInitialMarginCalcBase(Double value) {
		this.initMarginBase = value;
	}

	/**
	 * Получить точность цены.
	 * <p>
	 * @return точность десятичных знаков
	 */
	public int getPricePrecision() {
		return pricePrecision;
	}
	
	/**
	 * Установить точность цены.
	 * <p>
	 * @param value точность цены
	 */
	public void setPricePrecision(int value) {
		this.pricePrecision = value;
	}

}
