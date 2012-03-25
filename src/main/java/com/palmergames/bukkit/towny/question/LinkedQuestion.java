package com.palmergames.bukkit.towny.question;

import java.util.ArrayList;
import java.util.List;

/**
 * A linked question is used for when you need any one target to answer the question, and clear it from the other targets list.
 * 
 * @author Chris H (Shade)
 */
public class LinkedQuestion extends AbstractQuestion {
	// The names of those who are asked the question.
	protected List<String> targets;
	
	/**
	 * Constructor including the option to set persistence.
	 * 
	 * @param targets
	 * @param question
	 * @param options
	 * @param persistance
	 */
	public LinkedQuestion (int id, List<String> targets, String question, List<Option> options, boolean persistance) {
		this(id, targets, question, options);
		this.persistance = persistance;
	}
	
	/**
	 * Constructor, assuming the question isn't persistent.
	 * 
	 * @param targets
	 * @param question
	 * @param options
	 */
	public LinkedQuestion (int id, List<String> targets, String question, List<Option> options) {
		this.id = id;
		this.targets = targets;
		this.question = question;
		this.options = new ArrayList<Option>(options);
		for (Option option : options)
			if (option.reaction instanceof QuestionTask)
				((QuestionTask) option.reaction).setQuestion(this);
	}
	
	/**
	 * Make of copy of this question, but with a new targets.
	 * 
	 * @param targets
	 * @return
	 */
	public LinkedQuestion newInstance(List<String> targets) {
		return new LinkedQuestion(QuestionManager.getNextQuestionId(), targets, question, options, persistance);
	}
	
	public List<String> getTargets() {
		return targets;
	}
}
