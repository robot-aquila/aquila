package ru.prolib.aquila.core.data;

import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Интерфейс фабрики модификаторов.
 * <p>
 * 2012-10-20<br>
 * $Id: MFactory.java 497 2013-02-06 18:56:51Z whirlwind $
 */
public interface MFactory {

	/**
	 * Создать модификатор торгового счета заявки.
	 * <p>
	 * @param name идентификатор элемента ряда с кодом счета
	 * @return модификатор торгового счета
	 */
	public S<EditableOrder> rowOrdAccount(String name);
	
	/**
	 * Создать модификатор торгового счета заявки.
	 * <p>
	 * @param code идентификатор элемента ряда с кодом счета
	 * @param subCode идентификатор элемента ряда с суб-кодом счета
	 * @return модификатор торгового счета заявки
	 */
	public S<EditableOrder> rowOrdAccount(String code, String subCode);

	/**
	 * Создать модификатор направления заявки.
	 * <p>
	 * Создает модификатор, который преобразует значения ряда в одно из двух
	 * возможных направлений заявки. Если значение ряда соответствует
	 * объекту-эквиваленту, то устанавливается направление
	 * {@link ru.prolib.aquila.core.BusinessEntities.Direction#BUY
	 * OrderDirection#BUY}.
	 * При прочих значениях устанавливается направление
	 * {@link ru.prolib.aquila.core.BusinessEntities.Direction#SELL
	 * OrderDirection#SELL}.
	 * <p>
	 * @param name идентификатор элемента ряда 
	 * @param buyEquiv объект-эквивалент покупки
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdDir(String name, Object buyEquiv);

	/**
	 * Создать модификатор идентификатора заявки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	
	public S<EditableOrder> rowOrdId(String name);

	/**
	 * Создать модификатор цены заявки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdPrice(String name);

	/**
	 * Создать модификатор количества лотов заявки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdQty(String name);

	/**
	 * Создать модификатор неисполненного количества лотов заявки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdQtyRest(String name);

	/**
	 * Создать модификатор дескриптора инструмента заявки.
	 * <p>
	 * @param gSecDescr геттер инструмента
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdSecDescr(G<SecurityDescriptor> gSecDescr);

	/**
	 * Создать модификатор статуса заявки.
	 * <p>
	 * @param name идентификатор элемента ряда со значением ключа
	 * @param map карта сопоставления значения ряда на статус заявки
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdStatus(String name, Map<?, OrderStatus> map);

	/**
	 * Создать модификатор типа заявки.
	 * <p>
	 * Создает модификатор типа заявки, основанный на простом сопоставлении
	 * значения источника с ключом карты сопоставлений.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @param map карта сопоставления значения ряда на тип заявки
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdType(String name, Map<?, OrderType> map);

	/**
	 * Создать модификатор типа заявки.
	 * <p>
	 * Создает модификатор типа заявки, основанный на использовании валидатора
	 * ряда для каждого из возможных возвращаемых значений.
	 * <p>
	 * @param map карта сопоставлений валидатора ряда на тип заявки
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdType(Map<Validator, OrderType> map);
	
	/**
	 * Создать модификатор типа заявки.
	 * <p>
	 * Создает модификатор типа заявки, основанный на геттере типа
	 * {@link GOrderType}.
	 * <p>
	 * @param gSec геттер инструмента
	 * @param price идентификатор цены заявки
	 * @return модификатор
	 */
	public S<EditableOrder> rowOrdType(G<Security> gSec, String price);
	
	/**
	 * Создать модификатор размера лота.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecLot(String name);
	
	/**
	 * Создать модификатор верхнего лимита цены.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecMaxPrice(String name);
	
	/**
	 * Создать модификатор нижнего лимита цены.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecMinPrice(String name);
	
	/**
	 * Создать модификатор стоимости минимального шага цены.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecMinStepPrice(String name);
	
	/**
	 * Создать модификатор размера минимального шага цены.
	 * <p> 
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecMinStepSize(String name);
	
	/**
	 * Создать модификатор точности цены.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecPrecision(String name);
	
	/**
	 * Создать модификатор последней цены.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecLastPrice(String name);
	
	/**
	 * Создать модификатор лучшей цены предложения.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecAskPrice(String name);
	
	/**
	 * Создать модификатор размера предложения.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecAskSize(String name);
	
	/**
	 * Создать модификатор лучшей цены спроса.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecBidPrice(String name);
	
	/**
	 * Создать модификатор размера спроса.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecBidSize(String name);
	
	/**
	 * Создать модификатор цены закрытия.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecClosePrice(String name);
	
	/**
	 * Создать модификатор наименования инструмента.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecDisplayName(String name);
	
	/**
	 * Создать модификатор наивысшей цены за сессию.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecHighPrice(String name);
	
	/**
	 * Создать модификатор наименьшей цены за сессию.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecLowPrice(String name);
	
	/**
	 * Создать модификатор цены открытия.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecOpenPrice(String name);
	
	/**
	 * Создать модификатор статуса инструмента.
	 * <p>
	 * @param gStatus геттер статуса инструмента
	 * @return модификатор
	 */
	public S<EditableSecurity> rowSecStatus(G<SecurityStatus> gStatus);

	/**
	 * Создать модификатор размера кэша.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePortfolio> rowPortCash(String name);
	
	/**
	 * Создать модификатор вариационной маржи.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePortfolio> rowPortVarMargin(String name);
	
	/**
	 * Создать модификатор балансовой стоимости портфеля.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePortfolio> rowPortBalance(String name);
	
	/**
	 * Создать модификатор вариационной маржи позиции.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePosition> rowPosVarMargin(String name);
	
	/**
	 * Создать модификатор размера позиции на момент открытия.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePosition> rowPosOpenValue(String name);
	
	/**
	 * Создать модификатор заблокированного размера позиции.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePosition> rowPosLockValue(String name);
	
	/**
	 * Создать модификатор текущего размера позиции.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<EditablePosition> rowPosCurrValue(String name);
	
	/**
	 * Создать модификатор направления сделки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @param buyEquiv значение-эквивалент покупки
	 * @return модификатор
	 */
	public S<Trade> rowTrdDir(String name, Object buyEquiv);
	
	/**
	 * Создать модификатор идентификатора сделки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<Trade> rowTrdId(String name);
	
	/**
	 * Создать модификатор цены сделки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<Trade> rowTrdPrice(String name);
	
	/**
	 * Создать модификатор количества сделки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<Trade> rowTrdQty(String name);
	
	/**
	 * Создать модификатор дескриптор инструмента сделки.
	 * <p>
	 * @param gSecDescr геттер дескриптора инструмента
	 * @return модификатор
	 */
	public S<Trade> rowTrdSecDescr(G<SecurityDescriptor> gSecDescr);
	
	/**
	 * Создать модификатор времени сделки.
	 * <p>
	 * Создает модификатор времени, основанный на геттере {@link GDate2E}.
	 * <p>
	 * @param date идентификатор элемента ряда со строкой даты
	 * @param time идентификатор элемента ряда со строкой времени
	 * @param dateFormat формат даты
	 * @param timeFormat формат времени
	 * @return модификатор времени сделки
	 */
	public S<Trade> rowTrdTime(String date, String time,
			String dateFormat, String timeFormat);
	
	/**
	 * Создать модификатор времени сделки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<Trade> rowTrdTime(String name);
	
	/**
	 * Создать модификатор объема сделки.
	 * <p>
	 * @param name идентификатор элемента ряда
	 * @return модификатор
	 */
	public S<Trade> rowTrdVolume(String name);

}
