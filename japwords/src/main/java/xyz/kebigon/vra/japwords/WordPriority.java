package xyz.kebigon.vra.japwords;

import java.util.function.Predicate;

import xyz.kebigon.vra.japwords.jisho.WordDefinition;

public enum WordPriority
{
	/**
	 * JLPT level <= N2, Kanji level <= N2/High-school, Wanikani word
	 */
	PRIORITY_1("p1", w -> w.getJlptLevel() >= 2 && (w.getKanjiJlpt() >= 2 || w.getKanjiGrade() < 8) && w.hasWanikaniLevel()),

	/**
	 * JLPT level <= N2, Kanji level <= N2/High-school
	 */
	PRIORITY_2("p2", w -> w.getJlptLevel() >= 2 && (w.getKanjiJlpt() >= 2 || w.getKanjiGrade() < 8)),

	/**
	 * JLPT level <= N2
	 */
	PRIORITY_3("p3", w -> w.getJlptLevel() >= 2),

	/**
	 * JLPT level <= N1, Kanji level <= N1/High-school, Wanikani word
	 */
	PRIORITY_4("p4", w -> w.getJlptLevel() >= 1 && (w.getKanjiJlpt() >= 1 || w.getKanjiGrade() < 8) && w.hasWanikaniLevel()),

	/**
	 * JLPT level <= N1, Kanji level <= N1/High-school
	 */
	PRIORITY_5("p5", w -> w.getJlptLevel() >= 1 && (w.getKanjiJlpt() >= 1 || w.getKanjiGrade() < 8)),

	/**
	 * JLPT level <= N1
	 */
	PRIORITY_6("p6", w -> w.getJlptLevel() >= 1),

	/**
	 * Kanji level <= High-school, Wanikani word
	 */
	PRIORITY_7("p7", w -> w.getKanjiGrade() < 8 && w.hasWanikaniLevel()),

	/**
	 * Kanji level <= High-school, Wanikani word
	 */
	PRIORITY_8("p8", w -> w.getKanjiGrade() < 8),

	/**
	 * Everything else
	 */
	PRIORITY_9("p9", w -> true);

	private final String fileName;
	private final Predicate<WordDefinition> filter;

	private WordPriority(final String fileName, final Predicate<WordDefinition> filter)
	{
		this.fileName = fileName;
		this.filter = filter;
	}

	public String getFileName()
	{
		return fileName;
	}

	public static WordPriority getWordPriority(final WordDefinition word)
	{
		for (final WordPriority priority : values())
			if (priority.filter.test(word))
				return priority;
		return PRIORITY_9;
	}
}
