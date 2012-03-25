package com.palmergames.bukkit.towny.question;

import java.util.List;

public abstract class AbstractQuestion {
	// The number representing this question.
	protected int id;
	
	// What the question is.
	protected String question;
	
	// What options does the target have.
	protected List<Option> options;
	
	// Will this question be deleted when session ends?
	protected boolean persistance = false;
	
	/**
	 * Attempt to get the option specified by the command.
	 * 
	 * @param command
	 * @return the option attached to the command if it exists.
	 * @throws InvalidOptionException is called if the option doesn't exist.
	 */
	public Option getOption(String command) throws InvalidOptionException {
		for (Option option : options)
			if (option.command.toLowerCase().equals(command.toLowerCase()))
				return option;
		throw new InvalidOptionException();
	}
	
	public String getQuestion() {
		return question;
	}
	
	public boolean hasCommand(String command) {
		for (Option option : options)
			if (option.isCommand(command))
				return true;
		return false;
	}

	public List<Option> getOptions() {
		return options;
	}
}
