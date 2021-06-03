package xyz.kebigon.vra.japwords;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.io.Files;

import xyz.kebigon.vra.japwords.anki.AnkiDatabase;
import xyz.kebigon.vra.japwords.anki.MorphmanDatabase;
import xyz.kebigon.vra.japwords.jisho.WordDefinition;
import xyz.kebigon.vra.japwords.kanji.KanjiService;

public class JapaneseWords
{
	private static final File MORPHMAN_KNOWN_DATABASE = new File("/home/kebigon/.local/share/Anki2/User 1/dbs/known.db");
	private static final File EXTERNAL_KNOWN_WORDS = new File("/home/kebigon/Unison/Documents/japanese/words_lists/external.txt");

	private static final Collection<String> knownWords = new HashSet<>();

	/*
	 * Load words from wanikani.csv, keep the words order
	 */
	private static List<WordDefinition> loadWanikaniFile() throws IOException
	{
		final List<WordDefinition> words = new ArrayList<>();

		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(JapaneseWords.class.getClassLoader().getResourceAsStream("words.tsv"))))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				final String[] fields = line.split("\t");

				final WordDefinition wordDef = new WordDefinition(fields[0]);

				if (!"null".equals(fields[1]))
					wordDef.setWanikaniLevel(Integer.parseInt(fields[1]));
				if (!"null".equals(fields[2]))
					wordDef.setJlptLevel(Integer.parseInt(fields[2]));

				words.add(wordDef);
			}
		}

		return words;
	}

	public static void main(final String[] args) throws Exception
	{
		final KanjiService kanjiService = KanjiService.get();

		final MorphmanDatabase database = new MorphmanDatabase();

		knownWords.addAll(Files.readLines(EXTERNAL_KNOWN_WORDS, Charset.forName("UTF-8")));
		knownWords.addAll(database.load(MORPHMAN_KNOWN_DATABASE));

		System.out.println("Loaded " + knownWords.size() + " known words from Morphman database");

		final List<WordDefinition> words;
		try (final AnkiDatabase ankiDatabase = new AnkiDatabase())
		{
			words = loadWanikaniFile().stream() //
					.filter(word -> !knownWords.contains(word.getWord())) // Ignore already known words
					.map(word -> kanjiService.enrichWordDefinition(word)) //
					.filter(ankiDatabase::hasIPlusOneSentence) //
					.sorted(WordDefinition.DIFFICULTY_COMPARATOR) //
					.collect(Collectors.toList());
		}

		final Map<WordPriority, Collection<WordDefinition>> wordsByPriodity = new HashMap<>();
		for (final WordDefinition word : words)
		{
			final WordPriority priority = WordPriority.getWordPriority(word);
			wordsByPriodity.putIfAbsent(priority, new ArrayList<>());
			wordsByPriodity.get(priority).add(word);
		}

		for (final Entry<WordPriority, Collection<WordDefinition>> entry : wordsByPriodity.entrySet())
		{
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(entry.getKey().getFileName())))
			{
				for (final WordDefinition word : entry.getValue())
					writer.append(word.toString()).append('\n');
			}

			System.out.println(entry.getKey() + ": " + entry.getValue().size() + " words to learn");
		}
	}
}
