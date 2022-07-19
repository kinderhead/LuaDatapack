package mod.kinderhead.luadatapack.lua;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.compiler.CompileException;
import org.squiddev.cobalt.compiler.LoadState;
import org.squiddev.cobalt.lib.BaseLib;
import org.squiddev.cobalt.lib.Bit32Lib;
import org.squiddev.cobalt.lib.MathLib;
import org.squiddev.cobalt.lib.StringLib;
import org.squiddev.cobalt.lib.TableLib;
import org.squiddev.cobalt.lib.Utf8Lib;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.api.CommandsLib;
import mod.kinderhead.luadatapack.lua.api.LuastdLib;
import mod.kinderhead.util.Out;

public class LuaRunner {
    public static boolean Run(String code) throws LuaError {
        return Run(code, "main", new LuaTable());
    }

    public static boolean Run(String code, String name) throws LuaError {
        return Run(code, name, new LuaTable());
    }

    public static boolean Run(String code, String name, LuaTable env) throws LuaError {
        return Run(code, name, env, new Out<LuaValue>());
    }

    public static boolean Run(String code, String name, LuaTable env, Out<LuaValue> ret) throws LuaError {
        LuaState state = LuaState.builder().build();

        LuaTable _G = env;
        state.setupThread(_G);

        _G.load(state, new BaseLib());
        _G.load(state, new TableLib());
        _G.load(state, new StringLib());
        _G.load(state, new MathLib());
        _G.load(state, new Bit32Lib());
        _G.load(state, new Utf8Lib());

        _G.rawset("print", Constants.NIL);
        _G.rawset("dofile", Constants.NIL);
        _G.rawset("loadfile", Constants.NIL);

        // LuaDatapack api
        _G.load(state, new LuastdLib());
        _G.load(state, new CommandsLib());

        LuaDatapack.LOGGER.info("Running lua script: " + name);
        try {
            ret.Set(LoadState.load(state, new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), name, _G).call(state));
            return true;
        } catch (LuaError e) {
            LuaDatapack.LOGGER.error("Could not execute script \"" + name + "\"", e);
            return false;
        } catch (UnwindThrowable | IOException | CompileException e) {
            LuaDatapack.LOGGER.error("Could not execute script \"" + name + "\"", e);
            return false;
        } catch (Exception e) {
            LuaDatapack.LOGGER.error("Could not execute script \"" + name + "\"", e);
            return false;
        } 
    }
}
