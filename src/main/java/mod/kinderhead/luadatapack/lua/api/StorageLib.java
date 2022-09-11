package mod.kinderhead.luadatapack.lua.api;

import java.util.HashMap;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.lib.LuaLibrary;

import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.util.Identifier;

public class StorageLib implements LuaLibrary {
    public static HashMap<Identifier, LuaValue> data = new HashMap<>();

    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        LuaTable table = new LuaTable();

        table.rawset("load", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            return data.get(new Identifier(arg1.checkString()));
        }));

        table.rawset("save", LuaUtils.twoArgFunctionFactory((s, arg1, arg2) -> {
            data.put(new Identifier(arg1.checkString()), arg2);

            return Constants.NIL;
        }));

        env.rawset("storage", table);
        return table;
    }
}
