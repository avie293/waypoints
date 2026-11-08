package cc.avie.waypoints.mixin;

import cc.avie.waypoints.waypoint.WaypointStore;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageAccessMixin {
	@Shadow
	@Final
	private String levelId;

	@Inject(method = "deleteLevel", at = @At("RETURN"))
	private void avieswaypoints$deleteWaypoints(CallbackInfo info) {
		WaypointStore.onWorldDeleted(this.levelId);
	}
}
