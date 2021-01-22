package xyz.kebigon.vra.japwords.jisho;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Browse jisho.org to retrieve all words by wanikani/jlpt level and save them in words.tsv
 * 
 * @author kebigon
 */
public class WordsFileGenerator
{
	private static final Collection<String> TAGS = new HashSet<>();
	static
	{
		for (int wanikaniLevel = 1; wanikaniLevel <= 60; wanikaniLevel++)
			TAGS.add("wanikani" + wanikaniLevel);
		for (int jlptLevel = 1; jlptLevel <= 5; jlptLevel++)
			TAGS.add("jlpt-n" + jlptLevel);
	}

	public static void main(String[] args) throws IOException
	{
		final JishoBrowser browser = new JishoBrowser();
		final Map<String, WordDefinition> words = new HashMap<>();

		for (final String tag : TAGS)
		{
			for (final WordDefinition word : browser.getWordsForTag(tag))
			{
				final WordDefinition previousWord = words.get(word.getWord());

				if (previousWord == null)
					words.put(word.getWord(), word);
				else
					previousWord.mergeWith(word);
			}
		}

		try (final BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/words.tsv")))
		{
			for (final WordDefinition word : words.values().stream().sorted(WordDefinition.COMPARATOR).collect(Collectors.toList()))
				writer.write(word.getWord() + '\t' + word.getWanikaniLevel() + '\t' + word.getJlptLevel() + '\n');
		}
	}
}
