package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;
import static ru.prolib.aquila.data.storage.file.PtmlDeltaUpdateStorageCLI.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.data.DataFormatException;

public class PtmlDeltaUpdatePackerTest {
	private static final int NAME = 1;
	private static final int AGE = 2;
	private static final int BIRTH_DATE = 3;
	
	static class SampleConverter implements PtmlDeltaUpdateConverter {
		private static final DateTimeFormatter dateFormat;
		
		static {
			dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		}

		@Override
		public String toString(int token, Object value)
				throws DataFormatException
		{
			switch ( token ) {
			case NAME:
			case AGE:
				return value.toString();
			case BIRTH_DATE:
				return ((LocalDate) value).format(dateFormat);
			default:
				throw new DataFormatException("Unknown token: " + token);
			}
		}

		@Override
		public Object toObject(int token, String value)
				throws DataFormatException
		{
			switch ( token ) {
			case NAME:
				return value;
			case AGE:
				return Integer.valueOf(value);
			case BIRTH_DATE:
				return LocalDate.parse(value, dateFormat);
			default:
				throw new DataFormatException("Unknown token: " + token);
			}
		}
		
	}
	
	private PtmlDeltaUpdateConverter converter;
	private PtmlDeltaUpdatePacker packer;

	@Before
	public void setUp() throws Exception {
		converter = new SampleConverter();
		packer = new PtmlDeltaUpdatePacker(converter);
	}

	@Test
	public void testToBytes() throws Exception {
		String actual = packer.toString(new DeltaUpdateBuilder()
			.withTime(T("2017-01-15T00:00:35.987Z"))
			.withSnapshot(true)
			.withToken(NAME, "Sulaco\nMS-17345")
			.withToken(AGE, 100)
			.withToken(BIRTH_DATE, LocalDate.of(3015, 1, 24))
			.buildUpdate());
		
		String expected = "1:2017-01-15T00:00:35.987Z\n" +
				"1:Sulaco%0AMS-17345\n" +
				"2:100\n" +
				"3:3015-01-24\n";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testToUpdate() throws Exception {
		String record = "1:2017-01-15T00:00:35.987Z\n" +
				"1:Sulaco%0AMS-17345\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		DeltaUpdate actual = packer.toUpdate(record);
		
		assertEquals(new DeltaUpdateBuilder()
			.withTime(T("2017-01-15T00:00:35.987Z"))
			.withSnapshot(true)
			.withToken(NAME, "Sulaco\nMS-17345")
			.withToken(AGE, 100)
			.withToken(BIRTH_DATE, LocalDate.of(3015, 1, 24))
			.buildUpdate(), actual);
	}
	
	@Test (expected=DataFormatException.class)
	public void testToUpdate_ThrowsWhenLessThanTwoLines() throws Exception {
		packer.toUpdate("foobar");
	}
	
	@Test (expected=DataFormatException.class)
	public void testToUpdate_ThrowsWhenUnexpectedHeaderFormat() throws Exception {
		String record = "foobar\n" +
				"1:Sulaco%0AMS-17345\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		packer.toUpdate(record);
	}

	@Test (expected=DataFormatException.class)
	public void testToUpdate_ThrowsWhenInvalidSnapshotMarker() throws Exception {
		String record = "X:2017-01-15T00:00:35.987Z\n" +
				"1:Sulaco%0AMS-17345\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		packer.toUpdate(record);
	}
	
	@Test (expected=DataFormatException.class)
	public void testToUpdate_ThrowsWhenInvalidUpdateTime() throws Exception {
		String record = "0:zulu\n" +
				"1:Sulaco%0AMS-17345\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		packer.toUpdate(record);
	}
	
	@Test (expected=DataFormatException.class)
	public void testToUpdate_ThrowsWhenTokenNotFound() throws Exception {
		String record = "0:2017-01-15T00:00:35.987Z\n" +
				"delta\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		packer.toUpdate(record);
	}
	
	@Test (expected=DataFormatException.class)
	public void testToUpdate_ThrowsWhenInvalidTokenFormat() throws Exception {
		String record = "0:2017-01-15T00:00:35.987Z\n" +
				"X:Sulaco\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		packer.toUpdate(record);
	}

	@Test
	public void testToUpdate_EmptyTokenValueOK() throws Exception {
		String record = "0:2017-01-15T00:00:35.987Z\n" +
				"1:\n" +
				"2:100\n" +
				"3:3015-01-24\n";

		DeltaUpdate actual = packer.toUpdate(record);
		
		assertEquals(new DeltaUpdateBuilder()
			.withTime(T("2017-01-15T00:00:35.987Z"))
			.withSnapshot(false)
			.withToken(NAME, "")
			.withToken(AGE, 100)
			.withToken(BIRTH_DATE, LocalDate.of(3015, 1, 24))
			.buildUpdate(), actual);
	}

}
