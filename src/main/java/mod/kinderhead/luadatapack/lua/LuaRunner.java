package mod.kinderhead.luadatapack.lua;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.ValueFactory;
import org.squiddev.cobalt.compiler.LoadState;
import org.squiddev.cobalt.lib.BaseLib;
import org.squiddev.cobalt.lib.TableLib;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.api.LuastdLib;
import mod.kinderhead.util.Out;

public class LuaRunner {
    public static boolean run(String code) throws LuaError {
        return run(code, "main", new LuaTable());
    }

    public static boolean run(String code, String name) throws LuaError {
        return run(code, name, new LuaTable());
    }

    public static boolean run(String code, String name, LuaTable env) throws LuaError {
        return run(code, name, env, new Out<LuaValue>());
    }

    public static boolean run(String code, String name, LuaTable env, Out<LuaValue> ret) throws LuaError {
        LuaState state = LuaState.builder().build();

        LuaTable _G = env;
        state.setupThread(_G);

        _G.load(state, new BaseLib());
        _G.load(state, new TableLib());

        _G.rawset("dofile", Constants.NIL);
        _G.rawset("loadfile", Constants.NIL);

        // LuaDatapack api
        _G.load(state, new LuastdLib());

        _G.rawset("filename", ValueFactory.valueOf(name));

        try {
            ret.set(LoadState.load(state, new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), name, _G).call(state));
            return true;
        } catch (UnwindThrowable | Exception e) {
            LuaDatapack.LOGGER.error("Could not execute script \"" + name + "\"", e);
            return false;
        }
    }
}
