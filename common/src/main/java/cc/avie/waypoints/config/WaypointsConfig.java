package cc.avie.waypoints.config;

import cc.avie.tabbylib.api.Action;
import cc.avie.tabbylib.api.TabbyConfig;
import cc.avie.tabbylib.api.option.BooleanOption;
import cc.avie.tabbylib.api.option.DoubleOption;
import cc.avie.tabbylib.api.option.EnumOption;
import cc.avie.tabbylib.api.option.IntOption;
import cc.avie.tabbylib.api.option.Options;
import cc.avie.waypoints.AviesWaypoints;
import cc.avie.waypoints.Keybinds;
import cc.avie.waypoints.waypoint.XaeroImporter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class WaypointsConfig {
	public enum ShowMode {
		ALWAYS,
		LOOKING,
		NEVER
	}

	public static final BooleanOption ENABLED = Options.toggle("enabled", true);
	public static final EnumOption<ShowMode> SHOW_DISTANCE = Options.choice("showDistance", ShowMode.LOOKING).dependsOn(ENABLED);
	public static final BooleanOption NAME_WITH_DISTANCE = Options.toggle("nameWithDistance", true).dependsOn(ENABLED);
	public static final DoubleOption SCALE = Options.decimal("scale", 1.0).slider(0.5, 3.0, 0.1).dependsOn(ENABLED);
	public static final IntOption MAX_DISTANCE = Options.integer("maxDistance", 0)
		.range(0, 1_000_000)
		.format(value -> value == 0 ? Component.translatable("config.avies-waypoints.maxDistance.unlimited") : Component.literal(value + " m"))
		.dependsOn(ENABLED);
	public static final IntOption OPACITY = Options.integer("opacity", 80)
		.slider(10, 100, 5)
		.format(value -> Component.literal(value + "%"))
		.dependsOn(ENABLED);

	public static final BooleanOption DEATH_WAYPOINTS = Options.toggle("deathWaypoints", true);
	public static final BooleanOption KEEP_OLD_DEATHS = Options.toggle("keepOldDeaths", true).dependsOn(DEATH_WAYPOINTS);
	public static final BooleanOption DETECT_SHARED = Options.toggle("detectShared", true);
	public static final BooleanOption IMPORT_XAERO = Options.toggle("importXaero", true);

	public static final BooleanOption PLAYER_HEADS = Options.toggle("playerHeads", false);
	public static final BooleanOption WAYPOINTS_ON_BAR = Options.toggle("waypointsOnBar", false).dependsOn(PLAYER_HEADS);

	public static TabbyConfig CONFIG;

	private WaypointsConfig() {
	}

	private static void importNow() {
		Minecraft minecraft = Minecraft.getInstance();
		Component message = minecraft.level == null
			? Component.translatable("avies-waypoints.import.no_world")
			: Component.translatable("avies-waypoints.import.done", XaeroImporter.importCurrentWorld(minecraft));
		minecraft.gui.hud.getChat().addClientSystemMessage(message.copy().withStyle(ChatFormatting.GRAY));
	}

	public static void init(String modId) {
		CONFIG = TabbyConfig.builder(modId)
			.translationId(AviesWaypoints.MOD_ID)
			.fileName(AviesWaypoints.MOD_ID)
			.category("general", category -> category
				.section("display", section -> section.add(ENABLED, SHOW_DISTANCE, NAME_WITH_DISTANCE, SCALE, OPACITY, MAX_DISTANCE))
				.section("waypoints", section -> section.add(DEATH_WAYPOINTS, KEEP_OLD_DEATHS, DETECT_SHARED, IMPORT_XAERO,
					Action.of(Component.translatable("config.avies-waypoints.importXaeroNow"),
						Component.translatable("config.avies-waypoints.importXaeroNow.button"), WaypointsConfig::importNow)))
				.section("locatorBar", section -> section.add(PLAYER_HEADS, WAYPOINTS_ON_BAR))
				.section("keys", section -> section.add(
					Options.keyBind("keyAdd", Keybinds.ADD),
					Options.keyBind("keyList", Keybinds.LIST),
					Options.keyBind("keyToggle", Keybinds.TOGGLE),
					Options.keyBind("keyConfig", Keybinds.CONFIG))))
			.build();
	}
}
