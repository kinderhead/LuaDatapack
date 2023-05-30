package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.lib.LuaLibrary;

import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.util.Identifier;

public class StorageLib implements LuaLibrary {
    public static StorageState storageState;

    public static void Init() {
        storageState = StorageState.get();
    }

    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        LuaTable table = new LuaTable();

        table.rawset("load", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            return storageState.data.get(new Identifier(arg1.checkString()));
        }));

        table.rawset("save", LuaUtils.twoArgFunctionFactory((s, arg1, arg2) -> {
            storageState.data.put(new Identifier(arg1.checkString()), arg2);
            storageState.markDirty();

            return Constants.NIL;
        }));

        env.rawset("storage", table);
        return table;
    }
}
