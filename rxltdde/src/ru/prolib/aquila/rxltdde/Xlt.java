package ru.prolib.aquila.rxltdde;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Таблица XlTable. 
 * Ячейкам типа empty, skip и error соответствуют значения null.
 */
@Deprecated
public class Xlt {
	
	public interface ITable extends Serializable {
		
		public int getCols();
		public int getRows();
		public String getTopic();
		public String getItem();
		public Object getCell(int row, int col);

	}
	
	public static class Table implements ITable {
		private static final long serialVersionUID = 5603339036744207048L;
		private final Object[] cells;
		private final String topic,item;
		private final int rows,cols;
		
		public Table(Object[] cells, String topic, String item, int cols) {
			super();
			this.cells = cells;
			this.topic = topic;
			this.item = item;
			this.cols = cols;
			rows = cells.length / (cols == 0 ? 1 : cols);
		}

		@Override
		public Object getCell(int row, int col) {
			return cells[row * cols + col];
		}

		@Override
		public int getCols() {
			return cols;
		}

		@Override
		public String getItem() {
			return item;
		}

		@Override
		public int getRows() {
			return rows;
		}

		@Override
		public String getTopic() {
			return topic;
		}
		
	}
	
	public static class StreamReader {
		private final ByteBuffer buffer;
		
		public StreamReader(byte[] data) {
			super();
			buffer = ByteBuffer.wrap(data);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		
		public int readByte() {
			return buffer.get();
		}
		
		public int readWord() {
			return buffer.getShort();
		}
		
		public int readDword() {
			return buffer.getInt();
		}
		
		public double readDouble() {
			return buffer.getDouble();
		}
		
		public String readString(int length) {
			byte[] chars = new byte[length];
			buffer.get(chars);
			try {
				return new String(chars, "cp1251");
			} catch ( UnsupportedEncodingException e ) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
	}
	
	public static class CellAggregator {
		private final Object[] cells;
		private int index;
		
		public CellAggregator(Object[] cells) {
			this.cells = cells;
			index = 0;
		}
		
		public synchronized void put(Object cell) {
			cells[index] = cell;
			index ++;
		}
		
		public synchronized void skip() {
			cells[index] = null;
			index ++;
		}
		
		public synchronized int getIndex() {
			return index;
		}
		
	}
	
	public static class BlockReader {
		private final StreamReader in;
		private final CellAggregator cells;
		
		public BlockReader(StreamReader in, CellAggregator cells) {
			this.in = in;
			this.cells = cells;
		}
	
		public void readBlank() {
			in.readWord();
			int count = in.readWord();
			for ( int i = 0; i < count; i ++ ) {
				cells.skip();
			}
		}
	
		public void readBoolean() {
			int count = in.readWord() / 2;
			for ( int i = 0; i < count; i ++ ) {
				cells.put(new Boolean(in.readWord() == 1 ? true : false));
			}
		}
		
		public void readError() {
			int count = in.readWord() / 2;
			for ( int i = 0; i < count; i ++ ) {
				in.readWord();
				cells.skip();
			}
		}
		
		public void readDouble() {
			int count = in.readWord() / 8;
			for ( int i = 0; i < count; i ++ ) {
				cells.put(new Double(in.readDouble()));
			}
		}
		
		public void readInteger() {
			int count = in.readWord() / 2;
			for ( int i = 0; i < count; i ++ ) {
				cells.put(new Integer(in.readWord()));
			}
		}
		
		public void readSkip() {
			in.readWord();
			int count = in.readWord();
			for ( int i = 0; i < count; i ++ ) {
				cells.skip();
			}
		}
		
		public void readString() {
			int total = in.readWord();
			while ( total > 0 ) {
				int strlen = in.readByte();
				cells.put(new String(in.readString(strlen)));
				total -= (strlen + 1);
			}
		}
		
	}
	
	public static Table readTable(String topic, String item, byte[] data) {
		StreamReader in = new StreamReader(data);
		in.readDword();
		int rows = in.readWord();
		int cols = in.readWord();
		int total = rows * cols;
		Object[] cells = new Object[total];
		CellAggregator ca = new CellAggregator(cells);
		BlockReader reader = new BlockReader(in, ca);
		while ( ca.getIndex() < total ) {
			switch ( in.readWord() ) {
			case 1:
				reader.readDouble();
				break;
			case 2:
				reader.readString();
				break;
			case 3:
				reader.readBoolean();
				break;
			case 4:
				reader.readError();
				break;
			case 5:
				reader.readBlank();
				break;
			case 6:
				reader.readInteger();
				break;
			case 7:
				reader.readSkip();
				break;
			}
		}
		return new Table(cells, topic, item, cols);
	}

}
