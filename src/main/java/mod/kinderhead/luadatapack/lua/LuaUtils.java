package mod.kinderhead.luadatapack.lua;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.function.ThreeArgFunction;

public class LuaUtils {
    public static LuaTable readonly(LuaValue t) {
        LuaTable proxy = new LuaTable();

        LuaTable metatable = new LuaTable();
        metatable.rawset("__index", t);
        metatable.rawset("__newindex", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1, LuaValue arg2, LuaValue arg3) throws LuaError, UnwindThrowable {
                throw new LuaError("Table is readonly");
            }
        });

        proxy.setMetatable(metatable);
        return proxy;
    }
}
