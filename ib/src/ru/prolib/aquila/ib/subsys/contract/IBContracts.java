package ru.prolib.aquila.ib.subsys.contract;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.ib.IBException;

/**
 * Интерфейс фасада подсистемы контрактов.
 * <p>
 * В AQUILA инструмент представляет собой сущность, связанную с конкретной
 * биржей. Это значит через инструмент AQUILA доступны котировки и все операции
 * связанные с конкретным инструментом на конкретной бирже. Контракты IB имеют
 * уникальные идентификаторы, но могут котироваться на разных биржах (то есть,
 * помимо идентификатора контракта требуется указывать биржу для всех операции).
 * <p>
 * В связи с этим, выполнить прямое сопоставление контракта с инструментом
 * AQUILA не представляется возможным. Так как с отдельным контрактом IB может
 * быть связано несколько инструментов AQUILA, требуется дополнительный
 * функционал, который позволит в любое время обращаться к деталям контракта по
 * имеющимся идентифицирующим признакам и приводить эти признаки в нормальную
 * форму для AQUILA форму (то есть в дескриптор инструмента).
 * <p>
 * Данный файл определяет интерфейс подсистемы контрактов, которая позволяет
 * наиболее корректно сопоставить информацию IB с инструментом AQUILA.
 * <p>
 * 2013-01-07<br>
 * $Id: IBContracts.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface IBContracts extends IBContractsStorage, IBContractUtils {
	
	/**
	 * Сформировать наиболее подходящий дескриптор инструмента.
	 * <p>
	 * @param conId идентификатор контракта
	 * @return дескриптор инструмента
	 * @throws IBContractUnavailableException указанный контракт не загружен
	 */
	public SecurityDescriptor getAppropriateSecurityDescriptor(int conId)
			throws IBException;

}
