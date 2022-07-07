package mod.kinderhead.luadatapack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mod.kinderhead.luadatapack.datapack.Files;
import mod.kinderhead.luadatapack.datapack.ReloadListener;
import mod.kinderhead.luadatapack.lua.LuaRunner;

public class LuaDatapack implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("LuaDatapack");

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
							ctx.getSource().sendFeedback(Text.of("Could not find lua function " + id.toString()), false);
							return -1;
						}

						if (LuaRunner.Run(code, id.toString())) {
							return 1;
						}

						ctx.getSource().sendFeedback(Text.of("Error executing script " + id.toString() + ". See console for details"), false);
						return -1;
					}
				))
			);
		});
	}
}
