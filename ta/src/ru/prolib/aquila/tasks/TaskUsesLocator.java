package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;

/**
 * Задача, использующая локатор сервисов.
 * Подобные задачи должны извлекать необходимые сервисы в конструкторе.
 * Если какой либо из необходимых сервисов недоступен, это должно выясняться
 * на этапе конструкции объекта.
 */
abstract public class TaskUsesLocator extends TaskCommon {
	protected final ServiceLocator locator;
	
	public TaskUsesLocator(ServiceLocator locator) {
		super();
		this.locator = locator;
	}

}
