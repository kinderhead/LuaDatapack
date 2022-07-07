package mod.kinderhead.luadatapack;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mod.kinderhead.luadatapack.lua.LuaRunner;

public class LuaDatapack implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("LuaDatapack");

	@Override
	public void onInitialize() {
		LuaRunner.Run("print(\"Hello from Lua!\")");
	}
}
