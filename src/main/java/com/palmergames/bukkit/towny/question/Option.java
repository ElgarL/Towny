package com.palmergames.bukkit.towny.question;

public class Option {
	// The command used to choose this option.
	protected String command;
	
	// What is displayed when asked the question.
	// A null value will instead display what is in the variable command.
	protected String optionDescription;
	
	// The reaction caused when the player chooses this option
	protected Runnable reaction;
	
	public Option (String command, Runnable reaction) {
		this.command = command;
		this.reaction = reaction;
		if (reaction instanceof OptionTask)
			((OptionTask) reaction).setOption(this);
	}
	
	public Option (String command, Runnable reaction, String optionDescription) {
		this(command, reaction);
		this.optionDescription = optionDescription;
	}
	
	public String getOptionDescription() {
		return optionDescription;
	}
	
	public String getOptionString() {
		if (hasDescription())
			return optionDescription;
		else
			return command;
	}

	public boolean isCommand(String command) {
		return this.command.toLowerCase().equals(command.toLowerCase());
	}
	
	public Runnable getReaction() {
		return reaction;
	}
	
	@Override
	public String toString() {
		return command;
	}

	public boolean hasDescription() {
		return optionDescription != null;
	}
}
