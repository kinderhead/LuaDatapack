package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.ErrorFactory;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.Varargs;
import org.squiddev.cobalt.function.LibFunction;
import org.squiddev.cobalt.function.OneArgFunction;
import org.squiddev.cobalt.function.VarArgFunction;
import org.squiddev.cobalt.function.ZeroArgFunction;
import org.squiddev.cobalt.lib.LuaLibrary;

import mod.kinderhead.luadatapack.LuaDatapack;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

public class CommandsLib implements LuaLibrary {
    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        // One args
        LibFunction.bind(env, CommandsLib1::new, new String[]{"say"});
        LibFunction.bind(env, CommandsLib0::new, new String[]{"reload", "seed"});
        LibFunction.bind(env, CommandsLibV::new, new String[]{"teleport", "tp"});

        return env;
    }

    private static final class CommandsLib0 extends ZeroArgFunction {
        @Override
        public LuaValue call(LuaState state) throws LuaError, UnwindThrowable {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            switch (opcode) {
                case 0:
                    LuaDatapack.SERVER.getCommandManager().execute(source, "reload");
                    break;
                
                case 1:
                    LuaDatapack.SERVER.getCommandManager().execute(source, "seed");

                    break;
                default:
                    break;
            }

            return null;
        }
    }

    private static final class CommandsLib1 extends OneArgFunction {
        @Override
        public LuaValue call(LuaState state, LuaValue arg) throws LuaError, UnwindThrowable {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            switch (opcode) {
                case 0:
                    LuaDatapack.SERVER.getCommandManager().execute(source, "say " + arg.toString().strip());
                    break;
            
                default:
                    break;
            }

            return null;
        }
    }

    private static final class CommandsLibV extends VarArgFunction {
        @Override
        public Varargs invoke(LuaState state, Varargs args) throws LuaError, UnwindThrowable {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            switch (opcode) {
                case 0:
                case 1:
                    Entity entity;
                    Vec3d pos;
                    if (args.count() == 0) {
                        ErrorFactory.argError(0, "Expected 1 or 2 arguments");
                    }
                    else if (args.count() == 1) {
                        entity = null;
                        pos = MCLuaFactory.toVec(args.arg(1));
                        LuaDatapack.SERVER.getCommandManager().execute(source, "tp " + String.valueOf(pos.x) + " " + String.valueOf(pos.y) + " " + String.valueOf(pos.z));
                    }
                    else {
                        entity = MCLuaFactory.toEntity(args.arg(1));
                        pos = MCLuaFactory.toVec(args.arg(2));
                        LuaDatapack.SERVER.getCommandManager().execute(source, "tp " + entity.getUuidAsString() + " " + String.valueOf(pos.x) + " " + String.valueOf(pos.y) + " " + String.valueOf(pos.z));
                    }
                    return Constants.NIL;
            
                default:
                    break;
            }
            return null;
        }
    }
}
