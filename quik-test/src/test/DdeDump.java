package test;

import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDEServer;
import ru.prolib.aquila.dde.DDEService;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.jddesvr.JddesvrServer;

public class DdeDump implements DDEService {
	private static String NULL = "<NULL>";
	private static int MAX_ROWS = 20;
	private DDEServer server;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new DdeDump().run();
	}
	
	private void run() throws Exception {
		server = JddesvrServer.getInstance();
		server.start();
		server.registerService(this);
		server.join();
	}

	@Override
	public String getName() {
		return "AQUILA";
	}

	@Override
	public boolean onConnect(String topic) {
		print("Connected: " + topic);
		return true;
	}

	@Override
	public void onConnectConfirm(String topic) {
		
	}

	@Override
	public boolean onData(String topic, String item, byte[] dataBuffer) {
		return false;
	}

	@Override
	public void onDisconnect(String topic) {
		print("Disconnected: topic");
		try {
			server.stop();
		} catch ( DDEException e ) {
			System.err.println(e);
		}
	}

	@Override
	public void onRegister() {
		
	}

	@Override
	public void onTable(DDETable t) {
		print("Topic: " + t.getTopic() + " Item: " + t.getItem() + "\n");
		int count = t.getRows() <= MAX_ROWS ? t.getRows() : MAX_ROWS; 
		int[] width = getColumnsWidth(t);
		for ( int row = 0; row < count; row ++ ) {
			for ( int col = 0; col < t.getCols(); col ++ ) {
				printCell(t.getCell(row, col), width[col]);
			}
			print("\n");
		}
	}

	@Override
	public void onUnregister() {
		
	}
	
	/**
	 * Напечатать объект.
	 * <p>
	 * @param obj
	 */
	private void print(Object obj) {
		System.out.print(obj);
	}
	
	/**
	 * Напечатать значение ячейки с учетом максимальной ширины.
	 * <p>
	 * @param cell значение ячейки
	 * @param colWidth ширина колонки
	 */
	private void printCell(Object cell, int colWidth) {
		if ( cell == null ) {
			cell = NULL;
		}
		int rem = colWidth - cell.toString().length();
		print(cell);
		for ( int i = 0; i < rem; i ++ ) {
			print(" ");
		}
		print(" ");
	}
	
	/**
	 * Расчитать ширину каждой колонки таблицы.
	 * <p>
	 * @param t таблица
	 * @return массив с максимальной шириной значения колонки
	 */
	private int[] getColumnsWidth(DDETable t) {
		int[] width = new int[t.getCols()];
		int len = 0;
		for ( int col = 0; col < width.length; col ++ ) {
			int max = 0;
			for ( int row = 0; row < t.getRows(); row ++ ) {
				Object cell = t.getCell(row, col);
				len = (cell == null ? NULL.length() : cell.toString().length());
				max = (len > max ? len : max);
			}
			width[col] = max;
		}
		return width;
	}

}
