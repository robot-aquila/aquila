package ru.prolib.aquila.rxltdde;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Описание пакетов
 * 
 * type		  | request data           | response data
 * -----------------------------------------------------------------------------
 * RAWDATA    | RawData                | none
 */
@Deprecated
public class Protocol {
	final static Logger logger = LoggerFactory.getLogger(Protocol.class);
	
	public static class ProtocolException extends Exception {
		private static final long serialVersionUID = -3428005135904768708L;

		public ProtocolException(String msg) {
			super(msg);
		}
		
		public ProtocolException(String msg, Throwable t) {
			super(msg, t);
		}
		
	}
	
	public static class UnsupportedProtocolException extends ProtocolException {
		private static final long serialVersionUID = -3997908833335394779L;

		public UnsupportedProtocolException(String msg) {
			super(msg);
		}
		
		public UnsupportedProtocolException(String msg, Throwable t) {
			super(msg, t);
		}
		
	}
	
	public static class ReadException extends ProtocolException {
		private static final long serialVersionUID = 886332982555518981L;

		public ReadException(String msg) {
			super(msg);
		}
		
		public ReadException(String msg, Throwable t) {
			super(msg, t);
		}
		
	}

	public static class WriteException extends ProtocolException {
		private static final long serialVersionUID = 3645538880010349654L;

		public WriteException(String msg) {
			super(msg);
		}
		
		public WriteException(String msg, Throwable t) {
			super(msg, t);
		}
		
	}
	
	/**
	 * 
	 */
	public interface IReader {
		
		/**
		 * Прочитать пакет из потока.
		 * 
		 * Данный метод выполняет блокирующее чтение транспортного потока и
		 * предназначен для использования принимающей стороной (обработчиком
		 * DDE-транзакций). 
		 * 
		 * @return входящий пакет
		 * @throws ProtocolException
		 */
		public Packet read() throws ProtocolException;
		
		public void close();
	
	}
	
	public interface IWriter {

		/**
		 * Записать пакет в поток.
		 * 
		 * Данный метод выполняет запись пакета в транспортный поток и
		 * предназначен для использования отправляющей стороной.
		 * 
		 * @param packet исходящий пакет
		 * @throws ProtocolException
		 */
		public void write(Packet packet) throws ProtocolException;

		public void close();

	}
	
	/**
	 * 
	 * 
	 */
	public static class ObjectStreamWriter implements IWriter {
		final static Logger logger =
			LoggerFactory.getLogger(ObjectStreamWriter.class);
		final ObjectOutputStream out;
		
		public ObjectStreamWriter(ObjectOutputStream stream) {
			super();
			out = stream;
		}

		@Override
		public void write(Packet packet) throws ProtocolException {
			try {
				out.writeObject(packet);
				out.reset();
				out.flush();
			} catch (IOException e) {
				throw new WriteException(e.getMessage(), e);
			}
		}

		@Override
		public void close() {
			try {
				out.close();
			} catch ( IOException e ) {
				logger.debug("Close failed", e);
			}
		}
		
	}
	
	/**
	 * 
	 */
	public static class ObjectStreamReader implements IReader {
		final static Logger logger =
			LoggerFactory.getLogger(ObjectStreamReader.class); 
		final ObjectInputStream in;
		
		public ObjectStreamReader(ObjectInputStream stream) {
			super();
			in = stream;
		}

		@Override
		public Packet read() throws ProtocolException {
			try {
				return (Packet) in.readObject();
			} catch ( ClassNotFoundException e ) {
				throw new UnsupportedProtocolException(e.getMessage(), e);
			} catch ( IOException e ) {
				throw new ReadException(e.getMessage(), e);
			}
		}


		@Override
		public void close() {
			try {
				in.close();
			} catch ( IOException e ) {
				logger.debug("Close failed", e);
			}
		}

	}

	/**
	 * 
	 * 
	 */
	public static class SocketWriter implements IWriter {
		final static Logger logger = LoggerFactory.getLogger(SocketWriter.class);
		final int retries;
		final long pause;
		final String host;
		final int port;
		IWriter writer = null;
		Socket socket = null;

		public SocketWriter(String host, int port, int retries, long pause) {
			super();
			this.host = host;
			this.port = port;
			this.retries = retries;
			this.pause = pause;
		}
		
		public SocketWriter(String host, int port, long pause) {
			this(host, port, 0, pause);
		}
		
		public SocketWriter(String host, int port) {
			this(host, port, 5000);
		}
		
		private IWriter getWriter() throws IOException {
			if ( writer == null ) {
				socket = new Socket(host, port);
				writer = new ObjectStreamWriter(new ObjectOutputStream(
						socket.getOutputStream()));
			}
			return writer;
		}

		@Override
		public synchronized void write(Packet packet) throws ProtocolException {
			int i = 1;
			while ( true ) {
				try {
					getWriter().write(packet);
					return;
				} catch ( Exception e ) {
					logger.info("Write packet {} attempt number {} failed",
							new Object[]{packet.getId(), i, e});
					close();
					if ( retries > 0 && i > retries ) {
						throw new ProtocolException("Too many attempts", e);
					}
					try {
						Thread.sleep(pause);
					} catch ( InterruptedException e1 ) {
						logger.error("Unexpectedly interrupted", e1);
						Thread.currentThread().interrupt();
						return;
					}
				}
				i ++;
			}
		}

		@Override
		public void close() {
			if ( socket != null ) {
				try {
					if ( ! socket.isClosed() ) {
						socket.shutdownOutput();
						if ( writer != null ) {
							writer.close();
						}
						socket.close();
						logger.info("Socket closed");
					}
					
				} catch ( IOException e ) {
					logger.error("Socket close error", e);
				}
			}
			socket = null;
			writer = null;
		}
		
	}
	
	public static class RawData implements Serializable {
		private static final long serialVersionUID = -1770741202546599998L;
		public final String topic;
		public final String item;
		public final byte[] data;
		
		public RawData(String topic, String item, byte[] data) {
			super();
			this.topic = topic;
			this.item = item;
			this.data = data;
		}
	}
	
	public static class Packet implements Serializable {
		private static volatile long lastId = 0;
		private static final long serialVersionUID = -8922786493567531593L + 4;
		public static final int UNKNOWN		= 0x00;
		public static final int RAWDATA		= 0x03;
		private final int type;
		private final Serializable data;
		private final long id;
		
		public Packet(int type, Serializable data) {
			super();
			this.type = type;
			this.data = data;
			id = lastId ++;
		}
		
		public int getType() {
			return type;
		}
		
		public Object getData() {
			return data;
		}
		
		public long getId() {
			return id;
		}
		
	}

}
