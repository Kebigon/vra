package xyz.kebigon.vra.japwords.jisho;

import java.util.Collection;
import java.util.Comparator;

import lombok.Data;
import xyz.kebigon.vra.japwords.kanji.Kanji;

@Data
public class WordDefinition
{
	private final String word;

	private Integer wanikaniLevel;
	private Integer jlptLevel;

	private Collection<Kanji> kanjis;

	/* package */ void mergeWith(final WordDefinition o)
	{
		if (wanikaniLevel == null || (o.wanikaniLevel != null && wanikaniLevel > o.wanikaniLevel))
			wanikaniLevel = o.wanikaniLevel;
		if (jlptLevel == null || (o.jlptLevel != null && jlptLevel < o.jlptLevel))
			jlptLevel = o.jlptLevel;
	}

	/*
	 * Utils
	 */

	public boolean hasWanikaniLevel()
	{
		return wanikaniLevel != null;
	}

	public int getKanjiJlpt()
	{
		return kanjis.stream() //
				.map(k -> k.getJlpt_new()) //
				.reduce(5, Math::min);
	}

	public int getKanjiGrade()
	{
		return kanjis.stream() //
				.map(k -> k.getGrade()) //
				.reduce(1, Math::max);
	}

	/*
	 * Comparators
	 */

	public static final Comparator<WordDefinition> COMPARATOR = new Comparator<>()
	{
		@Override
		public int compare(final WordDefinition a, final WordDefinition b)
		{
			return a.word.compareTo(b.word);
		}
	};

	public static final Comparator<WordDefinition> DIFFICULTY_COMPARATOR = new Comparator<>()
	{
		@Override
		public int compare(final WordDefinition a, final WordDefinition b)
		{
			// Prioritize N2+ words
			if (a.getJlptLevel() < 2 && b.getJlptLevel() >= 2)
				return 1;
			if (b.getJlptLevel() < 2 && a.getJlptLevel() >= 2)
				return -1;

			if (a.getWanikaniLevel() != b.getWanikaniLevel())
			{
				if (!a.hasWanikaniLevel())
					return 1;
				if (!b.hasWanikaniLevel())
					return -1;
				return Integer.compare(a.getWanikaniLevel(), b.getWanikaniLevel());
			}

			if (a.getJlptLevel() != b.getJlptLevel())
				return -Integer.compare(a.getJlptLevel(), b.getJlptLevel());

			final int aKanjiJlpt = a.getKanjiJlpt();
			final int bKanjiJlpt = b.getKanjiJlpt();
			if (aKanjiJlpt != bKanjiJlpt)
				return -Integer.compare(aKanjiJlpt, bKanjiJlpt);

			final int aKanjiGrade = a.getKanjiGrade();
			final int bKanjiGrade = b.getKanjiGrade();
			if (aKanjiGrade != bKanjiGrade)
				return Integer.compare(aKanjiGrade, bKanjiGrade);

			return 0;
		}
	};
}
