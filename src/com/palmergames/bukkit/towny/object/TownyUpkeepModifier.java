package com.palmergames.bukkit.towny.object;

import java.util.HashMap;
import java.util.Map;

public class TownyUpkeepModifier {
	
	private static Map<Town,Double> town_Upkeep_Modifiers = new HashMap<Town,Double>();
	private static Map<Nation, Double> nation_Upkeep_Modifiers = new HashMap<Nation,Double>();
	
	
	public static double getTownUpkeepModifier(Town town){
		return (town_Upkeep_Modifiers.containsKey(town) ? town_Upkeep_Modifiers.get(town) : 0 );
	}
	
	public static double getNationUpkeepModifier(Nation nation){
		return (nation_Upkeep_Modifiers.containsKey(nation) ? nation_Upkeep_Modifiers.get(nation) : 0 );
	}
	
	public static void setTownUpkeepModifier(Town town, Double mod){
		town_Upkeep_Modifiers.put(town,mod);
	}
	
	public static void setNationUpkeepModifier(Nation nation, Double mod){
		nation_Upkeep_Modifiers.put(nation, mod);
	}
	
	public static Map<Town,Double> getTown_Upkeep_Modifiers() {
		return town_Upkeep_Modifiers;
	}

	public static void setTown_Upkeep_Modifiers(Map<Town,Double> town_Upkeep_Modifiers) {
		TownyUpkeepModifier.town_Upkeep_Modifiers = town_Upkeep_Modifiers;
	}

	public static Map<Nation, Double> getNation_Upkeep_Modifiers() {
		return nation_Upkeep_Modifiers;
	}

	public static void setNation_Upkeep_Modifiers(
			Map<Nation, Double> nation_Upkeep_Modifiers) {
		TownyUpkeepModifier.nation_Upkeep_Modifiers = nation_Upkeep_Modifiers;
	}

}
