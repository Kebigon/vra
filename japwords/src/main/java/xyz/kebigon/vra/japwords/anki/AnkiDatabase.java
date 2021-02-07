package xyz.kebigon.vra.japwords.anki;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import xyz.kebigon.vra.japwords.jisho.WordDefinition;

public class AnkiDatabase implements AutoCloseable
{
	private static final String ANKI_DATABASE = "/home/kebigon/.local/share/Anki2/User 1/collection.anki2";

	private final Connection connection;

	public AnkiDatabase() throws SQLException
	{
		connection = DriverManager.getConnection("jdbc:sqlite:" + ANKI_DATABASE);
	}

	public boolean hasIPlusOneSentence(WordDefinition word)
	{
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT COUNT(*) FROM notes WHERE tags LIKE '%i+1%' AND tags NOT LIKE '%TooLong%' AND tags NOT LIKE '%TooShort%' AND flds LIKE ?"))
		{
			ps.setString(1, "%" + word.getWord() + "%");
			final ResultSet rs = ps.executeQuery();

			if (!rs.next())
				return false;

			return rs.getInt(1) != 0;
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void close() throws SQLException
	{
		if (connection != null && !connection.isClosed())
			connection.close();
	}
}
