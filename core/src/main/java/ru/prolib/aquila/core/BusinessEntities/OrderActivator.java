package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.BusinessEntities.utils.OrderActivatorLink;

/**
 * Абстрактный активатор заявки.
 */
abstract public class OrderActivator {
	protected final OrderActivatorLink link;
	
	public OrderActivator(OrderActivatorLink link) {
		super();
		this.link = link;
	}
	
	/**
	 * Запуск контроллера активации заявки.
	 * <p>
	 * Данный служебный метод вызывается терминалом и не должен использоваться
	 * пользовательским кодом.
	 * <p>
	 * @param order заявка
	 * @throws OrderException - TODO:
	 */
	protected void start(EditableOrder order) throws OrderException {
		synchronized ( order ) {
			link.link(this, order);
			begin();
		}
	}
	
	/**
	 * Останов контроллера активации заявки.
	 * <p>
	 * Данный служебный метод вызывается терминалом и не должен использоваться
	 * пользовательским кодом.
	 */
	protected void stop() {
		synchronized ( link.getOrder() ) {
			finish();
		}
	}
	
	/**
	 * Получить связанную заявку.
	 * <p>
	 * @return заявка
	 */
	final protected EditableOrder getOrder() {
		return link.getOrder();
	}
	
	/**
	 * Активировать заявку.
	 * <p>
	 * Делегат к {@link OrderActivatorLink#activate()}.
	 */
	final protected void activate() {
		link.activate();
	}
	
	/**
	 * Начать отслеживание условий активации.
	 * <p>
	 * Данный метод определяется конкретной реализацией активатора.
	 * <p>
	 * @throws OrderException - TODO:
	 */
	abstract protected void begin() throws OrderException;
	
	/**
	 * Прекратить отслеживание условий активации.
	 * <p>
	 * Данный метод определяется конкретной реализацией активатора.
	 */
	abstract protected void finish();

}
