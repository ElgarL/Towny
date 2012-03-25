package com.palmergames.bukkit.towny.question;

public class PollTask extends QuestionTask {
	protected Poll poll;
	
	@Override
	public void run() {
		poll.voteFor(((Question)getQuestion()).getTarget(), getOption());
		poll.checkEnd();
	}

	public Poll getPoll() {
		return poll;
	}

	public void setPoll(Poll poll) {
		this.poll = poll;
	}

}
