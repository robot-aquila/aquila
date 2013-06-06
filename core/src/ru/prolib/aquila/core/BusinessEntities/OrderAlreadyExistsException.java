package ru.prolib.aquila.core.BusinessEntities;

/**
 * Заявка уже существует.
 * <p>
 * Исключение выбрасывается при регистрации заявки в случае, если заявка с
 * таким идентификатором (номером транзакции или номером заявки) уже существует.
 */
public class OrderAlreadyExistsException extends OrderException {
	private static final long serialVersionUID = 1L;
	private final EditableOrder order;

	/**
	 * Конструктор.
	 * <p>
	 * @param rejectedOrder заявка, в регистрации которой отказано
	 */
	public OrderAlreadyExistsException(EditableOrder rejectedOrder) {
		super();
		order = rejectedOrder;
	}
	
	/**
	 * Получить экземпляр отклоненной заявки.
	 * <p>
	 * @return экземпляр заявки
	 */
	public EditableOrder getRejectedOrder() {
		return order;
	}
	
	@Override
	public String getMessage() {
		String pfx = "Order already exists: ";
		if ( order == null ) {
			return pfx + "unspecified order instance";
		}
		return pfx + "orderId#" + order.getId() + " "
				   + "transId#" + order.getTransactionId();
	}

}
