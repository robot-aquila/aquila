package ru.prolib.aquila.ta.ds;

public class DataSetIteratorLimit extends DataSetIteratorDecorator {
	private final int limit;
	private int iterated = 0;
	private boolean closeDecorated = false;
	
	public DataSetIteratorLimit(DataSetIterator iterator, int limit) {
		super(iterator);
		if ( iterator == null ) {
			throw new NullPointerException("iterator");
		}
		this.limit = limit;
	}
	
	public int getLimit() {
		return limit;
	}
	
	@Override
	public boolean next() throws DataSetException {
		if ( iterated < limit ) {
			if ( getDataSetIterator().next() ) {
				iterated ++;
				return true;
			} else {
				closeDecorated = true;
			}
		}
		return false;
	}
	
	@Override
	public void close() {
		if ( closeDecorated ) {
			getDataSetIterator().close();
		}
	}

}
