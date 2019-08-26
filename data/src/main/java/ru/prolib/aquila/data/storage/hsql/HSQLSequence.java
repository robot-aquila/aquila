package ru.prolib.aquila.data.storage.hsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.prolib.aquila.data.Sequence;

public class HSQLSequence implements Sequence {
	private final Connection connection;
	private final String query, sequenceID;
	
	public HSQLSequence(Connection connection, String sequence_id) {
		this.connection = connection;
		this.query = String.format("CALL NEXT VALUE FOR \"%s\"", sequence_id.replace("\"", "\"\""));
		this.sequenceID = sequence_id;
	}
	
	public String getSequenceID() {
		return sequenceID;
	}

	@Override
	public long next() {
		try {
			PreparedStatement sth = connection.prepareStatement(query);
			ResultSet rs = sth.executeQuery();
			rs.next();
			long result =  rs.getLong(1);
			rs.close();
			sth.close();
			return result;
		} catch ( SQLException e ) {
			throw new RuntimeException("Query failed", e);
		}
	}

}
