package xyz.kebigon.vra.japwords.kanji;

import lombok.Data;

@Data
public class Kanji
{
	private Integer strokes;
	private Integer grade;
	private Integer freq;
	private Integer jlpt_old;
	private Integer jlpt_new;
	private String[] meanings;
	private String[] readings_on;
	private String[] readings_kun;
	private Integer wk_level;
	private String[] wk_meanings;
	private String[] wk_readings_on;
	private String[] wk_readings_kun;
	private String[] wk_radicals;

	public int getJlpt_new()
	{
		return jlpt_new != null ? jlpt_new : 0;
	}

	public int getGrade()
	{
		return grade != null ? grade : 11;
	}
}
