package com.palmergames.bukkit.towny.question;

public class PollQuestion extends Question {
	protected Poll poll;
	
	public PollQuestion(Poll poll, String voter, Question question, boolean persistance) {
		super(voter, question.question, question.options, persistance);
		this.poll = poll;
		for (Option option : options)
			if (option.reaction instanceof PollTask)
				((PollTask) option.reaction).setPoll(poll);
	}
	
	public PollQuestion(Poll poll, String voter, Question question) {
		this(poll, voter, question, false);
	}
}
