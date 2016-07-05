package ru.prolib.aquila.core.BusinessEntities;

/**
 * Transaction to change container.
 * <p>
 * Every transaction is tied to a separate container and can be applied once per
 * life. Transaction allows access to container attributes which will be changed
 * when transaction applied. Transaction cannot be changed after creation.
 * It is read-only object which may or may not be applied to the container.
 */
public interface ContainerTransaction {
	
	/**
	 * Test transaction was applied to the container.
	 * <p>
	 * @return true if applied, false otherwise
	 */
	public boolean isApplied();

	/**
	 * Apply transaction.
	 * <p>
	 * @throws ContainerTransactionException - transaction already applied or
	 * cannot apply transaction
	 */
	public void apply() throws ContainerTransactionException;

}
