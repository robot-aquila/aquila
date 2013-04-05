package ru.prolib.aquila.ta.ds.quik;

/**
 * Результат обработки транзакции.
 */
public class Tr2QuikResult {
	public final int status;
	public final long transId;
	public final String description;
	public final long orderNumber;
	
	Tr2QuikResult(long transId, int status,
				  String descr, long orderNum)
	{
		super();
		this.transId = transId;
		this.status = status;
		this.description = descr;
		this.orderNumber = orderNum;
	}
	
	@Override
	public String toString() {
		return "transId=" + transId
			+ " status=" + status
			+ " descr=" + description
			+ " order=" + orderNumber;
	}
	
}