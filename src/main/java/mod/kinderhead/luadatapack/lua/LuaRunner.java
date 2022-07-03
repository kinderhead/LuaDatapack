package mod.kinderhead.luadatapack.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;

public class LuaRunner {
    public static void Run(String code) {
        Run(code, "main");
    }

    public static void Run(String code, String name) {
        Globals globals = new Globals();
        globals.load(new BaseLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new MathLib());

        LoadState.install(globals);
        LuaC.install(globals);

        LuaValue chunk = globals.load(code, name, globals);
        chunk.call();
    }
}
