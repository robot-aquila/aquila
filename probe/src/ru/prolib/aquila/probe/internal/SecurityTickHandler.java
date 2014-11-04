package ru.prolib.aquila.probe.internal;

import ru.prolib.aquila.core.data.Tick;

/**
 * 
 * TODO: самый первый этап:
 * - установить полное наименование (display name)
 * - установить размер лота
 * - минимальный шаг цены
 * - установить точность цены
 */
public class SecurityTickHandler implements TickHandler {
	
	public SecurityTickHandler() {
		super();
	}

	/**
	 * Обработка первого тика.
	 * <p>
	 * На данном этапе:<br>
	 * - цена тика устанавливается в качестве значения расчетной цены
	 * - выполняется расчет гарантийного обеспечения
	 * - цена тика используется в качестве цены закрытия предыдущего дня
	 * - нижнюю и верхнюю планку цен в зависимости от расчетной цены
	 * - стоимость минимального шага цены
	 */
	@Override
	public void doInitialTask(Tick firstTick) {
		// TODO: ?
	}

	@Override
	public void doFinalTask(Tick lastTick) {
		// TODO: клиринг?
	}

	@Override
	public void doDailyTask(Tick prevDateTick, Tick nextDateTick) {
		// TODO: настроить клиринги?
	}

	@Override
	public Runnable createTask(Tick tick) {
		// TODO: генерация сделок, установка атрибутов
		return null;
	}

}
