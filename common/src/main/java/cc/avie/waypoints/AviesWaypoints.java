package cc.avie.waypoints;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import cc.avie.tabbylib.api.TabbyLibApi;
import cc.avie.waypoints.config.WaypointsConfig;
import cc.avie.waypoints.gui.WaypointListScreen;
import cc.avie.waypoints.share.ChatShareHandler;
import cc.avie.waypoints.waypoint.DeathWaypoints;
import cc.avie.waypoints.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared by the Fabric and NeoForge entrypoints, which only hook these methods into the loader events. */
public final class AviesWaypoints {
	/** Namespace of the assets and translations, the NeoForge mod id is "avies_waypoints". */
	public static final String MOD_ID = "avies-waypoints";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static boolean wasDead;

	private AviesWaypoints() {
	}

	/** {@code modId} is the id of the mod on the current loader. */
	public static void init(String modId) {
		WaypointsConfig.init(modId);
	}

	public static void tick(Minecraft minecraft) {
		WaypointStore.tick(minecraft);
		Keybinds.tick(minecraft);
		checkDeath(minecraft);
	}

	public static <S> LiteralArgumentBuilder<S> command() {
		return LiteralArgumentBuilder.<S>literal("avieswaypoints")
			.executes(context -> {
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.execute(() -> minecraft.gui.setScreen(new WaypointListScreen(null)));
				return 1;
			})
			.then(LiteralArgumentBuilder.<S>literal("config").executes(context -> {
				TabbyLibApi.openScreen(WaypointsConfig.CONFIG.getModId());
				return 1;
			}))
			.then(LiteralArgumentBuilder.<S>literal("add_shared")
				.then(RequiredArgumentBuilder.<S, Integer>argument("id", IntegerArgumentType.integer(0)).executes(context -> {
					ChatShareHandler.addShared(IntegerArgumentType.getInteger(context, "id"));
					return 1;
				})));
	}

	private static void checkDeath(Minecraft minecraft) {
		boolean dead = minecraft.player != null && minecraft.player.isDeadOrDying();
		if (dead && !wasDead && WaypointsConfig.DEATH_WAYPOINTS.get()) {
			String dimension = WaypointStore.currentDimension();
			if (dimension != null) {
				DeathWaypoints.onDeath(dimension, minecraft.player.blockPosition());
			}
		}
		wasDead = dead;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
