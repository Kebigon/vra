package xyz.kebigon.vra.japwords.anki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import net.razorvine.pickle.Unpickler;

public class MorphmanDatabase
{
	private final Unpickler unpickler = new Unpickler();

	public Collection<String> load(File database) throws FileNotFoundException, IOException
	{
		try (final FileInputStream fis = new FileInputStream(database))
		{
			try (final GZIPInputStream is = new GZIPInputStream(fis))
			{
				@SuppressWarnings("unchecked")
				final Map<Map<String, String>, Object> morphemes = (Map<Map<String, String>, Object>) unpickler.load(is);

				// Example morpheme infos for the expression "歩いて":
				// norm: 歩く [normalized base form]
				// base: 歩く
				// inflected: 歩い [mecab cuts off all endings, so there is not て]

				final Set<String> words = new HashSet<>();
				morphemes.keySet().stream().map(morpheme -> morpheme.get("norm")).forEach(words::add);
				morphemes.keySet().stream().map(morpheme -> morpheme.get("base")).forEach(words::add);
				morphemes.keySet().stream().map(morpheme -> morpheme.get("inflected")).forEach(words::add);
				return words;
			}
		}
	}

}
