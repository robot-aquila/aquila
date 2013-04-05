package ru.prolib.aquila.rxltdde.Receiver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.rxltdde.Dde;
import ru.prolib.aquila.rxltdde.Xlt;
import ru.prolib.aquila.rxltdde.Protocol.*;

@Deprecated
public class ReceiverService {
	final static Logger logger = LoggerFactory.getLogger(ReceiverService.class);
	final Selector selector;
	final Dde.IHandler handler;
	ReceiverThread thread = null;
	
	public ReceiverService(String host, int port, Dde.IHandler handler)
		throws ProtocolException
	{
		super();
		try {
			selector = SelectorProvider.provider().openSelector();
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(host, port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch ( IOException e ) {
			throw new ProtocolException("Initialization failed", e);
		}
		this.handler = handler;
	}
	
	public void close() {
		try {
			selector.close();
		} catch ( IOException e ) {
			logger.debug("Close failed", e);
		}
		stopThread();
	}
	
	public Selector getSelector() {
		return selector;
	}
	
	public void processSelectedKeys() throws ProtocolException {
		if ( ! selector.isOpen() ) {
			throw new ProtocolException("Service closed");
		}
		Iterator<SelectionKey> i = selector.selectedKeys().iterator();
		while ( i.hasNext() ) {
			SelectionKey k = i.next();
			i.remove();
			if ( k.isValid() ) {
				if ( k.isAcceptable() ) {
					accept(k);
				}
			}
		}
	}
	
	private synchronized void stopThread() {
		if ( thread != null ) {
			logger.debug("Interrupt receiver thread");
			thread.interrupt();
			try {
				logger.debug("Wait receiver thread");
				thread.join();
			} catch ( InterruptedException e ) {
				logger.error("Unexpectedly interrupted", e);
				Thread.currentThread().interrupt();
			} finally {
				thread = null;
			}
		}
	}
	
	private void accept(SelectionKey key) {
		stopThread();
		Socket socket = null;
		try {
			socket = ((ServerSocketChannel)key.channel()).accept().socket();
		} catch ( IOException e ) {
			logger.error("Accept failed", e);
		}
		thread = new ReceiverThread(socket, handler);
		thread.start();
		logger.debug("New connection accepted");
	}
	
	private static class ReceiverThread extends Thread {
		final Socket socket;
		final Dde.IHandler handler;
		
		private ReceiverThread(Socket socket, Dde.IHandler handler) {
			super();
			this.socket = socket;
			this.handler = handler;
		}
		
		@Override
		public void run() {
			logger.info("Receiver thread started");
			try {
				readAndServe(new ObjectStreamReader(new ObjectInputStream(
						socket.getInputStream())));
			} catch ( IOException e ) {
				logger.error("Create reader failed", e);
			} finally {
				try {
					socket.close();
				} catch ( IOException e1 ) {
					logger.debug("Close failed", e1);
				}
			}
			logger.info("Receiver thread finished");
		}
		
		@Override
		public void interrupt() {
			try {
				if ( ! socket.isClosed() ) {
					socket.close();
				}
			} catch ( IOException e ) {
				logger.error("Close error", e);
			}
			super.interrupt();
		}
		
		private void readAndServe(IReader reader) {
			try {
				while ( ! socket.isClosed() ) {
					Packet request = reader.read();
					switch ( request.getType() ) {
					case Packet.RAWDATA:
						RawData raw = (RawData) request.getData();
						handler.onRawData(raw.topic, raw.item, raw.data);
						break;
					default:
						logger.error("Unknown packet type: {}", request.getType());
					}
				}
			} catch ( ProtocolException e ) {
				if ( ! socket.isClosed() ) {
					logger.error("Error reading packet", e);
				}
			} finally {
				reader.close();
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			System.err.println("Usage: <port> [log4j-config]");
			System.exit(1);
		}
		if ( args.length >= 2 ) {
			PropertyConfigurator.configure(args[1]);
		}
		Dde.IHandler handler = new Dde.IHandler() {
			
			@Override
			public synchronized void onRawData(String topic, String item,
					byte[] data)
			{
				System.out.println("onRawData: topic=" + topic
					+ " item=" + item
					+ " data length=" + data.length);
				
				if ( data.length < 1024 ) {
					Xlt.Table table = Xlt.readTable(topic, item, data);
					for ( int row = 0; row < table.getRows(); row ++ ) {
						for ( int col = 0; col < table.getCols(); col ++ ) {
							System.out.print(table.getCell(row, col) + ",");
						}
						System.out.println();
					}
				} else {
					System.out.println("Too big table. Skip.");
				}
			}
			
		};

		ReceiverService service = new ReceiverService("0.0.0.0",
				Integer.parseInt(args[0]), handler);
		logger.info("Service started");
		Selector selector = service.getSelector();
		while ( selector.select() > -1 ) {
			logger.info("Selector state changed");
			service.processSelectedKeys();
		}
		System.exit(0);
	}

}
