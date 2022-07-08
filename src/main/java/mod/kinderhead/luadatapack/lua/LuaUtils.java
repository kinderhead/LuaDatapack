package mod.kinderhead.luadatapack.lua;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.function.OneArgFunction;
import org.squiddev.cobalt.function.ThreeArgFunction;
import org.squiddev.cobalt.function.TwoArgFunction;

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

    public interface TwoArgFunctionType {
        public LuaValue call(LuaState state, LuaValue arg, LuaValue arg2) throws LuaError, UnwindThrowable;
    }

    public static TwoArgFunction twoArgFunctionFactory(TwoArgFunctionType func) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1, LuaValue arg2) throws LuaError, UnwindThrowable {
                return func.call(state, arg1, arg2);
            }
        };
    }

    public interface OneArgFunctionType {
        public LuaValue call(LuaState state, LuaValue arg) throws LuaError, UnwindThrowable;
    }

    public static OneArgFunction oneArgFunctionFactory(OneArgFunctionType func) {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1) throws LuaError, UnwindThrowable {
                return func.call(state, arg1);
            }
        };
    }
}
