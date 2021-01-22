package xyz.kebigon.vra.japwords.jisho;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class JishoBrowser
{
	private final WebDriver driver = new HtmlUnitDriver();

	public Collection<WordDefinition> getWordsForTag(String tag)
	{
		final Collection<WordDefinition> tagWords = new HashSet<>();

		int roundWithoutNewWords = 0;
		final int expectedSize = getWordCountForTag(tag);

		while (tagWords.size() < expectedSize && roundWithoutNewWords < 10)
		{
			final Collection<WordDefinition> roundWords = getWordsForTagRound(tag);
			if (tagWords.containsAll(roundWords))
				roundWithoutNewWords++;
			else
			{
				tagWords.addAll(roundWords);
				roundWithoutNewWords = 0;
			}
		}

		return tagWords;
	}

	private int getWordCountForTag(String tag)
	{
		driver.navigate().to("https://jisho.org/search/%23" + tag + " %23words");

		// Format: "— 658 found"
		final String resultCountStr = driver.findElement(By.className("result_count")).getText();
		if (!resultCountStr.startsWith("— ") || !resultCountStr.endsWith(" found"))
			throw new RuntimeException("Invalid resultCountStr: " + resultCountStr);
		return Integer.parseInt(resultCountStr.substring(2, resultCountStr.length() - 6));
	}

	private Collection<WordDefinition> getWordsForTagRound(String tag)
	{
		final Collection<WordDefinition> result = new HashSet<>();
		driver.navigate().to("https://jisho.org/search/%23" + tag + " %23words");

		while (true)
		{
			driver.findElements(By.className("concept_light")).stream() //
					.map(JishoBrowser::createWord) //
					.forEach(result::add);

			try
			{
				driver.findElement(By.className("more")).click();
			}
			catch (final NoSuchElementException e)
			{
				break;
			}
		}

		System.out.println("[" + tag + "] " + result.size() + " words found");
		return result;
	}

	private static final String JLPT_TAG_PREFIX = "JLPT N";
	private static final int JLPT_TAG_PREFIX_LENGTH = JLPT_TAG_PREFIX.length();
	private static final String WANIKANI_TAG_PREFIX = "Wanikani level ";
	private static final int WANIKANI_TAG_PREFIX_LENGTH = WANIKANI_TAG_PREFIX.length();

	private static WordDefinition createWord(WebElement element)
	{
		final String word = element.findElement(By.cssSelector(".concept_light-representation > .text")).getText();
		final WordDefinition wordDefinition = new WordDefinition(word);

		final Collection<String> tags = element.findElements(By.className("concept_light-tag")).stream() //
				.map(e -> e.getText()) //
				.collect(Collectors.toSet());

		tags.stream() //
				.filter(tag -> tag.startsWith(JLPT_TAG_PREFIX)) //
				.map(s -> Integer.parseInt(s.substring(JLPT_TAG_PREFIX_LENGTH))) //
				.reduce((a, b) -> a > b ? a : b) //
				.ifPresentOrElse(jlptLevel -> wordDefinition.setJlptLevel(jlptLevel), () -> wordDefinition.setJlptLevel(0));

		tags.stream() //
				.filter(tag -> tag.startsWith(WANIKANI_TAG_PREFIX)) //
				.map(s -> Integer.parseInt(s.substring(WANIKANI_TAG_PREFIX_LENGTH))) //
				.reduce((a, b) -> a < b ? a : b) //
				.ifPresent(wanikaniLevel -> wordDefinition.setWanikaniLevel(wanikaniLevel));

		return wordDefinition;
	}
}
