package cc.avie.waypoints.fabric;

import cc.avie.waypoints.AviesWaypoints;
import cc.avie.waypoints.Keybinds;
import cc.avie.waypoints.render.WaypointRenderer;
import cc.avie.waypoints.share.ChatShareHandler;
import cc.avie.waypoints.waypoint.WaypointStore;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;

public class AviesWaypointsFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyMapping.Category.register(Keybinds.CATEGORY.id());
		Keybinds.ALL.forEach(KeyMappingHelper::registerKeyMapping);
		AviesWaypoints.init(AviesWaypoints.MOD_ID);

		ClientReceiveMessageEvents.ALLOW_CHAT.register((message, playerChatMessage, sender, boundChatType, timeStamp) ->
			ChatShareHandler.handle(message, sender != null ? sender.name() : null));
		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> overlay || ChatShareHandler.handle(message, null));

		WaypointRenderer renderer = new WaypointRenderer();
		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, AviesWaypoints.id("waypoints"), renderer::extractRenderState);

		ClientTickEvents.END_CLIENT_TICK.register(AviesWaypoints::tick);
		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> WaypointStore.save());

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(AviesWaypoints.<FabricClientCommandSource>command()));
	}
}
