package mod.kinderhead.luadatapack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mod.kinderhead.luadatapack.datapack.ReloadListener;

public class LuaDatapack implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("LuaDatapack");
	public static MinecraftServer SERVER = null;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing");

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ReloadListener());

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			LuaCommand.register(dispatcher, registryAccess, environment);
		});

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			SERVER = server;
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
			SERVER = null;
		});
	}
}
