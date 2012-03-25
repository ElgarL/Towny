package com.palmergames.bukkit.towny.question;

import java.util.ArrayList;
import java.util.List;

public class QuestionFormatter {
	private static String questionFormat = "%s";
	private static String optionFormat = "%6d) %s";
	
	public static List<String> format(AbstractQuestion question) {
		List<String> out = new ArrayList<String>();
		out.add(String.format(questionFormat, question.question));
		int n = 1;
		for (Option option : question.options) {
			out.add(String.format(optionFormat, n, option.toString()));
			n += 1;
		}
		return out;
	}
}
