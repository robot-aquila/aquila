package ru.prolib.aquila.ta.ds.quik;

/**
 * Исключение, возбуждаемое в случае отклонения транзакции.
 * В качестве статуса инкапсулирует код ответа quik-а.
 */
public class Tr2QuikRejectedException extends Tr2QuikException {
	private static final long serialVersionUID = -3454383497947100749L;
	private final int status;
	
	public Tr2QuikRejectedException(int status, String descr) {
		super(descr);
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
}