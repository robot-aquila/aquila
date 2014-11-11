package ru.prolib.aquila.probe.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.Tick;

/**
 * 
 * TODO: самый первый этап:
 * - установить полное наименование (display name)
 * - установить размер лота
 * - минимальный шаг цены
 * - установить точность цены
 */
public class CommonTickHandler implements TickHandler {
	private final EditableSecurity security;
	
	public CommonTickHandler(EditableSecurity security) {
		super();
		this.security = security;
	}

	/**
	 * Обработка первого тика.
	 * <p>
	 * ! - позже.
	 * <p>
	 * На данном этапе:<br>
	 * - (!) цена тика устанавливается в качестве значения расчетной цены
	 * - (!) выполняется расчет гарантийного обеспечения
	 * - цена тика используется в качестве цены закрытия предыдущего дня
	 * - (!) нижнюю и верхнюю планку цен в зависимости от расчетной цены
	 * - (!) стоимость минимального шага цены
	 */
	@Override
	public void doInitialTask(Tick firstTick) {
		// TODO: ?
	}

	@Override
	public void doFinalTask(Tick lastTick) {
		// TODO: клиринг не нужен т.к. запланирован на этапе daily task
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
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CommonTickHandler.class ) {
			return false;
		}
		CommonTickHandler o = (CommonTickHandler) other;
		return new EqualsBuilder()
			.append(o.security, security)
			.isEquals();
	}

}
