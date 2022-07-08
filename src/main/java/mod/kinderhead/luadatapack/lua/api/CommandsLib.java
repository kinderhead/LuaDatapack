package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.function.LibFunction;
import org.squiddev.cobalt.function.OneArgFunction;
import org.squiddev.cobalt.lib.LuaLibrary;

import mod.kinderhead.luadatapack.LuaDatapack;

public class CommandsLib implements LuaLibrary {
    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        // One args
        LibFunction.bind(env, CommandsLib1::new, new String[]{"say"});

        return env;
    }

    private static final class CommandsLib1 extends OneArgFunction {
        @Override
        public LuaValue call(LuaState state, LuaValue arg) throws LuaError, UnwindThrowable {
            LuaTable _G = state.getCurrentThread().getfenv();
            switch (opcode) {
                case 0:
                    LuaDatapack.SERVER.getCommandManager().execute(LuaDatapack.SERVER.getCommandSource(), "say " + arg.checkString().strip());
                    break;
            
                default:
                    break;
            }

            return null;
        }
    }
}
