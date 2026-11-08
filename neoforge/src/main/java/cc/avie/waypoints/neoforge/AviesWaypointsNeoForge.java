package cc.avie.waypoints.neoforge;

import cc.avie.waypoints.AviesWaypoints;
import cc.avie.waypoints.Keybinds;
import cc.avie.waypoints.render.WaypointRenderer;
import cc.avie.waypoints.share.ChatShareHandler;
import cc.avie.waypoints.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import org.jspecify.annotations.Nullable;

@Mod(value = "avies_waypoints", dist = Dist.CLIENT)
public class AviesWaypointsNeoForge {
	public AviesWaypointsNeoForge(IEventBus modBus, ModContainer container) {
		// NeoForge mod ids can't contain "-", TabbyLib needs the id of the mod on this loader
		AviesWaypoints.init(container.getModId());

		modBus.addListener(RegisterKeyMappingsEvent.class, event -> {
			event.registerCategory(Keybinds.CATEGORY);
			Keybinds.ALL.forEach(event::register);
		});

		WaypointRenderer renderer = new WaypointRenderer();
		modBus.addListener(RegisterGuiLayersEvent.class, event ->
			event.registerBelow(VanillaGuiLayers.CHAT, AviesWaypoints.id("waypoints"), renderer::extractRenderState));

		NeoForge.EVENT_BUS.addListener(ClientChatReceivedEvent.class, AviesWaypointsNeoForge::onChat);
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> AviesWaypoints.tick(Minecraft.getInstance()));
		NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, event -> WaypointStore.save());
		NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, event ->
			event.getDispatcher().register(AviesWaypoints.<CommandSourceStack>command()));
	}

	private static void onChat(ClientChatReceivedEvent event) {
		if (event instanceof ClientChatReceivedEvent.System system && system.isOverlay()) {
			return;
		}
		if (!ChatShareHandler.handle(event.getMessage(), senderName(event))) {
			event.setCanceled(true);
		}
	}

	private static @Nullable String senderName(ClientChatReceivedEvent event) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (event.isSystem() || connection == null) {
			return null;
		}
		PlayerInfo info = connection.getPlayerInfo(event.getSender());
		return info != null ? info.getProfile().name() : null;
	}
}
