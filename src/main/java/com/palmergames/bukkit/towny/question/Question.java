package com.palmergames.bukkit.towny.question;

import java.util.ArrayList;
import java.util.List;

public class Question extends AbstractQuestion {
	// The name of who is asked the question.
	protected String target;
	
	/**
	 * Constructor including the option to set persistence.
	 * 
	 * @param target
	 * @param question
	 * @param options
	 * @param persistance
	 */
	public Question (String target, String question, List<Option> options, boolean persistance) {
		this(target, question, options);
		this.persistance = persistance;
	}
	
	/**
	 * Constructor, assuming the question isn't persistent.
	 * 
	 * @param target
	 * @param question
	 * @param options
	 */
	public Question (String target, String question, List<Option> options) {
		this.id = QuestionManager.getNextQuestionId();
		this.target = target;
		this.question = question;
		this.options = new ArrayList<Option>(options);
		for (Option option : options)
			if (option.reaction instanceof QuestionTask)
				((QuestionTask) option.reaction).setQuestion(this);
	}
	
	/**
	 * Make of copy of this question, but with a new target.
	 * 
	 * @param target
	 * @return
	 */
	public Question newInstance(String target) {
		return new Question(target, question, options, persistance);
	}
	
	public String getTarget() {
		return target;
	}
}
