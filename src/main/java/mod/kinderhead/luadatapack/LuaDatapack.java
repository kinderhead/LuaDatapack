package mod.kinderhead.luadatapack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squiddev.cobalt.LuaTable;

import mod.kinderhead.luadatapack.datapack.Files;
import mod.kinderhead.luadatapack.datapack.ReloadListener;
import mod.kinderhead.luadatapack.lua.LuaRunner;
import mod.kinderhead.luadatapack.lua.api.MCLuaFactory;

public class LuaDatapack implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("LuaDatapack");
	public static MinecraftServer SERVER = null;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing");

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ReloadListener());

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
				CommandManager.literal("lua")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("name", IdentifierArgumentType.identifier())
					.executes(ctx -> {
						var id = IdentifierArgumentType.getIdentifier(ctx, "name");

						String code = Files.get(id);
						if (code == null) {
							ctx.getSource().sendFeedback(Text.translatable("text.luadatapack.file_not_found", id.toString()), false);
							return -1;
						}

						LuaTable env = new LuaTable();
						env.rawset("src", MCLuaFactory.get(ctx.getSource()));

						if (LuaRunner.Run(code, id.toString(), env)) {
							return 1;
						}

						ctx.getSource().sendFeedback(Text.translatable("text.luadatapack.lua_error", id.toString()), false);
						return -1;
					}
				))
			);
		});

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			SERVER = server;
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
			SERVER = null;
		});
	}
}
