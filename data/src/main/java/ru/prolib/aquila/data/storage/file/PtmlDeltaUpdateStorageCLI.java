package ru.prolib.aquila.data.storage.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.DeltaUpdate;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;

/**
 * Test writer to run in separate JVM.
 */
public class PtmlDeltaUpdateStorageCLI {

	static class SampleConverter implements PtmlDeltaUpdateConverter {

		@Override
		public String toString(int token, Object value)
				throws DataFormatException
		{
			return value.toString();
		}

		@Override
		public Object toObject(int token, String value)
				throws DataFormatException
		{
			return value;
		}
		
	}
	
	public static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	public static void main(String[] args) {
		System.exit(new PtmlDeltaUpdateStorageCLI().run());
	}
	
	static class StreamGobbler extends Thread {
		private final BufferedReader reader;
		private final PrintStream dup;
		private final List<String> lines;
		
		public StreamGobbler(String threadID, InputStream in, PrintStream dup) {
			super(threadID);
			this.reader = new BufferedReader(new InputStreamReader(in));
			this.dup = dup;
			this.lines = new ArrayList<>();
		}
		
		public List<String> getLines() {
			synchronized ( lines ) {
				return new ArrayList<>(lines);
			}
		}
		
		@Override
		public void run() {
			String line = null;
			try {
				while ( (line = reader.readLine()) != null ) {
					synchronized ( lines ) {
						lines.add(line);
					}
					if ( dup != null ) {
						dup.println(line);
					}
				}
			} catch ( Throwable t ) {
				t.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		
	}
	
	static class RemoteAPI {
		private final PtmlDeltaUpdatePacker packer;
		private final Process cli;
		private final BufferedWriter input;
		private final StreamGobbler outputGobbler;
		private final StreamGobbler errorGobbler;
		
		public RemoteAPI(boolean dupOut, boolean dupErr) throws IOException {
			packer = new PtmlDeltaUpdatePacker(new SampleConverter());
			cli = new ProcessBuilder(System.getProperty("java.home")
					+ File.separator + "bin" + File.separator + "java",
					"-cp", System.getProperty("java.class.path"),
					PtmlDeltaUpdateStorageCLI.class.getName())
				.start();
			input = new BufferedWriter(new OutputStreamWriter(cli.getOutputStream()));
			outputGobbler = new StreamGobbler("OUTPUT", cli.getInputStream(), dupOut ? System.out : null);
			errorGobbler = new StreamGobbler("ERROR", cli.getErrorStream(), dupErr ? System.err : null);
			outputGobbler.start();
			errorGobbler.start();
		}
		
		public List<String> getOutputLines() {
			return outputGobbler.getLines();
		}
		
		public List<String> getErrorLines() {
			return errorGobbler.getLines();
		}
		
		private void send(String line) throws IOException {
			input.write(line + "\n");
			input.flush();
		}
		
		public void exit() throws IOException {
			send("exit");
			try {
				if ( ! cli.waitFor(2, TimeUnit.SECONDS) ) {
					cli.destroy();
				}
				outputGobbler.join(1000);
				errorGobbler.join(1000);
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}
		
		public void write(DeltaUpdate update) throws IOException, DataFormatException {
			send("write");
			send(packer.toString(update));
		}
		
		public void open(File file) throws IOException {
			send("open " + file);
		}
		
		public void close() throws IOException {
			send("close");
		}
		
		public void help() throws IOException {
			send("help");
		}

	}
	
	private final PtmlDeltaUpdateStorage storage;
	private DeltaUpdateWriter updateWriter;
	
	public PtmlDeltaUpdateStorageCLI() {
		storage = new PtmlDeltaUpdateStorage(new SampleConverter());		
	}
	
	private int run() {
		BufferedReader reader = null;
		int exitCode = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			for ( ;; ) {
				String line = reader.readLine();
				if ( line == null || "quit".equals(line) || "exit".equals(line) ) {
					printSuccess("exit");
					break;
					
				} else if ( "write".equals(line) ) {
					StringBuffer buffer = new StringBuffer();
					String dummy = null;
					while ( (dummy = reader.readLine()) != null ) {
						if ( "".equals(dummy) ) {
							break;
						} else {
							buffer.append(dummy + "\n");
						}
					}
					if ( updateWriter == null ) {
						printError(line, "Writer was not opened");
					} else if ( buffer.length() > 0 ) {
						try {
							DeltaUpdate update = storage.getPacker().toUpdate(buffer.toString());
							updateWriter.writeUpdate(update);
							printSuccess(line);
						} catch ( Exception e ) {
							printError(line, "Failed to write update", e);
						}
					} else {
						printError(line, "An empty update received");
					}
					
				} else if ( "close".equals(line) ) {
					IOUtils.closeQuietly(updateWriter);
					printSuccess(line);
					
				} else if ( "help".equals(line) ) {
					printHelp(System.out);
					printSuccess(line);
					
				} else if ( line.startsWith("open ") ) {
					File file = new File(line.substring(5));
					try {
						IOUtils.closeQuietly(updateWriter);
						updateWriter = storage.createWriter(file);
						printSuccess(line);
					} catch ( IOException e ) {
						printError(line, "Failed  to open a writer", e);
					}
					
				} else {
					printError(line, "Unknown command: " + line);
				}
			}
		} catch ( Exception e ) {
			printError("Unhandled exception", e);
			exitCode = 1;
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return exitCode;
	}
	
	private void printSuccess(String command) {
		System.out.println(Instant.now() + "@OK: " + command);
		System.out.flush();
	}
	
	private void printHelp(PrintStream stream) {
		stream.println("Available commands: ");
		stream.println("  quit|exit - Exit program.");
		stream.println("open <path> - Open the file to write updates.");
		stream.println("      write - Read delta-update from the input and write it to the file.");
		stream.println("              Use an empty line to mark the end of data.");
		stream.println("      close - Close update writer.");
		stream.println("       help - Show this help.");
		stream.flush();
	}

	private void printError(String msg, Throwable t) {
		Instant time = Instant.now();
		System.err.println(time + "@ERR: " + msg);
		t.printStackTrace(System.err);
		System.err.flush();
	}
	
	private void printError(String command, String msg, Throwable t) {
		Instant time = Instant.now();
		System.err.println(time + "@ERR: " + msg);
		t.printStackTrace(System.err);
		System.err.flush();
		System.out.println(time + "@ERR: " + command);
		System.out.flush();
	}
	
	private void printError(String command, String msg) {
		Instant time = Instant.now();
		System.err.println(time + "@ERR: " + msg);
		System.err.flush();
		System.out.println(time + "@ERR: " + command);
		System.out.flush();
	}

}
