package ru.prolib.aquila.data.storage.hsql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.data.Sequence;
import ru.prolib.aquila.data.SequenceRegistry;

public class HSQLSequenceRegistry implements SequenceRegistry {
	private static final String DEFAULT_NAME_PREFIX = "aquila_data_seq_";
	private final Connection connection;
	private final String namePrefix;
	private final Map<String, HSQLSequence> registry;
	
	HSQLSequenceRegistry(
			Connection connection,
			String name_prefix,
			Map<String, HSQLSequence> registry
		)
	{
		this.connection = connection;
		this.namePrefix = name_prefix;
		this.registry = registry;
	}
	
	public HSQLSequenceRegistry(Connection connection, String name_prefix) {
		this(connection, name_prefix, new HashMap<>());
	}
	
	public HSQLSequenceRegistry(Connection connection) {
		this(connection, DEFAULT_NAME_PREFIX);
	}

	@Override
	public synchronized Sequence get(String sequence_id) {
		HSQLSequence sequence = registry.get(sequence_id);
		if ( sequence == null ) {
			sequence = new HSQLSequence(connection, namePrefix + sequence_id);
			registry.put(sequence_id, sequence);
			try {
				connection.prepareStatement(String.format(
						"CREATE SEQUENCE IF NOT EXISTS \"%s\" AS BIGINT START WITH 1 INCREMENT BY 1",
						(namePrefix + sequence_id).replace("\"", "\"\"")
					)).execute();
			} catch ( SQLException e ) {
				throw new RuntimeException("Query failed", e);
			}
		}
		return sequence;
	}

}
