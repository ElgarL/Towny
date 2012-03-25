package com.palmergames.bukkit.towny.question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Poll {
	// The polled question
	protected Question question;
	
	// A mapping of the voters with their votes. Those who havn't voted yet have null votes.
	protected HashMap<String,Option> voters = new HashMap<String,Option>();
	
	// A simple holder for the current calculated votes.
	protected Map<Option,Integer> votes = new HashMap<Option,Integer>();
	
	// When the poll will end and the results will be acted upon.
	// A value of -1 will have a never ending poll.
	protected long endDate = -1;
	
	// Will this poll be deleted when session ends?
	protected boolean persistance = false;
	
	public Poll (List<String> voters, Question question, long endDate, boolean persistance) {
		this(voters, question, endDate);
		this.persistance = persistance;
		
	}
	
	public Poll (List<String> voters, Question question, long endDate) {
		this(voters, question);
		this.endDate = endDate;
	}
	
	public Poll (List<String> voters, Question question) {
		for (String voter : voters)
			addVoter(voter);
	}
	
	public void addVoter(String voter) {
		voters.put(voter, null);
	}
	
	
	public void voteFor(String voter, Option vote) {
		voters.put(voter, vote);
		if (votes.containsKey(vote)) {
			votes.put(vote, votes.get(vote) + 1);
		} else {
			votes.put(vote, 1);
		}
	}
	
	public void checkEnd() {
		for (String voter : voters.keySet()) 
			if (voters.get(voter) == null)
				return;
		
		end();
	}
	
	public HashMap<String, Option> getVoters() {
		return voters;
	}
	
	public Set<String> getVoterNames() {
		return voters.keySet();
	}
	
	public boolean isPersistant() {
		return persistance;
	}
	
	public Map<Option,Integer> getVotes() {
		return votes;
	}
	
	public abstract void end();
}
