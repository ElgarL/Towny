package com.palmergames.bukkit.config;

public enum ConfigNodes {
	VERSION_HEADER("version", "", ""),
	VERSION(
			"version.version",
			"",
			"# This is the current version of Towny.  Please do not edit."),
	LAST_RUN_VERSION(
			"version.last_run_version",
			"",
			"# This is for showing the changelog on updates.  Please do not edit."),
	LANGUAGE(
			"language",
			"english.yml",
			"# The language file you wish to use"),
	PERMS(
			"permissions",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                   Permission nodes                   | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			"",
			"#  Possible permission nodes",
			"#",
			"#    for a full list of permission nodes visit: ",
			"#    http://palmergames.com/towny/towny-permission-nodes/ "),
	LEVELS(
			"levels",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                Town and Nation levels                | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	LEVELS_TOWN_LEVEL("levels.town_level", ""),
	LEVELS_NATION_LEVEL("levels.nation_level", ""),
	TOWN(
			"town",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |               Town Claim/new defaults                | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	TOWN_DEF_PUBLIC(
			"town.default_public",
			"true",
			"# Default public status of the town (used for /town spawn)"),
	TOWN_DEF_OPEN(
			"town.default_open",
			"false",
			"# Default Open status of the town (are new towns open and joinable by anyone at creation?)"),
	TOWN_DEF_TAXES(
			"town.default_taxes", "", "# Default tax settings for new towns."),
	TOWN_DEF_TAXES_TAX(
			"town.default_taxes.tax",
			"0.0",
			"# Default amount of tax of a new town. This must be lower than the economy.daily_taxes.max_tax_percent setting."),
	TOWN_DEF_TAXES_SHOP_TAX(
			"town.default_taxes.shop_tax",
			"0.0",
			"# Default amount of shop tax of a new town."),
	TOWN_DEF_TAXES_EMBASSY_TAX(
			"town.default_taxes.embassy_tax",
			"0.0",
			"# Default amount of embassy tax of a new town."),
	TOWN_DEF_TAXES_PLOT_TAX(
			"town.default_taxes.plot_tax",
			"0.0",
			"# Default amount for town's plottax costs."),
	TOWN_DEF_TAXES_TAXPERCENTAGE(
			"town.default_taxes.taxpercentage",
			"false",
			"# Default status of new town's taxpercentage. True means that the default_tax is treated as a percentage instead of a fixed amount."),
	TOWN_DEF_TAXES_MINIMUMTAX(
			"town.default_taxes.minimumtax",
			"0.0",
			"# A required minimum tax amount for the default_tax, will not change any towns which already have a tax set.",
			"# Do not forget to set the default_tax to more than 0 or new towns will still begin with a tax of zero."),
	TOWN_MAX_PURCHASED_BLOCKS(
			"town.max_purchased_blocks",
			"0",
			"# Limits the maximum amount of bonus blocks a town can buy."),
	TOWN_MAX_PLOTS_PER_RESIDENT(
			"town.max_plots_per_resident",
			"100",
			"# maximum number of plots any single resident can own"),
	TOWN_MAX_CLAIM_RADIUS_VALUE(
			"town.max_claim_radius_value",
			"4",
			"# maximum number used in /town claim/unclaim # commands.",
			"# set to 0 to disable limiting of claim radius value check.",
			"# keep in mind that the default value of 4 is a radius, ",
			"# and it will allow claiming 9x9 (80 plots) at once."),
	TOWN_LIMIT(
			"town.town_limit",
			"3000",
			"# Maximum number of towns allowed on the server."),
	TOWN_MIN_PLOT_DISTANCE_FROM_TOWN_PLOT(
			"town.min_plot_distance_from_town_plot",
			"5",
			"",
			"# Minimum number of plots any towns plot must be from the next town's own plots.",
			"# This will prevent town encasement to a certain degree."),
	TOWN_MIN_DISTANCE_FROM_TOWN_HOMEBLOCK(
			"town.min_distance_from_town_homeblock",
			"5",
			"",
			"# Minimum number of plots any towns home plot must be from the next town.",
			"# This will prevent someone founding a town right on your doorstep"),
    TOWN_MIN_DISTANCE_FOR_OUTPOST_FROM_PLOT(
    		"town.min_distance_for_outpost_from_plot",
    		"5",
    		"",
    		"# Minimum number of plots an outpost must be from any other town's plots.",
    		"# Useful when min_plot_distance_from_town_plot is set to near-zero to allow towns to have claims",
    		"# near to each other, but want to keep outposts away from towns."),
	TOWN_MAX_DISTANCE_BETWEEN_HOMEBLOCKS(
			"town.max_distance_between_homeblocks",
			"0",
			"",
			"# Maximum distance between homeblocks.",
			"# This will force players to build close together."),
	TOWN_TOWN_BLOCK_RATIO(
			"town.town_block_ratio",
			"8",
			"",
			"# The maximum townblocks available to a town is (numResidents * ratio).",
			"# Setting this value to 0 will instead use the level based jump values determined in the town level config."),
	TOWN_TOWN_BLOCK_SIZE(
			"town.town_block_size",
			"16",
			"# The size of the square grid cell. Changing this value is suggested only when you first install Towny.",
			"# Doing so after entering data will shift things unwantedly. Using smaller value will allow higher precision,",
			"# at the cost of more work setting up. Also, extremely small values will render the caching done useless.",
			"# Each cell is (town_block_size * town_block_size * 128) in size, with 128 being from bedrock to clouds."),
	NWS(
			"new_world_settings",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |             Default new world settings               | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			"",
			"  # These flags are only used at the initial setup of a new world.",
			"",
			"  # Once Towny is running each world can be altered from within game",
			"  # using '/townyworld toggle'",
			""),

	NWS_WORLD_USING_TOWNY("new_world_settings.using_towny", "true",
			"# Default for new worlds to have towny enabled."),

	NWS_WORLD_PVP_HEADER("new_world_settings.pvp", "", ""),
	NWS_WORLD_PVP(
			"new_world_settings.pvp.world_pvp",
			"true",
			"# Set if PVP is enabled in this world"),
	NWS_FORCE_PVP_ON(
			"new_world_settings.pvp.force_pvp_on",
			"false",
			"# force_pvp_on is a global flag and overrides any towns flag setting"),

	NWS_WORLD_MONSTERS_HEADER("new_world_settings.mobs", "", ""),
	NWS_WORLD_MONSTERS_ON(
			"new_world_settings.mobs.world_monsters_on",
			"true",
			"# world_monsters_on is a global flag setting per world."),
	NWS_FORCE_TOWN_MONSTERS_ON(
			"new_world_settings.mobs.force_town_monsters_on",
			"false",
			"# force_town_monsters_on is a global flag and overrides any towns flag setting"),

	NWS_WORLD_EXPLOSION_HEADER("new_world_settings.explosions", "", ""),
	NWS_WORLD_EXPLOSION(
			"new_world_settings.explosions.world_explosions_enabled",
			"true",
			"# Allow explosions in this world"),
	NWS_FORCE_EXPLOSIONS_ON(
			"new_world_settings.explosions.force_explosions_on",
			"false",
			"# force_explosions_on is a global flag and overrides any towns flag setting"),

	NWS_WORLD_FIRE_HEADER("new_world_settings.fire", "", ""),
	NWS_WORLD_FIRE(
			"new_world_settings.fire.world_firespread_enabled",
			"true",
			"# Allow fire to be lit and spread in this world."),
	NWS_FORCE_FIRE_ON(
			"new_world_settings.fire.force_fire_on",
			"false",
			"# force_fire_on is a global flag and overrides any towns flag setting"),

	NWS_WORLD_ENDERMAN(
			"new_world_settings.enderman_protect",
			"true",
			"",
			"# Prevent Endermen from picking up and placing blocks."),

	NWS_DISABLE_PLAYER_CROP_TRAMPLING(
			"new_world_settings.disable_player_crop_trampling",
			"true",
			"# Disable players trampling crops"),
	NWS_DISABLE_CREATURE_CROP_TRAMPLING(
			"new_world_settings.disable_creature_crop_trampling",
			"true",
			"# Disable creatures trampling crops"),

	NWS_PLOT_MANAGEMENT_HEADER(
			"new_world_settings.plot_management",
			"",
			"",
			"# World management settings to deal with un/claiming plots"),

	NWS_PLOT_MANAGEMENT_DELETE_HEADER(
			"new_world_settings.plot_management.block_delete",
			"",
			""),
	NWS_PLOT_MANAGEMENT_DELETE_ENABLE(
			"new_world_settings.plot_management.block_delete.enabled",
			"true"),
	NWS_PLOT_MANAGEMENT_DELETE(
			"new_world_settings.plot_management.block_delete.unclaim_delete",
			"BED_BLOCK,TORCH,REDSTONE_WIRE,SIGN_POST,WOODEN_DOOR,WALL_SIGN,STONE_PLATE,IRON_DOOR_BLOCK,WOOD_PLATE,REDSTONE_TORCH_OFF,REDSTONE_TORCH_ON,DIODE_BLOCK_OFF,DIODE_BLOCK_ON",
			"# These items will be deleted upon a plot being unclaimed"),

	NWS_PLOT_MANAGEMENT_MAYOR_DELETE_HEADER(
			"new_world_settings.plot_management.mayor_plotblock_delete",
			"",
			""),
	NWS_PLOT_MANAGEMENT_MAYOR_DELETE_ENABLE(
			"new_world_settings.plot_management.mayor_plotblock_delete.enabled",
			"true"),
	NWS_PLOT_MANAGEMENT_MAYOR_DELETE(
			"new_world_settings.plot_management.mayor_plotblock_delete.mayor_plot_delete",
			"WALL_SIGN,SIGN_POST",
			"# These items will be deleted upon a mayor using /plot clear",
			"# To disable deleting replace the current entries with NONE."),

	NWS_PLOT_MANAGEMENT_REVERT_HEADER(
			"new_world_settings.plot_management.revert_on_unclaim",
			"",
			""),
	NWS_PLOT_MANAGEMENT_REVERT_ENABLE(
			"new_world_settings.plot_management.revert_on_unclaim.enabled",
			"true",
			"# *** WARNING***",
			"# If this is enabled any town plots which become unclaimed will",
			"# slowly be reverted to a snapshot taken before the plot was claimed.",
			"#",
			"# Regeneration will only work if the plot was",
			"# claimed under version 0.76.2, or",
			"# later with this feature enabled",
			"# Unlike the rest of this config section, the speed setting is not",
			"# set per-world. What you set for speed will be used in all worlds.",
			"#",
			"# If you allow players to break/build in the wild the snapshot will",
			"# include any changes made before the plot was claimed."),
	NWS_PLOT_MANAGEMENT_REVERT_TIME(
			"new_world_settings.plot_management.revert_on_unclaim.speed",
			"1s"),
	NWS_PLOT_MANAGEMENT_REVERT_IGNORE(
			"new_world_settings.plot_management.revert_on_unclaim.block_ignore",
			"GOLD_ORE,LAPIS_ORE,LAPIS_BLOCK,GOLD_BLOCK,IRON_BLOCK,MOSSY_COBBLESTONE,TORCH,MOB_SPAWNER,DIAMOND_ORE,DIAMOND_BLOCK,SIGN_POST,WALL_SIGN,GLOWSTONE",
			"# These block types will NOT be regenerated"),

	NWS_PLOT_MANAGEMENT_WILD_MOB_REVERT_HEADER(
			"new_world_settings.plot_management.wild_revert_on_mob_explosion",
			"",
			""),
	NWS_PLOT_MANAGEMENT_WILD_MOB_REVERT_ENABLE(
			"new_world_settings.plot_management.wild_revert_on_mob_explosion.enabled",
			"true",
			"# Enabling this will slowly regenerate holes created in the",
			"# wilderness by monsters exploding."),
	NWS_PLOT_MANAGEMENT_WILD_ENTITY_REVERT_LIST(
			"new_world_settings.plot_management.wild_revert_on_mob_explosion.entities",
			"Creeper,EnderCrystal,EnderDragon,Fireball,SmallFireball,LargeFireball,TNTPrimed,ExplosiveMinecart"),
	NWS_PLOT_MANAGEMENT_WILD_MOB_REVERT_TIME(
			"new_world_settings.plot_management.wild_revert_on_mob_explosion.delay",
			"20s"),

	GTOWN_SETTINGS(
			"global_town_settings",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                Global town settings                  | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	GTOWN_SETTINGS_FRIENDLY_FIRE(
			"global_town_settings.friendly_fire",
			"true",
			"# can residents/Allies harm other residents when in an area with pvp enabled? Other than an Arena plot."),
	GTOWN_SETTINGS_HEALTH_REGEN(
			"global_town_settings.health_regen",
			"",
			"# Players within their town or allied towns will regenerate half a heart after every health_regen_speed seconds."),
	GTOWN_SETTINGS_REGEN_SPEED("global_town_settings.health_regen.speed", "3s"),
	GTOWN_SETTINGS_REGEN_ENABLE(
			"global_town_settings.health_regen.enable",
			"true"),
	GTOWN_SETTINGS_ALLOW_OUTPOSTS(
			"global_town_settings.allow_outposts",
			"true",
			"# Allow towns to claim outposts (a townblock not connected to town)."),
	GTOWN_SETTINGS_LIMIT_OUTPOST_USING_LEVELS(
			"global_town_settings.limit_outposts_using_town_and_nation_levels",
			"false",
			"# When set to true outposts can be limited by the townOutpostLimit value of the Town Levels and",
			"# the nationBonusOutpostLimit value in the Nation Levels. In this way nations can be made to be",
			"# the only way of receiving outposts, or as an incentive to receive more outposts. Towns which are",
			"# larger can have more outposts.",
			"# When activated, this setting will not cause towns who already have higher than their limit",
			"# to lose outposts. They will not be able to start new outposts until they have unclaimed outposts",
			"# to become under their limit. Likewise, towns that join a nation and receive bonus outposts will",
			"# be over their limit if they leave the nation."),
	GTOWN_SETTINGS_OVER_OUTPOST_LIMIT_STOP_TELEPORT(
			"global_town_settings.over_outpost_limits_stops_teleports",
			"false",
			"# When limit_outposts_using_town_and_nation_levels is also true, towns which are over their outpost",
			"# limit will not be able to use their /town outpost teleports for the outpost #'s higher than their limit,",
			"# until they have dropped below their limit.",
			"# eg: If their limit is 3 then they cannot use /t outpost 4"),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN(
			"global_town_settings.allow_town_spawn",
			"true",
			"# Allow the use of /town spawn",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the town,",
			"# when there is a war or peace."),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL(
			"global_town_settings.allow_town_spawn_travel",
			"true",
			"# Allow regular residents to use /town spawn [town] (TP to other towns if they are public).",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the town,",
			"# when there is a war or peace."),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL_NATION(
			"global_town_settings.allow_town_spawn_travel_nation",
			"true",
			"# Allow regular residents to use /town spawn [town] to other towns in your nation.",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the town,",
			"# when there is a war or peace."),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL_ALLY(
			"global_town_settings.allow_town_spawn_travel_ally",
			"true",
			"# Allow regular residents to use /town spawn [town] to other towns in a nation allied with your nation.",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the town,",
			"# when there is a war or peace."),
	GTOWN_SETTINGS_IS_ALLY_SPAWNING_REQUIRING_PUBLIC_STATUS(
			"global_town_settings.is_nation_ally_spawning_requiring_public_status",
			"false",
			"# When set to true both nation and ally spawn travel will also require the target town to have their status set to public."),
	GTOWN_SETTINGS_SPAWN_TIMER(
			"global_town_settings.teleport_warmup_time",
			"0",
			"# If non zero it delays any spawn request by x seconds."),
	GTOWN_SETTINGS_TOWN_RESPAWN(
			"global_town_settings.town_respawn",
			"false",
			"# Respawn the player at his town spawn point when he/she dies"),
	GTOWN_SETTINGS_TOWN_RESPAWN_SAME_WORLD_ONLY(
			"global_town_settings.town_respawn_same_world_only",
			"false",
			"# Town respawn only happens when the player dies in the same world as the town's spawn point."),
	GTOWN_SETTINGS_PREVENT_TOWN_SPAWN_IN(
			"global_town_settings.prevent_town_spawn_in",
			"enemy",
			"# Prevent players from using /town spawn while within unclaimed areas and/or enemy/neutral towns.",
			"# Allowed options: unclaimed,enemy,neutral"),
	GTOWN_SETTINGS_SHOW_TOWN_NOTIFICATIONS(
			"global_town_settings.show_town_notifications",
			"true",
			"# Enables the [~Home] message.",
			"# If false it will make it harder for enemies to find the home block during a war"),
	GTOWN_SETTINGS_REQUIRED_NUMBER_RESIDENTS_JOIN_NATION(
			"global_town_settings.required_number_residents_join_nation",
			"0",
			"# The required number of residents in a town to join a nation",
			"# If the number is 0, towns will not require a certain amount of residents to join a nation"
	),
	GTOWN_SETTINGS_REQUIRED_NUMBER_RESIDENTS_CREATE_NATION(
			"global_town_settings.required_number_residents_create_nation",
			"0",
			"# The required number of residents in a town to create a nation",
			"# If the number is 0, towns will not require a certain amount of residents to create a nation"
	),
	GTOWN_SETTINGS_REFUND_DISBAND_LOW_RESIDENTS(
			"global_town_settings.refund_disband_low_residents",
			"true",
			"# If set to true, if a nation is disbanded due to a lack of residents, the capital will be refunded the cost of nation creation."
	),
	GTOWN_SETTINGS_NATION_REQUIRES_PROXIMITY(
			"global_town_settings.nation_requires_proximity",
			"0.0",
			"# The maximum number of townblocks a town can be away from a nation capital,",
			"# Automatically precludes towns from one world joining a nation in another world.",
			"# If the number is 0, towns will not a proximity to a nation."
	),
	GTOWN_FARM_PLOT_ALLOW_BLOCKS(
			"global_town_settings.farm_plot_allow_blocks",
			"LONG_GRASS,DOUBLE_PLANT,YELLOW_FLOWER,RED_ROSE,PUMPKIN_STEM,MELON_STEM,BEETROOT_BLOCK,CARROT,POTATO,CROPS,MELON_BLOCK,PUMPKIN,BEETROOT_BLOCK,CHORUS_PLANT,CHORUS_FLOWER",
			"# List of blocks which can be modified on farm plots, as long as player is also allowed in the plot's '/plot perm' line."
	),
	GTOWN_FARM_ANIMALS(
			"global_town_settings.farm_animals",
			"PIG,COW,CHICKEN,SHEEP,MOOSHROOM",
			"# List of animals which can be kiled on farm plots by town residents."
	),
	GTOWN_MAX_RESIDENTS_PER_TOWN(
			"global_town_settings.max_residents_per_town",
			"0",
			"# The maximum number of residents that can be joined to a town. Setting to 0 disables this feature."
	),
	GTOWN_SETTINGS_DISPLAY_TOWNBOARD_ONLOGIN(
			"global_town_settings.display_board_onlogin",
			"true",
			"# If Towny should show players the townboard when they login"
	),
	GTOWN_SETTINGS_OUTSIDERS_PREVENT_PVP_TOGGLE(
			"global_town_settings.outsiders_prevent_pvp_toggle",
			"false",
			"# If set to true, Towny will prevent a town from toggling PVP while an outsider is within the town's boundaries.",
			"# When active this feature can cause a bit of lag when the /t toggle pvp command is used, depending on how many players are online."
	),
	GTOWN_SETTINGS_HOMEBLOCKS_PREVENT_FORCEPVP(
			"global_town_settings.homeblocks_prevent_forcepvp",
			"false",
			"# If set to true, when a world has forcepvp set to true, homeblocks of towns will not be affected and have PVP set to off."),
	GTOWN_SETTINGS_MINIMUM_AMOUNT_RESIDENTS_FOR_OUTPOSTS(
			"global_town_settings.minimum_amount_of_residents_in_town_for_outpost",
			"0",
			"# The amount of residents a town needs to claim an outpost,",
			"# Setting this value to 0, means a town can claim outposts no matter how many residents"
	),
	GTOWN_SETTINGS_KEEP_INVENTORY_ON_DEATH_IN_TOWN(
			"global_town_settings.keep_inventory_on_death_in_town",
			"false",
			"# If People should keep their inventories on death in a town",
			"# Is not guaranteed to work with other keep inventory plugins!"
	),
	GTOWN_SETTINGS_KEEP_EXPERIENCE_ON_DEATH_IN_TOWN(
			"global_town_settings.keep_inventory_on_death_in_town",
			"false",
			"# If People should keep their experience on death in a town",
			"# Is not guaranteed to work with other keep experience plugins!"
	),
	GTOWN_MAX_PLOT_PRICE_COST(
			"global_town_settings.maximum_plot_price_cost",
			"1000000.0",
			"# Maximum amount that a town can set their plot, embassy, shop, etc plots' prices to.",
			"# Setting this higher can be dangerous if you use Towny in a mysql database. Large numbers can become shortened to scientific notation. "
	),	
	GNATION_SETTINGS(
			"global_nation_settings",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |              Global nation settings                  | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	GNATION_SETTINGS_NATIONZONE(
			"global_nation_settings.nationzone",
			"",
			"",
			"# Nation Zones are a special type of wilderness surrounding Capitals of Nations or Nation Capitals and their Towns.",
			"# When it is enabled players who are members of the nation can use the wilderness surrounding the town like normal.",
			"# Players who are not part of that nation will find themselves unable to break/build/switch/itemuse in this part of the wilderness.",
			"# The amount of townblocks used for the zone is determined by the size of the nation and configured in the nation levels.",
			"# Because these zones are still wilderness anyone can claim these townblocks.",
			"# It is recommended that whatever size you choose, these numbers should be less than the min_plot_distance_from_town_plot otherwise",
			"# someone might not be able to build/destroy in the wilderness outside their town."),
	GNATION_SETTINGS_NATIONZONE_ENABLE(
			"global_nation_settings.nationzone.enable",
			"false",
			"",
			"# Nation zone feature is disabled by default. This is because it can cause a higher server load for servers with a large player count."),
	GNATION_SETTINGS_NATIONZONE_ONLY_CAPITALS(
			"global_nation_settings.nationzone.only_capitals",
			"true",
			"",
			"# When set to true, only the capital town of a nation will be surrounded by a nation zone type of wilderness."),
	GNATION_SETTINGS_NATIONZONE_CAPITAL_BONUS_SIZE(
			"global_nation_settings.nationzone.capital_bonus_size",
			"0",
			"",
			"# Amount of buffer added to nation zone width surrounding capitals only. Creates a larger buffer around nation capitals."),
	GNATION_SETTINGS_NATIONZONE_WAR_DISABLES(
			"global_nation_settings.nationzone.war_disables",
			"true",
			"",
			"# When set to true, nation zones are disabled during the the Towny war types."),
	GNATION_SETTINGS_DISPLAY_NATIONBOARD_ONLOGIN(
			"global_nation_settings.display_board_onlogin",
			"true",
			"# If Towny should show players the nationboard when they login."),
	GNATION_SETTINGS_CAPITAL_SPAWN(
			"global_nation_settings.capital_spawn",
			"true",
			"# If enabled, only allow the nation spawn to be set in the capital city."),
    GNATION_SETTINGS_ALLOW_NATION_SPAWN(
			"global_nation_settings.allow_nation_spawn",
			"true",
			"# Allow the use of /nation spawn",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the nation,",
			"# when there is a war or peace."),
	GNATION_SETTINGS_ALLOW_NATION_SPAWN_TRAVEL(
			"global_nation_settings.allow_nation_spawn_travel",
			"true",
			"# Allow regular residents to use /nation spawn [nation] (TP to other nations if they are public).",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the nation,",
			"# when there is a war or peace."),
	GNATION_SETTINGS_ALLOW_NATION_SPAWN_TRAVEL_ALLY(
			"global_nation_settings.allow_nation_spawn_travel_ally",
			"true",
			"# Allow regular residents to use /nation spawn [nation] to other nations allied with your nation.",
			"# Valid values are: true, false, war, peace",
			"# When war or peace is set, it is only possible to teleport to the nations,",
			"# when there is a war or peace."),
	GNATION_SETTINGS_MAX_TOWNS_PER_NATION(
			"global_nation_settings.max_towns_per_nation",
			"0",
			"# If higher than 0, it will limit how many towns can be joined into a nation.",
			"# Does not affect existing nations that are already over the limit."),
    GNATION_DEF_PUBLIC(
            "global_nation_settings.default.public",
            "false",
            "# If set to true, any newly made nation will have their spawn set to public."),
	PLUGIN(
			"plugin",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                 Plugin interfacing                   | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	PLUGIN_DATABASE(
			"plugin.database",
			"",
			"",
			"# Valid load and save types are: flatfile, mysql, sqlite, h2."),
	PLUGIN_DATABASE_LOAD("plugin.database.database_load", "flatfile"),
	PLUGIN_DATABASE_SAVE("plugin.database.database_save", "flatfile"),

	PLUGIN_DATABASE_SQL_HEADER(
			"plugin.database.sql",
			"",
			"",
			"# SQL database connection details (IF set to use SQL)."),
	PLUGIN_DATABASE_HOSTNAME("plugin.database.sql.hostname", "localhost"),
	PLUGIN_DATABASE_PORT("plugin.database.sql.port", "3306"),
	PLUGIN_DATABASE_DBNAME("plugin.database.sql.dbname", "towny"),
	PLUGIN_DATABASE_TABLEPREFIX("plugin.database.sql.table_prefix", "towny_"),
	PLUGIN_DATABASE_USERNAME("plugin.database.sql.username", "root"),
	PLUGIN_DATABASE_PASSWORD("plugin.database.sql.password", ""),
	PLUGIN_DATABASE_SSL("plugin.database.sql.ssl", "false"),

	PLUGIN_DAILY_BACKUPS_HEADER(
			"plugin.database.daily_backups",
			"",
			"",
			"# Flatfile backup settings."),
	PLUGIN_DAILY_BACKUPS("plugin.database.daily_backups", "true"),
	PLUGIN_BACKUPS_ARE_DELETED_AFTER(
			"plugin.database.backups_are_deleted_after",
			"90d"),
	PLUGIN_FLATFILE_BACKUP(
			"plugin.database.flatfile_backup",
			"zip",
			"",
			"# Valid entries are: zip, none."),

	PLUGIN_INTERFACING("plugin.interfacing", "", ""),
	PLUGIN_MODS(
			"plugin.interfacing.tekkit", "", ""),
	PLUGIN_MODS_FAKE_RESIDENTS(
			"plugin.interfacing.tekkit.fake_residents",
			"[IndustrialCraft],[BuildCraft],[Redpower],[Forestry],[Turtle]",
			"# Add any fake players for client/server mods (aka Tekkit) here"),
	PLUGIN_USING_ESSENTIALS(
			"plugin.interfacing.using_essentials",
			"false",
			"",
			"# Enable using_essentials if you are using cooldowns in essentials for teleports."),
	PLUGIN_USING_ECONOMY(
			"plugin.interfacing.using_economy",
			"true",
			"",
			"# This enables/disables all the economy functions of Towny.",
			"# This will first attempt to use Vault to bridge your economy plugin with Towny.",
			"# If Vault is not present it will attempt to find the old iConomy 5.01 plugin.",
			"# If neither Vault or iConomy 5.01 are present it will not be possible to create towns or do any operations that require money."),
	PLUGIN_USING_PERMISSIONS(
			"plugin.interfacing.using_permissions",
			"true",
			"",
			"# True to attempt to use GroupManager, PEX, bPermissions, Permissions2/3 or BukkitPerms",
			"# False to disable permission checks and rely on Towny settings."),

	PLUGIN_DAY_HEADER("plugin.day_timer", "", ""),
	PLUGIN_DAY_INTERVAL(
			"plugin.day_timer.day_interval",
			"1d",
			"# The number of hours in each \"day\".",
			"# You can configure for 10 hour days. Default is 24 hours."),
	PLUGIN_NEWDAY_TIME(
			"plugin.day_timer.new_day_time",
			"12h",
			"# The time each \"day\", when taxes will be collected.",
			"# MUST be less than day_interval. Default is 12h (midday)."),

	PLUGIN_DEBUG_MODE(
			"plugin.debug_mode",
			"false",
			"",
			"# Lots of messages to tell you what's going on in the server with time taken for events."),
	PLUGIN_INFO_TOOL(
			"plugin.info_tool",
			"BRICK",
			"",
			"# Info tool for server admins to use to query in game blocks and entities."),
	PLUGIN_DEV_MODE(
			"plugin.dev_mode",
			"",
			"",
			"# Spams the player named in dev_name with all messages related to towny."),
	PLUGIN_DEV_MODE_ENABLE("plugin.dev_mode.enable", "false"),
	PLUGIN_DEV_MODE_DEV_NAME("plugin.dev_mode.dev_name", "ElgarL"),
	PLUGIN_LOGGING(
			"plugin.LOGGING",
			"true",
			"",
			"# Record all messages to the towny.log"),
	PLUGIN_RESET_LOG_ON_BOOT(
			"plugin.reset_log_on_boot",
			"true",
			"# If true this will cause the log to be wiped at every startup."),
	FILTERS_COLOUR_CHAT(
			"filters_colour_chat",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |               Filters colour and chat                | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	FILTERS_NPC_PREFIX(
			"filters_colour_chat.npc_prefix",
			"NPC",
			"# This is the name given to any NPC assigned mayor."),
	FILTERS_REGEX(
			"filters_colour_chat.regex",
			"",
			"# Regex fields used in validating inputs."),
	FILTERS_REGEX_NAME_FILTER_REGEX(
			"filters_colour_chat.regex.name_filter_regex",
			"[ /]"),
	FILTERS_REGEX_NAME_CHECK_REGEX(
			"filters_colour_chat.regex.name_check_regex",
			"^[a-zA-Z0-9._\\[\\]-]*$"),
	FILTERS_REGEX_STRING_CHECK_REGEX(
			"filters_colour_chat.regex.string_check_regex",
			"^[a-zA-Z0-9\\s._\\[\\]-]*$"),
	FILTERS_REGEX_NAME_REMOVE_REGEX(
			"filters_colour_chat.regex.name_remove_regex",
			"[^a-zA-Z0-9._\\[\\]-]"),

	FILTERS_MODIFY_CHAT("filters_colour_chat.modify_chat", "", ""),
	FILTERS_MAX_NAME_LGTH(
			"filters_colour_chat.modify_chat.max_name_length",
			"20",
			"# Maximum length of Town and Nation names."),
	FILTERS_MODIFY_CHAT_MAX_LGTH(
			"filters_colour_chat.modify_chat.max_title_length",
			"10",
			"# Maximum length of titles and surnames."),

	PROT(
			"protection",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |             block/item/mob protection                | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	PROT_ITEM_USE_MAT(
			"protection.item_use_ids",
			"FLINT_AND_STEEL,BUCKET,WATER_BUCKET,LAVA_BUCKET,MINECART,STORAGE_MINECART,INK_SACK,SHEARS,ENDER_PEARL,GLASS_BOTTLE,FIREBALL,ARMOR_STAND,SKULL_ITEM,BOAT_BIRCH,BOAT_ACACIA,BOAT_DARK_OAK,BOAT_JUNGLE,BOAT,BOAT_SPRUCE,END_CRYSTAL,POWERED_MINECART,COMMAND_MINECART,EXPLOSIVE_MINECART,HOPPER_MINECART,CHORUS_FRUIT",
			"",
			"# Items that can be blocked within towns via town/plot flags",
			"# 259 - flint and steel",
			"# 325 - bucket",
			"# 326 - water bucket",
			"# 327 - lava bucket",
			"# 351 - bone/bonemeal",
			"# 359 - shears",
			"# 368 - ender pearl",
			"# 374 - glass bottle",
			"# 385 - fire charge"),
	PROT_SWITCH_MAT(
			"protection.switch_ids",
			"JUKEBOX,NOTE_BLOCK,BEACON,CHEST,TRAPPED_CHEST,FURNACE,DISPENSER,HOPPER,DROPPER,LEVER,COMPARATOR,REPEATER,STONE_PRESSURE_PLATE,ACACIA_PRESSURE_PLATE,BIRCH_PRESSURE_PLATE,DARK_OAK_PRESSURE_PLATE,JUNGLE_PRESSURE_PLATE,OAK_PRESSURE_PLATE,SPRUCE_PRESSURE_PLATE,HEAVY_WEIGHTED_PRESSURE_PLATE,LIGHT_WEIGHTED_PRESSURE_PLATE,STONE_BUTTON,ACACIA_BUTTON,BIRCH_BUTTON,DARK_OAK_BUTTON,JUNGLE_BUTTON,OAK_BUTTON,SPRUCE_BUTTON,ACACIA_DOOR,BIRCH_DOOR,DARK_OAK_DOOR,JUNGLE_DOOR,OAK_DOOR,SPRUCE_DOOR,ACACIA_FENCE_GATE,BIRCH_FENCE_GATE,DARK_OAK_FENCE_GATE,OAK_FENCE_GATE,JUNGLE_FENCE_GATE,SPRUCE_FENCE_GATE,ACACIA_TRAPDOOR,BIRCH_TRAPDOOR,DARK_OAK_TRAPDOOR,JUNGLE_TRAPDOOR,OAK_TRAPDOOR,SPRUCE_TRAPDOOR,MINECART,COMMAND_BLOCK_MINECART,CHEST_MINECART,FURNACE_MINECART,HOPPER_MINECART,TNT_MINECART,SHULKER_BOX,WHITE_SHULKER_BOX,ORANGE_SHULKER_BOX,MAGENTA_SHULKER_BOX,LIGHT_BLUE_SHULKER_BOX,YELLOW_SHULKER_BOX,LIME_SHULKER_BOX,PINK_SHULKER_BOX,GRAY_SHULKER_BOX,CYAN_SHULKER_BOX,PURPLE_SHULKER_BOX,BLUE_SHULKER_BOX,BROWN_SHULKER_BOX,GREEN_SHULKER_BOX,RED_SHULKER_BOX,BLACK_SHULKER_BOX,CARROT_STICK,DAYLIGHT_DETECTOR",
			"",
			"# Items which can be blocked or enabled via town/plot flags",
			"# 25 - noteblock",
			"# 54 - chest ...etc"),
	PROT_MOB_REMOVE_TOWN(
			"protection.town_mob_removal_entities",
			"Monster,Flying,Slime,Shulker,SkeletonHorse,ZombieHorse",
			"",
			"# permitted entities https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/LivingEntity.html",
			"# Animals, Chicken, Cow, Creature, Creeper, Flying, Ghast, Giant, Monster, Pig, ",
			"# PigZombie, Sheep, Skeleton, Slime, Spider, Squid, WaterMob, Wolf, Zombie, Shulker",
			"# Husk, Stray, SkeletonHorse, ZombieHorse, Vex, Vindicator, Evoker, Endermite, PolarBear",
			"",
			"# Remove living entities within a town's boundaries, if the town has the mob removal flag set."),

	PROT_MOB_REMOVE_VILLAGER_BABIES_TOWN(
			"protection.town_prevent_villager_breeding",
			"false",
			"",
			"# Prevent the spawning of villager babies in towns."),

	PROT_MOB_DISABLE_TRIGGER_PRESSURE_PLATE_STONE(
			"protection.disable_creature_pressureplate_stone",
			"true",
			"# Disable creatures triggering stone pressure plates"),

	PROT_MOB_REMOVE_WORLD(
			"protection.world_mob_removal_entities",
			"Monster,Flying,Slime,Shulker,SkeletonHorse,ZombieHorse",
			"",
			"# Globally remove living entities in all worlds that have their flag set."),

	PROT_MOB_REMOVE_VILLAGER_BABIES_WORLD(
			"protection.world_prevent_villager_breeding",
			"false",
			"",
			"# Prevent the spawning of villager babies in the world."),

	PROT_MOB_REMOVE_SPEED(
			"protection.mob_removal_speed",
			"5s",
			"",
			"# The maximum amount of time a mob could be inside a town's boundaries before being sent to the void.",
			"# Lower values will check all entities more often at the risk of heavier burden and resource use.",
			"# NEVER set below 1."),
	PROT_MOB_TYPES(
			"protection.mob_types",
			"Animals,WaterMob,NPC,Snowman,ArmorStand",
			"",
			"# permitted entities https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/package-summary.html",
			"# Animals, Chicken, Cow, Creature, Creeper, Flying, Ghast, Giant, Monster, Pig, ",
			"# PigZombie, Sheep, Skeleton, Slime, Spider, Squid, WaterMob, Wolf, Zombie",
			"",
			"# Protect living entities within a town's boundaries from being killed by players."),
	PROT_POTION_TYPES(
			"protection.potion_types",
			"BLINDNESS,CONFUSION,HARM,HUNGER,POISON,SLOW,SLOW_DIGGING,WEAKNESS,WITHER",
			"",
			"# permitted Potion Types https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionType.html",
			"# ABSORPTION, BLINDNESS, CONFUSION, DAMAGE_RESISTANCE, FAST_DIGGING, FIRE_RESISTANCE, HARM, HEAL, HEALTH_BOOST, HUNGER, ",
			"# INCREASE_DAMAGE, INVISIBILITY, JUMP, NIGHT_VISION, POISON, REGENERATION, SATURATION, SLOW , SLOW_DIGGING, ",
			"# SPEED, WATER_BREATHING, WEAKNESS, WITHER.",
			"",
			"# When preventing PVP prevent the use of these potions."),
	PROT_CHEAT(
			"protection.cheat_protection",
			"true",
			"",
			"# Prevent fly and double block jump cheats."),
	UNCLAIMED_ZONE(
			"unclaimed",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                Wilderness settings                   | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			"",
			"  # These Settings defaults only. They are copied to each worlds data files upon first detection",
			"  # To make changes for each world edit the settings in the relevant worlds data file 'plugins/Towny/data/worlds/'",
			""),
	UNCLAIMED_ZONE_BUILD("unclaimed.unclaimed_zone_build", "false"),
	UNCLAIMED_ZONE_DESTROY("unclaimed.unclaimed_zone_destroy", "false"),
	UNCLAIMED_ZONE_ITEM_USE("unclaimed.unclaimed_zone_item_use", "false"),
	UNCLAIMED_ZONE_IGNORE(
			"unclaimed.unclaimed_zone_ignore",
			"SAPLING,GOLD_ORE,IRON_ORE,COAL_ORE,LOG,LEAVES,LAPIS_ORE,LONG_GRASS,YELLOW_FLOWER,RED_ROSE,BROWN_MUSHROOM,RED_MUSHROOM,TORCH,DIAMOND_ORE,LADDER,RAILS,REDSTONE_ORE,GLOWING_REDSTONE_ORE,CACTUS,CLAY,SUGAR_CANE_BLOCK,PUMPKIN,GLOWSTONE,LOG_2,VINE,NETHER_WARTS,COCOA"),
	UNCLAIMED_ZONE_SWITCH("unclaimed.unclaimed_zone_switch", "false"),

	NOTIFICATION(
			"notification",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                 Town Notifications                   | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			"",
			"  # This is the format for the notifications sent as players move between plots.",
			"  # Empty a particular format for it to be ignored.",
			"",
			"  # Example:",
			"  # [notification.format]",
			"  # ~ [notification.area_[wilderness/town]][notification.splitter][notification.[no_]owner][notification.splitter][notification.plot.format]",
			"  # ... [notification.plot.format]",
			"  # ... [notification.plot.homeblock][notification.plot.splitter][notification.plot.forsale][notification.plot.splitter][notification.plot.type]",
			"  # ~ Wak Town - Lord Jebus - [Home] [For Sale: 50 Beli] [Shop]",
			""),
	NOTIFICATION_FORMAT("notification.format", "&6 ~ %s"),
	NOTIFICATION_SPLITTER("notification.splitter", "&7 - "),
	NOTIFICATION_AREA_WILDERNESS("notification.area_wilderness", "&2%s"),
	NOTIFICATION_AREA_WILDERNESS_PVP("notification.area_wilderness_pvp", "%s"),
	NOTIFICATION_AREA_TOWN("notification.area_town", "&6%s"),
	NOTIFICATION_AREA_TOWN_PVP("notification.area_town_pvp", "%s"),
	NOTIFICATION_OWNER("notification.owner", "&a%s"),
	NOTIFICATION_NO_OWNER("notification.no_owner", "&a%s"),
	NOTIFICATION_PLOT("notification.plot", ""),
	NOTIFICATION_PLOT_SPLITTER("notification.plot.splitter", " "),
	NOTIFICATION_PLOT_FORMAT("notification.plot.format", "%s"),
	NOTIFICATION_PLOT_HOMEBLOCK("notification.plot.homeblock", "&b[Home]"),
	NOTIFICATION_PLOT_OUTPOSTBLOCK(
			"notification.plot.outpostblock",
			"&b[Outpost]"),
	NOTIFICATION_PLOT_FORSALE("notification.plot.forsale", "&e[For Sale: %s]"),
	NOTIFICATION_PLOT_TYPE("notification.plot.type", "&6[%s]"),
	NOTIFICATION_USING_TITLES(
			"notification.using_titles",
			"false",
			"# If set to true MC's Title and Subtitle feature will be used when crossing into a town.",
			"# Could be seen as intrusive/distracting, so false by default."),
	NOTIFICATION_TITLES(
			"notification.titles",
			"",
			"",
			"# Requires the above using_titles to be set to true.",
			"# Title and Subtitle shown when entering a town or the wilderness. By default 1st line is blank, the 2nd line shows {townname} or {wilderness}.",
			"# You may use colour codes &f, &c and so on."),	
	NOTIFICATION_TITLES_TOWN_TITLE(
			"notification.titles.town_title",
			"",
			"# Entering Town Upper Title Line"),
	NOTIFICATION_TITLES_TOWN_SUBTITLE(
			"notification.titles.town_subtitle",
			"&b{townname}",
			"# Entering Town Lower Subtitle line."),
	NOTIFICATION_TITLES_WILDERNESS_TITLE(
			"notification.titles.wilderness_title",
			"",
			"# Entering Wilderness Upper Title Line"),
	NOTIFICATION_TITLES_WILDERNESS_SUBTITLE(
			"notification.titles.wilderness_subtitle",
			"&2{wilderness}",
			"# Entering Wilderness Lower Subtitle line."),
	NOTIFICATION_OWNER_SHOWS_NATION_TITLE("notification.owner_shows_nation_title", 
			"false", 
			"# If the notification.owner option should show name or {title} name.", 
			"# Titles are the ones granted by nation kings."),
	FLAGS_DEFAULT(
			"default_perm_flags",
			"",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |             Default Town/Plot flags                  | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			"",
			""),
	FLAGS_DEFAULT_RES(
			"default_perm_flags.resident",
			"",
			"",
			"# Default permission flags for residents plots within a town",
			"#",
			"# Can allies/friends/outsiders perform certain actions in the town",
			"#",
			"# build - place blocks and other items",
			"# destroy - break blocks and other items",
			"# itemuse - use items such as furnaces (as defined in item_use_ids)",
			"# switch - trigger or activate switches (as defined in switch_ids)"),
	FLAGS_RES_FR_BUILD("default_perm_flags.resident.friend.build", "true"),
	FLAGS_RES_FR_DESTROY("default_perm_flags.resident.friend.destroy", "true"),
	FLAGS_RES_FR_ITEM_USE("default_perm_flags.resident.friend.item_use", "true"),
	FLAGS_RES_FR_SWITCH("default_perm_flags.resident.friend.switch", "true"),
	FLAGS_RES_ALLY_BUILD("default_perm_flags.resident.ally.build", "false"),
	FLAGS_RES_ALLY_DESTROY("default_perm_flags.resident.ally.destroy", "false"),
	FLAGS_RES_ALLY_ITEM_USE(
			"default_perm_flags.resident.ally.item_use",
			"false"),
	FLAGS_RES_ALLY_SWITCH("default_perm_flags.resident.ally.switch", "false"),
	FLAGS_RES_OUTSIDER_BUILD(
			"default_perm_flags.resident.outsider.build",
			"false"),
	FLAGS_RES_OUTSIDER_DESTROY(
			"default_perm_flags.resident.outsider.destroy",
			"false"),
	FLAGS_RES_OUTSIDER_ITEM_USE(
			"default_perm_flags.resident.outsider.item_use",
			"false"),
	FLAGS_RES_OUTSIDER_SWITCH(
			"default_perm_flags.resident.outsider.switch",
			"false"),
	FLAGS_DEFAULT_TOWN(
			"default_perm_flags.town",
			"",
			"",
			"# Default permission flags for towns",
			"# These are copied into the town data file at creation",
			"#",
			"# Can allies/outsiders/residents perform certain actions in the town",
			"#",
			"# build - place blocks and other items",
			"# destroy - break blocks and other items",
			"# itemuse - use items such as flint and steel or buckets (as defined in item_use_ids)",
			"# switch - trigger or activate switches (as defined in switch_ids)"),
	FLAGS_TOWN_DEF_PVP("default_perm_flags.town.default.pvp", "true"),
	FLAGS_TOWN_DEF_FIRE("default_perm_flags.town.default.fire", "false"),
	FLAGS_TOWN_DEF_EXPLOSION(
			"default_perm_flags.town.default.explosion",
			"false"),
	FLAGS_TOWN_DEF_MOBS("default_perm_flags.town.default.mobs", "false"),

	FLAGS_TOWN_RES_BUILD("default_perm_flags.town.resident.build", "true"),
	FLAGS_TOWN_RES_DESTROY("default_perm_flags.town.resident.destroy", "true"),
	FLAGS_TOWN_RES_ITEM_USE("default_perm_flags.town.resident.item_use", "true"),
	FLAGS_TOWN_RES_SWITCH("default_perm_flags.town.resident.switch", "true"),
	FLAGS_TOWN_ALLY_BUILD("default_perm_flags.town.ally.build", "false"),
	FLAGS_TOWN_ALLY_DESTROY("default_perm_flags.town.ally.destroy", "false"),
	FLAGS_TOWN_ALLY_ITEM_USE("default_perm_flags.town.ally.item_use", "false"),
	FLAGS_TOWN_ALLY_SWITCH("default_perm_flags.town.ally.switch", "false"),
	FLAGS_TOWN_OUTSIDER_BUILD("default_perm_flags.town.outsider.build", "false"),
	FLAGS_TOWN_OUTSIDER_DESTROY(
			"default_perm_flags.town.outsider.destroy",
			"false"),
	FLAGS_TOWN_OUTSIDER_ITEM_USE(
			"default_perm_flags.town.outsider.item_use",
			"false"),
	FLAGS_TOWN_OUTSIDER_SWITCH(
			"default_perm_flags.town.outsider.switch",
			"false"),
	INVITE_SYSTEM(
			"invite_system",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                 Towny Invite System                  | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	INVITE_SYSTEM_ACCEPT_COMMAND(
			"invite_system.accept_command",
			"accept",
			"# Command used to accept towny invites)",
			"#e.g Player join town invite."),
	INVITE_SYSTEM_DENY_COMMAND(
			"invite_system.deny_command",
			"deny",
			"# Command used to deny towny invites",
			"#e.g Player join town invite."),
	INVITE_SYSTEM_CONFIRM_COMMAND(
			"invite_system.confirm_command",
			"confirm",
			"# Command used to confirm some towny actions/tasks)",
			"#e.g Purging database or removing a large amount of townblocks"),
	INVITE_SYSTEM_CANCEL_COMMAND(
			"invite_system.cancel_command",
			"cancel",
			"# Command used to cancel some towny actions/tasks",
			"#e.g Purging database or removing a large amount of townblocks"),
	INVITE_SYSTEM_COOLDOWN_TIME(
			"invite_system.cooldowntime",
			"0m",
			"# When set for more than 0m, the amount of time (in minutes) which must have passed between",
			"# a player's first log in and when they can be invited to a town."),
	INVITE_SYSTEM_MAXIMUM_INVITES_SENT(
			"invite_system.maximum_invites_sent",
			"# Max invites for Town & Nations, which they can send. Invites are capped to decrease load on large servers.",
			"# You can increase these limits but it is not recommended. Invites/requests are not saved between server reloads/stops."),
	INVITE_SYSTEM_MAXIMUM_INVITES_SENT_TOWN(
			"invite_system.maximum_invites_sent.town_toplayer",
			"35",
			"# How many invites a town can send out to players, to join the town."),
	INVITE_SYSTEM_MAXIMUM_INVITES_SENT_NATION(
			"invite_system.maximum_invites_sent.nation_totown",
			"35",
			"# How many invites a nation can send out to towns, to join the nation."),
	INVITE_SYSTEM_MAXIMUM_REQUESTS_SENT_NATION(
			"invite_system.maximum_invites_sent.nation_tonation",
			"35",
			"# How many requests a nation can send out to other nations, to ally with the nation.",
			"# Only used when war.disallow_one_way_alliance is set to true."),
	INVITE_SYSTEM_MAXIMUM_INVITES_RECEIVED(
			"invite_system.maximum_invites_received",
			"# Max invites for Players, Towns & nations, which they can receive. Invites are capped to decrease load on large servers.",
			"# You can increase these limits but it is not recommended. Invites/requests are not saved between server reloads/stops."),
	INVITE_SYSTEM_MAXIMUM_INVITES_RECEIVED_PLAYER(
			"invite_system.maximum_invites_received.player",
			"10",
			"# How many invites can one player have from towns."),
	INVITE_SYSTEM_MAXIMUM_INVITES_RECEIVED_TOWN(
			"invite_system.maximum_invites_received.town",
			"10",
			"# How many invites can one town have from nations."),
	INVITE_SYSTEM_MAXIMUM_REQUESTS_RECEIVED_NATION(
			"invite_system.maximum_invites_received.nation",
			"10",
			"# How many requests can one nation have from other nations for an alliance."),
	RES_SETTING(
			"resident_settings",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                  Resident settings                   | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	RES_SETTING_INACTIVE_AFTER_TIME(
			"resident_settings.inactive_after_time",
			"1h",
			"# player is flagged as inactive after 1 hour (default)"),
	RES_SETTING_DELETE_OLD_RESIDENTS(
			"resident_settings.delete_old_residents",
			"",
			"# if enabled old residents will be kicked and deleted from a town",
			"# after Two months (default) of not logging in"),
	RES_SETTING_DELETE_OLD_RESIDENTS_ENABLE(
			"resident_settings.delete_old_residents.enable",
			"false"),
	RES_SETTING_DELETE_OLD_RESIDENTS_TIME(
			"resident_settings.delete_old_residents.deleted_after_time",
			"60d"),
	RES_SETTING_DELETE_OLD_RESIDENTS_ECO(
			"resident_settings.delete_old_residents.delete_economy_account",
			"true"),
	RES_SETTING_DEFAULT_TOWN_NAME(
			"resident_settings.default_town_name",
			"",
			"# The name of the town a resident will automatically join when he first registers."),
	RES_SETTING_DENY_BED_USE(
			"resident_settings.deny_bed_use",
			"false",
			"# If true, players can only use beds in plots they personally own."),
	RES_SETTING_IS_SHOWING_WELCOME_MESSAGE(
			"resident_settings.is_showing_welcome_message",
			"true",
			"# If true, players who join the server for the first time will cause the msg_registration message in the language files to be shown server-wide."),
	ECO(
			"economy",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                  Economy settings                    | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	ECO_USE_ASYNC(
			"economy.use_async",
			"true",
			"# By default it is set to true.",
			"# Rarely set to false. Set to false if you get concurrent modification errors on timers for daily tax collections."),
	ECO_TOWN_PREFIX(
			"economy.town_prefix",
			"town-",
			"# Prefix to apply to all town economy accounts."),
	ECO_NATION_PREFIX(
			"economy.nation_prefix",
			"nation-",
			"# Prefix to apply to all nation economy accounts."),
	ECO_TOWN_RENAME_COST(
			"economy.town_rename_cost",
			"0",
			"# The cost of renaming a town."),
	ECO_NATION_RENAME_COST(
			"economy.nation_rename_cost",
			"0",
			"# The cost of renaming a nation."),
	ECO_SPAWN_TRAVEL("economy.spawn_travel", "", ""),
	ECO_PRICE_TOWN_SPAWN_TRAVEL(
			"economy.spawn_travel.price_town_spawn_travel",
			"0.0",
			"# Cost to use /town spawn"),
	ECO_PRICE_TOWN_SPAWN_TRAVEL_NATION(
			"economy.spawn_travel.price_town_nation_spawn_travel",
			"5.0",
			"# Cost to use '/town spawn [town]' to another town in your nation."),
	ECO_PRICE_TOWN_SPAWN_TRAVEL_ALLY(
			"economy.spawn_travel.price_town_ally_spawn_travel",
			"10.0",
			"# Cost to use '/town spawn [town]' to another town in a nation that is allied with your nation."),
	ECO_PRICE_TOWN_SPAWN_TRAVEL_PUBLIC(
			"economy.spawn_travel.price_town_public_spawn_travel",
			"10.0",
			"# Cost to use /town spawn [town]",
			"# This is paid to the town you goto."),
	ECO_PRICE_TOWN_SPAWN_PAID_TO_TOWN(
			"economy.spawn_travel.town_spawn_cost_paid_to_town",
			"true",
			"# When set to true, any cost paid by a player to use any variant of '/town spawn' will be paid to the town bank.",
			"# When false the amount will be paid to the server account whose name is set in the closed economy setting below.."),
	ECO_PRICE_NATION_NEUTRALITY(
			"economy.price_nation_neutrality",
			"100.0",
			"",
			"# The daily upkeep to remain neutral during a war. Neutrality will exclude you from a war event, as well as deterring enemies."),

	ECO_NEW_EXPAND("economy.new_expand", "", ""),
	ECO_PRICE_NEW_NATION(
			"economy.new_expand.price_new_nation",
			"1000.0",
			"# How much it costs to start a nation."),
	ECO_PRICE_NEW_TOWN(
			"economy.new_expand.price_new_town",
			"250.0",
			"# How much it costs to start a town."),
	ECO_PRICE_OUTPOST(
			"economy.new_expand.price_outpost",
			"500.0",
			"# How much it costs to make an outpost. An outpost isn't limited to being on the edge of town."),
	ECO_PRICE_CLAIM_TOWNBLOCK(
			"economy.new_expand.price_claim_townblock",
			"25.0",
			"# The price for a town to expand one townblock."),
	ECO_PRICE_PURCHASED_BONUS_TOWNBLOCK(
			"economy.new_expand.price_purchased_bonus_townblock",
			"25.0",
			"# How much it costs a player to buy extra blocks."),
	ECO_PRICE_PURCHASED_BONUS_TOWNBLOCK_INCREASE(
			"economy.new_expand.price_purchased_bonus_townblock_increase",
			"1.0",
			"# How much every extra bonus block costs more. Set to 1 to deactivate this. 1.2 means +20% to every bonus claim block cost."),

	ECO_DEATH("economy.death", "", ""),
	ECO_PRICE_DEATH_TYPE("economy.death.price_death_type", "fixed", "# Either fixed or percentage.", "# For percentage 1.0 would be 100%. 0.01 would be 1%."),
	ECO_PRICE_DEATH_PERCENTAGE_CAP("economy.death.percentage_cap", "0.0", "# A maximum amount paid out by a resident from their personal holdings for percentage deaths.", "# Set to 0 to have no cap."),
	ECO_PRICE_DEATH_PVP_ONLY("economy.death.price_death_pvp_only", "false", "# If True, only charge death prices for pvp kills. Not monsters/environmental deaths."),
	ECO_PRICE_DEATH("economy.death.price_death", "1.0", ""),
	ECO_PRICE_DEATH_TOWN("economy.death.price_death_town", "0.0", ""),
	ECO_PRICE_DEATH_NATION("economy.death.price_death_nation", "0.0", ""),

	ECO_BANK_CAP("economy.banks", "", ""),
	ECO_BANK_CAP_TOWN(
			"economy.banks.town_bank_cap",
			"0.0",
			"# Maximum amount of money allowed in town bank",
			"# Use 0 for no limit"),
	ECO_BANK_TOWN_ALLOW_WITHDRAWLS(
			"economy.banks.town_allow_withdrawls",
			"true",
			"# Set to true to allow withdrawls from town banks"), ECO_BANK_CAP_NATION(
			"economy.banks.nation_bank_cap",
			"0.0",
			"# Maximum amount of money allowed in nation bank",
			"# Use 0 for no limit"),
	ECO_BANK_NATION_ALLOW_WITHDRAWLS(
			"economy.banks.nation_allow_withdrawls",
			"true",
			"# Set to true to allow withdrawls from nation banks"),
	ECO_BANK_DISALLOW_BANK_ACTIONS_OUTSIDE_TOWN(
			"economy.banks.disallow_bank_actions_outside_town",
			"false",
			"# When set to true, players can only use their town withdraw/deposit commands while inside of their own town.",
			"# Likewise, nation banks can only be withdrawn/deposited to while in the capital city."),

	ECO_CLOSED_ECONOMY_SERVER_ACCOUNT(
			"economy.closed_economy.server_account",
			"towny-server",
			"# The name of the account that all money that normally disappears goes into."),
	ECO_CLOSED_ECONOMY_ENABLED(
			"economy.closed_economy.enabled",
			"false",
			"# Turn on/off whether all transactions that normally don't have a second party are to be done with a certain account.",
			"# Eg: The money taken during Daily Taxes is just removed. With this on, the amount taken would be funneled into an account.",
			"#     This also applies when a player collects money, like when the player is refunded money when a delayed teleport fails."),

	ECO_DAILY_TAXES("economy.daily_taxes", "", ""),
	ECO_DAILY_TAXES_ENABLED(
			"economy.daily_taxes.enabled",
			"true",
			"# Enables taxes to be collected daily by town/nation",
			"# If a town can't pay it's tax then it is kicked from the nation.",
			"# if a resident can't pay his plot tax he loses his plot.",
			"# if a resident can't pay his town tax then he is kicked from the town.",
			"# if a town or nation fails to pay it's upkeep it is deleted."),
	ECO_DAILY_TAXES_MAX_TAX(
			"economy.daily_taxes.max_tax_amount",
			"1000.0",
			"# Maximum tax amount allowed when using flat taxes"),
	ECO_DAILY_TAXES_MAX_TAX_PERCENT(
			"economy.daily_taxes.max_tax_percent",
			"25",
			"# maximum tax percentage allowed when taxing by percentages"),
	ECO_PRICE_NATION_UPKEEP(
			"economy.daily_taxes.price_nation_upkeep",
			"100.0",
			"# The server's daily charge on each nation. If a nation fails to pay this upkeep",
			"# all of it's member town are kicked and the Nation is removed."),
	ECO_PRICE_TOWN_UPKEEP(
			"economy.daily_taxes.price_town_upkeep",
			"10.0",
			"# The server's daily charge on each town. If a town fails to pay this upkeep",
			"# all of it's residents are kicked and the town is removed."),
	ECO_PRICE_TOWN_UPKEEP_PLOTBASED(
			"economy.daily_taxes.town_plotbased_upkeep",
			"false",
			"# Uses total amount of owned plots to determine upkeep instead of the town level (Number of residents)",
			"# calculated by (number of claimed plots X price_town_upkeep)."),
	ECO_PRICE_TOWN_UPKEEP_PLOTBASED_TOWNLEVEL_MODIFIER(
			"economy.daily_taxes.town_plotbased_upkeep_affected_by_town_level_modifier",
			"false",
			"# If set to true, the plot-based-upkeep system will be modified by the Town Levels' upkeep modifiers."),
	ECO_UPKEEP_PLOTPAYMENTS(
			"economy.daily_taxes.use_plot_payments",
			"false",
			"# If enabled and you set a negative upkeep for the town",
			"# any funds the town gains via upkeep at a new day",
			"# will be shared out between the plot owners."),
	JAIL(
			"jail",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                 Jail Plot settings                   | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	JAIL_IS_JAILING_ATTACKING_ENEMIES(
			"jail.is_jailing_attacking_enemies",
			"false",
			"#If true attacking players who die on enemy-town land will be placed into the defending town's jail if it exists.",
			"#Requires town_respawn to be true in order to work."),
	JAIL_IS_JAILING_ATTACKING_OUTLAWS(
			"jail.is_jailing_attacking_outlaws",
			"false",
			"#If true attacking players who are considered an outlaw, that are killed inside town land will be placed into the defending town's jail if it exists.",
			"#Requires town_respawn to be true in order to work."),
	JAIL_JAIL_ALLOWS_ENDER_PEARLS(
			"jail.jail_allows_ender_pearls",
			"false",
			"#If true jailed players can use Ender Pearls but are still barred from using other methods of teleporting."),
	JAIL_JAIL_DENIES_TOWN_LEAVE(
			"jail.jail_denies_town_leave",
			"false",
			"#If false jailed players can use /town leave, and escape a jail."),
	JAIL_BAIL("jail.bail", "", ""),
	JAIL_BAIL_IS_ALLOWING_BAIL(
			"jail.bail.is_allowing_bail",
			"false",
			"#If true players can pay a bail amount to be unjailed."),
	JAIL_BAIL_BAIL_AMOUNT(
			"jail.bail.bail_amount",
			"10",
			"#Amount that bail costs."),
	BANK(
			"bank",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                 Bank Plot settings                   | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			"  # Bank plots may be used by other economy plugins using the Towny API.",			
			""),
	BANK_IS_LIMTED_TO_BANK_PLOTS(
			"bank.is_banking_limited_to_bank_plots",
			"false",
			"# If true players will only be able to use /t deposit, /t withdraw, /n deposit & /n withdraw while inside bank plots belonging to the town or nation capital respectively.",
			"# Home plots will also allow deposit and withdraw commands."),			
	WAR(
			"war",
			"",
			"",
			"",
			"  ############################################################",
			"  # +------------------------------------------------------+ #",
			"  # |                     War settings                     | #",
			"  # +------------------------------------------------------+ #",
			"  ############################################################",
			""),
	WARTIME_NATION_CAN_BE_NEUTRAL(
			"war.nation_can_be_neutral",
			"true",
			"#This setting allows you disable the ability for a nation to pay to remain neutral during a war."),
	WAR_DISALLOW_ONE_WAY_ALLIANCE(
			"war.disallow_one_way_alliance",
			"false",
			"#By setting this to true, nations will receive a prompt for alliances and alliances will show on both nations."
	),
	WAR_ECONOMY(
			"war.economy",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |         Economy Transfers During War settings        | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	WAR_ECONOMY_ENEMY("war.economy.enemy", ""),
	WAR_ECONOMY_ENEMY_PLACE_FLAG(
			"war.economy.enemy.place_flag",
			"10",
			"# Amount charged to place a warflag (payed to server)."),
	WAR_ECONOMY_ENEMY_DEFENDED_ATTACK(
			"war.economy.enemy.defended_attack",
			"10",
			"# Amount payed from the flagbearer to the defender after defending the area."),
	WAR_ECONOMY_TOWNBLOCK_WON(
			"war.economy.townblock_won",
			"10",
			"# Defending town pays attaking flagbearer. If a negative (attacker pays defending town),",
			"# and the attacker can't pay, the attack is canceled."),
	WAR_ECONOMY_HOMEBLOCK_WON(
			"war.economy.homeblock_won",
			"100",
			"# Same as townblock_won but for the special case of winning the homeblock."),
	WAR_EVENT(
			"war.event",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                 War Event settings                   | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			"",
			"# This is started with /townyadmnin toggle war",
			"",
			"# In peace time War spoils are accumulated from towns and nations being",
			"# deleted with any money left in the bank.",
			"#",
			"# These funds are increased during a war event upon a player death.",
			"# An additional bonus to the war chest is set in base_spoils.",
			"#",
			"# During the event a town losing a townblock pays the wartime_town_block_loss_price to the attacking town.",
			"# The war is won when the only nations left in the battle are allies, or only a single nation.",
			"#",
			"# The winning nations share half of the war spoils.",
			"# The remaining half is paid to the town which took the most town blocks, and lost the least.",
			""),
	WAR_EVENT_WARNING_DELAY("war.event.warning_delay", "30"),
	WAR_EVENT_TOWNS_NEUTRAL(
			"war.event.towns_are_neutral",
			"true",
			"#If false all towns not in nations can be attacked during a war event."),
	WAR_EVENT_ENEMY_ONLY_ATTACK_BORDER(
			"war.event.enemy.only_attack_borders",
			"true",
			"# If true, enemy's can only attack the edge plots of a town in war."),
	WAR_EVENT_PLOTS_HEALABLE(
			"war.event.plots.healable",
			"true",
			"# If true, nation members and allies can regen health on plots during war."),
	WAR_EVENT_PLOTS_FIREWORK_ON_ATTACKED(
			"war.event.plots.firework_on_attacked",
			"true",
			"# If true, fireworks will be launched at plots being attacked or healed in war every war tick."),
	WAR_EVENT_REMOVE_ON_MONARCH_DEATH(
			"war.event.remove_on_monarch_death",
			"false",
			"",
			"# If true and the monarch/king dies the nation is removed from the war."),
	WAR_EVENT_BLOCK_GRIEFING(
			"war.event.allow_block_griefing",
			"false",
			"# If enabled players will be able to break/place any blocks in enemy plots during a war.",
			"# This setting SHOULD NOT BE USED unless you want the most chaotic war possible.",
			"# The editable_materials list in the Warzone Block Permission section should be used instead."),
	WAR_EVENT_BLOCK_HP_HEADER(
			"war.event.block_hp",
			"",
			"",
			"# A townblock takes damage every 5 seconds that an enemy is stood in it."),
	WAR_EVENT_TOWN_BLOCK_HP("war.event.block_hp.town_block_hp", "60"),
	WAR_EVENT_HOME_BLOCK_HP("war.event.block_hp.home_block_hp", "120"),

	WAR_EVENT_ECO_HEADER("war.event.eco", "", ""),
	WAR_EVENT_BASE_SPOILS(
			"war.event.eco.base_spoils",
			"100.0",
			"# This amount is new money injected into the economy with a war event."),
	WAR_EVENT_TOWN_BLOCK_LOSS_PRICE(
			"war.event.eco.wartime_town_block_loss_price",
			"100.0",
			"# This amount is taken from the losing town for each plot lost."),
	WAR_EVENT_PRICE_DEATH(
			"war.event.eco.price_death_wartime",
			"200.0",
			"# This amount is taken from the player if they die during the event"),
	WAR_EVENT_COSTS_TOWNBLOCKS(
			"war.event.costs_townblocks",
			"false",
			"# If set to true when a town drops an enemy townblock's HP to 0, the attacking town gains a bonus townblock,",
			"# and the losing town gains a negative (-1) bonus townblock."),

	WAR_EVENT_POINTS_HEADER("war.event.points", "", ""),
	WAR_EVENT_POINTS_TOWNBLOCK("war.event.points.points_townblock", "1"),
	WAR_EVENT_POINTS_TOWN("war.event.points.points_town", "10"),
	WAR_EVENT_POINTS_NATION("war.event.points.points_nation", "100"),
	WAR_EVENT_POINTS_KILL("war.event.points.points_kill", "1"),
	WAR_EVENT_MIN_HEIGHT(
			"war.event.min_height",
			"60",
			"",
			"# The minimum height at which a player must stand to count as an attacker."),
	WAR_ENEMY(
			"war.enemy",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                   Flag war settings                  | #",
			"# |                                                      | #",
			"# |               Separate from Event War                | #",
			"# |                 Unsupported / Buggy                  | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	WAR_ENEMY_ALLOW_ATTACKS(
			"war.enemy.allow_attacks",
			"false",
			"# If false, players won't be able to place war flags, effectively disabling warzones."),
	WAR_ENEMY_ONLY_ATTACK_BORDER(
			"war.enemy.only_attack_borders",
			"true",
			"# If true, enemy's can only attack the edge plots of a town with war flags."),
	WAR_ENEMY_MIN_PLAYERS_ONLINE_IN_TOWN(
			"war.enemy.min_players_online_in_town",
			"2",
			"# This many people must be online in target town in order to place a war flag in their domain."),
	WAR_ENEMY_MIN_PLAYERS_ONLINE_IN_NATION(
			"war.enemy.min_players_online_in_nation",
			"3",
			"# This many people must be online in target nation in order to place a war flag in their domain."),
	WAR_ENEMY_MAX_ACTIVE_FLAGS_PER_PLAYER(
			"war.enemy.max_active_flags_per_player",
			"1"),
	WAR_ENENY_FLAG("war.enemy.flag", ""),
	WAR_ENEMY_FLAG_WAITING_TIME("war.enemy.flag.waiting_time", "1m"),
	WAR_ENEMY_FLAG_BASE_BLOCK(
			"war.enemy.flag.base_block",
			"fence",
			"# This is the block a player must place to trigger the attack event."),
	WAR_ENEMY_FLAG_LIGHT_BLOCK(
			"war.enemy.flag.light_block",
			"torch",
			"# This is the block a player must place to trigger the attack event."),
	WAR_ENEMY_BEACON("war.enemy.beacon", ""),
	WAR_ENEMY_BEACON_RADIUS(
			"war.enemy.beacon.radius",
			"3",
			"# Must be smaller than half the size of town_block_size."),
	WAR_ENEMY_BEACON_HEIGHT_ABOVE_FLAG(
			"war.enemy.beacon.height_above_flag",
			"",
			"# The range the beacon will be drawn in. It's flexibility is in case the flag is close to the height limit.",
			"# If a flag is too close to the height limit (lower than the minimum), it will not be drawn."),
	WAR_ENEMY_BEACON_HEIGHT_ABOVE_FLAG_MIN(
			"war.enemy.beacon.height_above_flag.min",
			"3"),
	WAR_ENEMY_BEACON_HEIGHT_ABOVE_FLAG_MAX(
			"war.enemy.beacon.height_above_flag.max",
			"64"),
	WAR_ENEMY_BEACON_DRAW("war.enemy.beacon.draw", "true"),
	WAR_ENEMY_BEACON_WIREFRAME_BLOCK(
			"war.enemy.beacon.wireframe_block",
			"glowstone"),
	WAR_WARZONE(
			"war.warzone",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |              Warzone Block Permissions               | #",
			"# |                                                      | #",
			"# |              Used in Flag & Event Wars               | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	WAR_WARZONE_EDITABLE_MATERIALS(
			"war.warzone.editable_materials",
			"tnt,fence,ladder,wood_door,iron_door,fire",
			"# List of materaials that can be modified in a warzone.",
			"# '*' = Allow all materials.",
			"# Prepend a '-' in front of a material to remove it. Used in conjunction with when you use '*'.",
			"# Eg: '*,-chest,-furnace'"),
	WAR_WARZONE_ITEM_USE("war.warzone.item_use", "true"),
	WAR_WARZONE_SWITCH("war.warzone.switch", "true"),
	WAR_WARZONE_FIRE(
			"war.warzone.fire",
			"true",
			"# Add '-fire' to editable materials for complete protection when setting is false. This prevents fire to be created and spread."),
	WAR_WARZONE_EXPLOSIONS("war.warzone.explosions", "true"),
	WAR_WARZONE_EXPLOSIONS_BREAK_BLOCKS(
			"war.warzone.explosions_break_blocks",
			"true"),
	WAR_WARZONE_EXPLOSIONS_REGEN_BLOCKS(
			"war.warzone.explosions_regen_blocks",
			"true",
			"# Only under affect when explosions_break_blocks is true."),
	WAR_WARZONE_EXPLOSIONS_IGNORE_LIST(
			"war.warzone.explosions_ignore_list",
			"WOODEN_DOOR,ACACIA_DOOR,DARK_OAK_DOOR,JUNGLE_DOOR,BIRCH_DOOR,SPRUCE_DOOR,IRON_DOOR,CHEST,TRAPPED_CHEST,FURNACE,BURNING_FURNACE,DROPPER,DISPENSER,HOPPER,ENDER_CHEST,WHITE_SHULKER_BOX,ORANGE_SHULKER_BOX,MAGENTA_SHULKER_BOX,LIGHT_BLUE_SHULKER_BOX,YELLOW_SHULKER_BOX,LIME_SHULKER_BOX,PINK_SHULKER_BOX,GRAY_SHULKER_BOX,SILVER_SHULKER_BOX,CYAN_SHULKER_BOX,PURPLE_SHULKER_BOX,BLUE_SHULKER_BOX,BROWN_SHULKER_BOX,GREEN_SHULKER_BOX,RED_SHULKER_BOX,BLACK_SHULKER_BOX,NOTE_BLOCK,LEVER,STONE_PLATE,IRON_DOOR_BLOCK,WOOD_PLATE,JUKEBOX,DIODE_BLOCK_OFF,DIODE_BLOCK_ON,FENCE_GATE,GOLD_PLATE,IRON_PLATE,REDSTONE_COMPARATOR_OFF,REDSTONE_COMPARATOR_ON,BEACON",
			"# A list of blocks that will not be exploded, mostly because they won't regenerate properly.",
			"# These blocks will also protect the block below them, so that blocks like doors do not dupe themselves.",
			"# Only under affect when explosions_break_blocks is true.");


	private final String Root;
	private final String Default;
	private String[] comments;

	ConfigNodes(String root, String def, String... comments) {

		this.Root = root;
		this.Default = def;
		this.comments = comments;
	}

	/**
	 * Retrieves the root for a config option
	 *
	 * @return The root for a config option
	 */
	public String getRoot() {

		return Root;
	}

	/**
	 * Retrieves the default value for a config path
	 *
	 * @return The default value for a config path
	 */
	public String getDefault() {

		return Default;
	}

	/**
	 * Retrieves the comment for a config path
	 *
	 * @return The comments for a config path
	 */
	public String[] getComments() {

		if (comments != null) {
			return comments;
		}

		String[] comments = new String[1];
		comments[0] = "";
		return comments;
	}
}
