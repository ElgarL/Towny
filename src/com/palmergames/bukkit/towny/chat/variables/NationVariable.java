package com.palmergames.bukkit.towny.chat.variables;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.tnemc.tnc.core.common.chat.ChatVariable;
import org.bukkit.entity.Player;

/**
 * @author creatorfromhell
 */
public class NationVariable extends ChatVariable {
	@Override
	public String name() {
		return "$nation";
	}

	@Override
	public String parse(Player player, String message) {
		try {
			return TownyUniverse.getDataSource().getResident(player.getName()).getTown().getNation().getName();
		} catch(NotRegisteredException ignore) {
			return "";
		}
	}
}