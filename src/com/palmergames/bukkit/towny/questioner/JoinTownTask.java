package com.palmergames.bukkit.towny.questioner;

import static com.palmergames.bukkit.towny.object.TownyObservableType.TOWN_ADD_RESIDENT;

import org.bukkit.Bukkit;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.ResidentJoinEvent;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import com.palmergames.bukkit.util.ChatTools;

public class JoinTownTask extends ResidentTownQuestionTask {

	public JoinTownTask(Resident resident, Town town) {

		super(resident, town);
	}

	@Override
	public void run() {

		try {
			ResidentJoinEvent Residentjoin = new ResidentJoinEvent(resident, town);
			Bukkit.getServer().getPluginManager().callEvent(Residentjoin);
			town.addResident(resident);
			towny.deleteCache(resident.getName());
			TownyUniverse.getDataSource().saveResident(resident);
			TownyUniverse.getDataSource().saveTown(town);

			TownyMessaging.sendTownMessage(town, ChatTools.color(String.format(TownySettings.getLangString("msg_join_town"), resident.getName())));
			
			universe.setChangedNotify(TOWN_ADD_RESIDENT);
			
		} catch (AlreadyRegisteredException e) {
			try {
				TownyMessaging.sendResidentMessage(resident, e.getMessage());
			} catch (TownyException e1) {
			}
		}
	}
}
