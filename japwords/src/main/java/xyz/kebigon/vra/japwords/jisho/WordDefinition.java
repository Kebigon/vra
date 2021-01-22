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

	/* package */ void mergeWith(WordDefinition o)
	{
		if (wanikaniLevel == null || (o.wanikaniLevel != null && wanikaniLevel > o.wanikaniLevel))
			wanikaniLevel = o.wanikaniLevel;
		if (jlptLevel == null || (o.jlptLevel != null && jlptLevel < o.jlptLevel))
			jlptLevel = o.jlptLevel;
	}

	/*
	 * Comparators
	 */

	public static final Comparator<WordDefinition> COMPARATOR = new Comparator<>()
	{
		@Override
		public int compare(WordDefinition a, WordDefinition b)
		{
			return a.word.compareTo(b.word);
		}
	};

	public static final Comparator<WordDefinition> DIFFICULTY_COMPARATOR = new Comparator<>()
	{
		@Override
		public int compare(WordDefinition a, WordDefinition b)
		{
			if (a.getWanikaniLevel() != b.getWanikaniLevel())
			{
				if (a.getWanikaniLevel() == null)
					return 1;
				if (b.getWanikaniLevel() == null)
					return -1;
				return Integer.compare(a.getWanikaniLevel(), b.getWanikaniLevel());
			}

			if (a.getJlptLevel() != b.getJlptLevel())
				return -Integer.compare(a.getJlptLevel(), b.getJlptLevel());

			final int aKanjiJlpt = a.getKanjis().stream().map(k -> k.getJlpt_new()).reduce(Math::min).orElse(5);
			final int bKanjiJlpt = b.getKanjis().stream().map(k -> k.getJlpt_new()).reduce(Math::min).orElse(5);
			if (aKanjiJlpt != bKanjiJlpt)
				return -Integer.compare(aKanjiJlpt, bKanjiJlpt);

			final int aKanjiGrade = a.getKanjis().stream().map(k -> k.getGrade()).reduce(Math::max).orElse(1);
			final int bKanjiGrade = b.getKanjis().stream().map(k -> k.getGrade()).reduce(Math::max).orElse(1);
			if (aKanjiGrade != bKanjiGrade)
				return Integer.compare(aKanjiGrade, bKanjiGrade);

			return 0;
		}
	};
}
