package cc.avie.waypoints;

import com.mojang.blaze3d.platform.InputConstants;
import cc.avie.tabbylib.api.TabbyLibApi;
import cc.avie.waypoints.config.WaypointsConfig;
import cc.avie.waypoints.gui.WaypointEditScreen;
import cc.avie.waypoints.gui.WaypointListScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.List;

public final class Keybinds {
	// Registered by the loader entrypoints together with the key mappings
	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(AviesWaypoints.id("waypoints"));

	public static final KeyMapping ADD = new KeyMapping("key.avies-waypoints.add", InputConstants.KEY_B, CATEGORY);
	public static final KeyMapping LIST = new KeyMapping("key.avies-waypoints.list", InputConstants.KEY_U, CATEGORY);
	public static final KeyMapping TOGGLE = new KeyMapping("key.avies-waypoints.toggle", InputConstants.UNKNOWN.getValue(), CATEGORY);
	public static final KeyMapping CONFIG = new KeyMapping("key.avies-waypoints.config", InputConstants.UNKNOWN.getValue(), CATEGORY);
	public static final List<KeyMapping> ALL = List.of(ADD, LIST, TOGGLE, CONFIG);

	private Keybinds() {
	}

	public static void tick(Minecraft minecraft) {
		while (ADD.consumeClick()) {
			if (minecraft.player != null && minecraft.gui.screen() == null) {
				minecraft.gui.setScreen(WaypointEditScreen.create(null));
			}
		}
		while (LIST.consumeClick()) {
			if (minecraft.player != null && minecraft.gui.screen() == null) {
				minecraft.gui.setScreen(new WaypointListScreen(null));
			}
		}
		while (TOGGLE.consumeClick()) {
			WaypointsConfig.ENABLED.set(!WaypointsConfig.ENABLED.get());
			WaypointsConfig.CONFIG.save();
		}
		while (CONFIG.consumeClick()) {
			TabbyLibApi.openScreen(WaypointsConfig.CONFIG.getModId());
		}
	}
}
