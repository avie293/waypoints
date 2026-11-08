package cc.avie.waypoints.platform;

import java.nio.file.Path;
import java.util.ServiceLoader;

/** What the mod needs from the mod loader, implemented once per loader and found with {@link ServiceLoader}. */
public interface Platform {
	static Platform get() {
		return Holder.INSTANCE;
	}

	/** The folder of the game, with the {@code saves} and {@code config} folders in it. */
	Path gameDir();

	/** The {@code config} folder of the game. */
	Path configDir();

	boolean isModLoaded(String modId);

	final class Holder {
		private static final Platform INSTANCE = ServiceLoader.load(Platform.class, Platform.class.getClassLoader()).findFirst()
			.orElseThrow(() -> new IllegalStateException("No platform found, the jar of the wrong loader is installed"));

		private Holder() {
		}
	}
}
