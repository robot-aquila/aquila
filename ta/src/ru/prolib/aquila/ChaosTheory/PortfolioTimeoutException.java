package ru.prolib.aquila.ChaosTheory;

/**
 * Таймаут во время выполнения операции. Возникновение этого исключения
 * может означать что подсистема не отвечает временно и возможно будет
 * доступна несколько позже. Исходя из критичности операции, можно
 * попробовать выполнить запрос позже, либо пропустить операцию, либо
 * завершить работу программы.
 */
public class PortfolioTimeoutException extends PortfolioException {
	private static final long serialVersionUID = 1L;
	
	public PortfolioTimeoutException() {
		super();
	}

	public PortfolioTimeoutException(String msg) {
		super(msg);
	}

	public PortfolioTimeoutException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public PortfolioTimeoutException(Throwable t) {
		super(t);
	}

}