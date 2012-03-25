package com.palmergames.bukkit.towny.question;

public abstract class QuestionTask extends OptionTask {
	protected AbstractQuestion question;
	
	public AbstractQuestion getQuestion() {
		return question;
	}

	void setQuestion(AbstractQuestion question) {
		this.question = question;
	}
	
	public abstract void run();
}
