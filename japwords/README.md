# Japanese words list generator

A small java program that helps me determine the words I should learn based on my current knowledge of Japanese.

It takes the following inputs:
* The words.tsv file, a word list with Wanikani/JLPT levels extracted from jisho.org by running `WordsFileGenerator.main`
* The [kanji.json](https://github.com/davidluzgouveia/kanji-data) file, released under the MIT License, Copyright (c) 2019 David Gouveia
* The Morphman database
* The Anki database

Basicaly the program will:
* read words from words.tsv
* enrich each word with the list of its kanjis from kanji.json
* remove already known words via Morphman's database
* remove words without an existing i+1 sentence via Anki's database
* keep only the words that fulfill certain conditions of word/kanji level (see `JapaneseWords.toLearn(WordDefinition)`)
* sort the result (see `WordDefinition.DIFFICULTY_COMPARATOR`)
* write the list to the standard output
