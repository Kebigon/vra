package xyz.kebigon.vra.japwords.kanji;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.kebigon.vra.japwords.jisho.WordDefinition;

public class KanjiService
{
	private final KanjiMap kanjis;

	private KanjiService() throws IOException
	{
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(Feature.ALLOW_COMMENTS, true);
		kanjis = objectMapper.readValue(getClass().getClassLoader().getResource("kanji.json"), KanjiMap.class);
	}

	public WordDefinition enrichWordDefinition(WordDefinition def)
	{
		final String word = def.getWord();
		final Collection<Kanji> wordKanjis = new HashSet<>();

		for (int i = 0; i != word.length(); i++)
		{
			final Kanji kanji = kanjis.get(word.substring(i, i + 1));
			if (kanji == null)
				continue;
			wordKanjis.add(kanji);
		}

		def.setKanjis(wordKanjis);
		return def;
	}

	/*
	 * Singleton
	 */

	private static KanjiService instance;

	public static KanjiService get() throws IOException
	{
		if (instance == null)
			instance = new KanjiService();
		return instance;
	}
}
