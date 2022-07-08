package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.ValueFactory;
import org.squiddev.cobalt.lib.LuaLibrary;

import mod.kinderhead.luadatapack.datapack.Scripts;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.util.Identifier;

public class LuastdLib implements LuaLibrary {
    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        env.rawset("require", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            String name = arg1.checkString();

            String data = Scripts.get(new Identifier(name));
            if (data == null) {
                throw new LuaError("Could not find module " + name);
            } else {
                return env.rawget("loadstring").checkFunction().call(state, ValueFactory.valueOf(data)).checkFunction().call(state);
            }
        }));

        return env;
    }
}
