package mod.kinderhead.luadatapack.lua;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
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

public class LuaRunner {
    /**
     * Returns true if the code executed successfully
     * 
     * @param code
     * @return exit code
     */
    public static boolean Run(String code) {
        return Run(code, "main");
    }

    /**
     * Returns true if the code executed successfully
     * 
     * @param code
     * @param name
     * @return exit code
     */
    public static boolean Run(String code, String name) {
        LuaState state = LuaState.builder().build();

        LuaTable _G = new LuaTable();
        state.setupThread(_G);

        _G.load( state, new BaseLib() );
        _G.load( state, new TableLib() );
        _G.load( state, new StringLib() );
        _G.load( state, new MathLib() );
        _G.load( state, new Bit32Lib() );
        _G.load( state, new Utf8Lib() );

        _G.rawset("print", Constants.NIL);
        _G.rawset("dofile", Constants.NIL);
        _G.rawset("loadfile", Constants.NIL);
        try {
            LoadState.load(state, new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), name, _G).call(state);
            return true;
        } catch (LuaError | IOException | CompileException | UnwindThrowable e) {
            LuaDatapack.LOGGER.error("Could not execute script \"" + name + "\"", e);
            return false;
        }
    }
}
