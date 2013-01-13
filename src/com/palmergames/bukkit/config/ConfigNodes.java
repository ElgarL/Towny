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
	VERSION_BUKKIT(
			"version.bukkit_version",
			"2543",
			"# Minimum required version of CraftBukkit.  Please do not edit."),
	VERSION_BUKKIT_BYPASS(
			"version.bypass_version_check",
			"false",
			"# If enabled we will not check the CraftBukkit version at startup."),
	LANGUAGE(
			"language",
			"english.yml",
			"",
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
			"#    towny.admin: User is able to use /townyadmin, as well as the ability to build/destroy anywhere. User is also able to make towns or nations when set to admin only.",
			"#    towny.cheat.bypass : User is able to use any fly mods and double block jump (disables towny cheat protection for this user).",
			"#    towny.top : User can access the command towny top",
			"#    towny.town.* : User has access to all town permission nodes.",
			"#        towny.town.new :User is able to create a town",
			"#        towny.town.delete :User is able to delete their town",
			"#        towny.town.claim : User is able to expand his town with /town claim",
			"#        towny.town.plot : User is able to use the /plot commands",
			"#        towny.town.resident : User is able to join towns upon invite.",
			"#    towny.town.toggle.*: User can access all town toggle commands (if a mayor or assistant).",
			"#        towny.town.toggle.pvp",
			"#        towny.town.toggle.public",
			"#        towny.town.toggle.explosions",
			"#        towny.town.toggle.fire",
			"#        towny.town.toggle.mobs",
			"#    towny.nation.* : User has access to all town permission nodes.",
			"#        towny.nation.new :User is able to create a nation",
			"#        towny.nation.delete :User is able to delete their nation (if king)",
			"#        towny.nation.rename :User is able to rename their nation (if king/assistant)",
			"#        towny.nation.grant-titles :User is able to grant titles/surnames to the nation residents (if King)",
			"#    towny.wild.*: User is able to build/destroy in wild regardless.",
			"#        towny.wild.build",
			"#        towny.wild.destroy",
			"#        towny.wild.switch",
			"#        towny.wild.item_use",

			"#    towny.wild.block.[block id].* : User is able to edit [block id] in the wild.",
			"#        towny.wild.build.[block id]",
			"#        towny.wild.destroy.[block id]",
			"#        towny.wild.switch.[block id]",
			"#        towny.wild.item_use.[block id]",
			"#",
			"#    towny.claimed.* : User can build/destroy/switch/item_use in all towns.",
			"#        towny.claimed.build : User can build in all towns.",
			"#        towny.claimed.destroy : User can destroy in all towns.",
			"#        towny.claimed.switch : User can switch in all towns.",
			"#        towny.claimed.item_use : User can use use items in all towns. ",
			"#    towny.claimed.alltown.* : User is able to edit specified/all block types in all towns.",
			"#        towny.claimed.alltown.build.[block id]",
			"#        towny.claimed.alltown.destroy.[block id]",
			"#        towny.claimed.alltown.switch.[block id] : User can switch specified/all block types in all towns.",
			"#        towny.claimed.alltown.item_use.[block id]",
			"#    towny.claimed.owntown.* : User is able to edit specified/all block types in their own town.",
			"#        towny.claimed.owntown.build.[block id]",
			"#        towny.claimed.owntown.destroy.[block id] : (handy to allow clearing of snow '78')",
			"#        towny.claimed.owntown.switch.[block id]",
			"#        towny.claimed.owntown.item_use.[block id]",
			"#",
			"#    towny.town.spawn.*: Grants all Spawn travel nodes",
			"#        towny.town.spawn.town : Ability to spawn to your own town.",
			"#        towny.town.spawn.nation : Ability to spawn to other towns in your nation.",
			"#        towny.town.spawn.ally : Ability to spawn to towns in nations allied with yours.",
			"#        towny.town.spawn.public : Ability to spawn to unaffilated public towns.",
			"#",
			"# these will be moved to permissions nodes at a later date"),
	PERMS_TOWN_CREATION_ADMIN_ONLY(
			"permissions.town_creation_admin_only",
			"false"),
	PERMS_NATION_CREATION_ADMIN_ONLY(
			"permissions.nation_creation_admin_only",
			"false"),
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
	TOWN_MAX_PURCHASED_BLOCKS(
			"town.max_purchased_blocks",
			"0",
			"# Limits the maximum amount of bonus blocks a town can buy."),
	TOWN_MAX_PLOTS_PER_RESIDENT(
			"town.max_plots_per_resident",
			"100",
			"# maximum number of plots any single resident can own"),
	TOWN_LIMIT(
			"town.town_limit",
			"3000",
			"# Maximum number of towns allowed on the server."),
	TOWN_MIN_DISTANCE_FROM_TOWN_HOMEBLOCK(
			"town.min_distance_from_town_homeblock",
			"5",
			"",
			"# Minimum number of plots any towns home plot must be from the next town.",
			"# This will prevent someone founding a town right on your doorstep"),
	TOWN_MAX_DISTANCE_BETWEEN_HOMEBLOCKS(
			"town.max_distance_between_homeblocks",
			"0",
			"",
			"# Maximum distance between homblocks.",
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
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |             Default new world settings               | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			"",
			"# These flags are only used at the initial setp of a new world.",
			"",
			"# Once Towny is running each world can be altered from within game",
			"# using '/townyworld toggle'",
			""),

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
			"26,50,55,63,64,68,70,71,72,75,76,93,94",
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
			"#",
			"# If you allow players to break/build in the wild the snapshot will",
			"# include any changes made before the plot was claimed."),
	NWS_PLOT_MANAGEMENT_REVERT_TIME(
			"new_world_settings.plot_management.revert_on_unclaim.speed",
			"1s"),
	NWS_PLOT_MANAGEMENT_REVERT_IGNORE(
			"new_world_settings.plot_management.revert_on_unclaim.block_ignore",
			"14,21,22,41,42,48,50,52,56,57,63,68,89",
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
			"Creeper,EnderCrystal,EnderDragon,Fireball,SmallFireball,TNTPrimed"),
	NWS_PLOT_MANAGEMENT_WILD_MOB_REVERT_TIME(
			"new_world_settings.plot_management.wild_revert_on_mob_explosion.delay",
			"20s"),

	GTOWN_SETTINGS(
			"global_town_settings",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                Global town settings                  | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
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
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN(
			"global_town_settings.allow_town_spawn",
			"true",
			"# Allow the use of /town spawn"),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL(
			"global_town_settings.allow_town_spawn_travel",
			"true",
			"# Allow regular residents to use /town spawn [town] (TP to other towns if they are public)."),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL_NATION(
			"global_town_settings.allow_town_spawn_travel_nation",
			"true",
			"# Allow regular residents to use /town spawn [town] to other towns in your nation."),
	GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL_ALLY(
			"global_town_settings.allow_town_spawn_travel_ally",
			"true",
			"# Allow regular residents to use /town spawn [town] to other towns in a nation allied with your nation."),
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
	PLUGIN(
			"plugin",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                 Plugin interfacing                   | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
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
			"# This will attempt to use Register (if present)",
			"# Then it will attempt to access iConomy 5.01 directly",
			"# Enable if you have either iConomy 5.01, or Register.jar to",
			"# support: iConomy5, iConomy6, EssentialsEco, BOSEconomy.",
			"# https://github.com/iConomy/Register/tree/master/dist"),

	PLUGIN_USING_QUESTIONER_HEADER(
			"plugin.interfacing.using_questioner",
			"",
			"",
			"# Enable using_questioner if you are using questioner to send/receive invites to towns/nations.",
			"# See http://code.google.com/a/eclipselabs.org/p/towny/wiki/Questioner for more info."),
	PLUGIN_USING_QUESTIONER_ENABLE(
			"plugin.interfacing.using_questioner.enable",
			"true"),
	PLUGIN_QUESTIONER_ACCEPT(
			"plugin.interfacing.using_questioner.accept",
			"accept",
			"# The command to accept invitations."),
	PLUGIN_QUESTIONER_DENY(
			"plugin.interfacing.using_questioner.deny",
			"deny",
			"# The command to refuse invitations."),
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
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |               Filters colour and chat                | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
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
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |             block/item/mob protection                | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	PROT_ITEM_USE_ID(
			"protection.item_use_ids",
			"259,325,326,327,351,359,368,374,385",
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
	PROT_SWITCH_ID(
			"protection.switch_ids",
			"23,25,54,61,62,64,69,70,71,72,77,96,84,93,94,107",
			"",
			"# Items which can be blocked or enabled via town/plot flags",
			"# 25 - noteblock",
			"# 54 - chest",
			"# 61 - furnace",
			"# 62 - lit furnace",
			"# 64 - wooden door",
			"# 69 - lever",
			"# 70 - stone pressure plate",
			"# 71 - iron door",
			"# 72 - wooden pressure plate",
			"# 77 - stone button",
			"# 96 - trap door",
			"# 84 - jukebox",
			"# 93/94 - redstone repeater"),
	PROT_MOB_REMOVE_TOWN(
			"protection.town_mob_removal_entities",
			"Monster,WaterMob,Flying,Slime",
			"",
			"# permitted entities http://jd.bukkit.org/apidocs/org/bukkit/entity/package-summary.html",
			"# Animals, Chicken, Cow, Creature, Creeper, Flying, Ghast, Giant, Monster, Pig, ",
			"# PigZombie, Sheep, Skeleton, Slime, Spider, Squid, WaterMob, Wolf, Zombie",
			"",
			"# Remove living entities within a town's boundaries, if the town has the mob removal flag set."),
			
	PROT_MOB_REMOVE_VILLAGER_BABIES_TOWN(
			"protection.town_prevent_villager_breeding",
			"false",
			"",
			"# Prevent the spawning of villager babies in towns."),
			
	PROT_MOB_REMOVE_WORLD(
			"protection.world_mob_removal_entities",
			"Monster,WaterMob,Flying,Slime",
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
	PROT_CHEAT(
			"protection.cheat_protection",
			"true",
			"",
			"# Prevent fly and double block jump cheats."),
	PROT_REGEN_DELAY(
			"protection.regen_delay",
			"0s",
			"",
			"# The amount of time it takes for a protected block to regenerate.  Use zero for no delay."),
	UNCLAIMED_ZONE(
			"unclaimed",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                Wilderness settings                   | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			"",
			"# These Settings defaults only. They are copied to each worlds data files upon first detection",
			"# To make changes for each world edit the settings in the relevant worlds data file 'plugins/Towny/data/worlds/'",
			""),
	UNCLAIMED_ZONE_BUILD("unclaimed.unclaimed_zone_build", "false"),
	UNCLAIMED_ZONE_DESTROY("unclaimed.unclaimed_zone_destroy", "false"),
	UNCLAIMED_ZONE_ITEM_USE("unclaimed.unclaimed_zone_item_use", "false"),
	UNCLAIMED_ZONE_IGNORE(
			"unclaimed.unclaimed_zone_ignore",
			"6,14,15,16,17,18,21,31,37,38,39,40,50,56,65,66,73,74,81,82,83,86,89"),
	UNCLAIMED_ZONE_SWITCH("unclaimed.unclaimed_zone_switch", "false"),
	//UNCLAIMED_ZONE_NAME("unclaimed.unclaimed_zone_name",""),
	//UNCLAIMED_PLOT_NAME("unclaimed.unclaimed_plot_name",""),

	NOTIFICATION(
			"notification",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                 Town Notifications                   | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			"",
			"# This is the format for the notifications sent as players move between plots.",
			"# Empty a particular format for it to be ignored.",
			"",
			"# Example:",
			"# [notification.format]",
			"# ~ [notification.area_[wilderness/town]][notification.splitter][notification.[no_]owner][notification.splitter][notification.plot.format]",
			"# ... [notification.plot.format]",
			"# ... [notification.plot.homeblock][notification.plot.splitter][notification.plot.forsale][notification.plot.splitter][notification.plot.type]",
			"# ~ Wak Town - Lord Jebus - [Home] [For Sale: 50 Beli] [Shop]",
			""),
	NOTIFICATION_FORMAT("notification.format", "&6 ~ %s"),
	NOTIFICATION_SPLITTER("notification.splitter", "&7 - "),
	NOTIFICATION_AREA_WILDERNESS("notification.area_wilderness", "&2%s"),
	NOTIFICATION_AREA_TOWN("notification.area_town", "&6%s"),
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
	FLAGS_DEFAULT(
			"default_perm_flags",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |             Default Town/Plot flags                  | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
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
	RES_SETTING(
			"resident_settings",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                  Resident settings                   | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
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
	ECO(
			"economy",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                  Economy settings                    | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
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

	ECO_PRICE_DEATH("economy.price_death", "1.0", ""),

	ECO_BANK_CAP("economy.banks", "", ""),
	ECO_BANK_CAP_TOWN(
			"economy.banks.town_bank_cap",
			"0.0",
			"# Maximum amount of money allowed in town bank",
			"# Use 0 for no limit"),
	ECO_BANK_TOWN_ALLOW_WITHDRAWLS(
			"economy.banks.town_allow_withdrawls",
			"true",
			"# Set to true to allow withdrawls from town banks"),
	ECO_BANK_CAP_NATION(
			"economy.banks.nation_bank_cap",
			"0.0",
			"# Maximum amount of money allowed in nation bank",
			"# Use 0 for no limit"),
	ECO_BANK_NATION_ALLOW_WITHDRAWLS(
			"economy.banks.nation_allow_withdrawls",
			"true",
			"# Set to true to allow withdrawls from nation banks"),

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
	ECO_UPKEEP_PLOTPAYMENTS(
			"economy.daily_taxes.use_plot_payments",
			"false",
			"# If enabled and you set a negative upkeep for the town",
			"# any funds the town gains via upkeep at a new day",
			"# will be shared out between the plot owners."),

	WAR(
			"war",
			"",
			"",
			"",
			"############################################################",
			"# +------------------------------------------------------+ #",
			"# |                     War settings                     | #",
			"# +------------------------------------------------------+ #",
			"############################################################",
			""),
	WARTIME_NATION_CAN_BE_NEUTRAL(
			"war.nation_can_be_neutral",
			"true",
			"#This setting allows you disable the ability for a nation to pay to remain neutral during a war."),
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
	WAR_EVENT_REMOVE_ON_MONARCH_DEATH(
			"war.event.remove_on_monarch_death",
			"false",
			"",
			"# If true and the monarch/king dies the nation is removed from the war."),
	WAR_EVENT_BLOCK_GRIEFING(
			"war.event.allow_block_griefing",
			"true",
			"# If enabled players will be able to break/place blocks in enemy plots during a war."),

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
			"# TODO: Blocks will not regen as of yet. Stay tuned for later changes.",
			"# Only under affect when explosions_break_blocks is true."), ;

	private final String Root;
	private final String Default;
	private String[] comments;

	private ConfigNodes(String root, String def, String... comments) {

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
