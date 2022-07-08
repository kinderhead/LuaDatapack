package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaUserdata;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.function.OneArgFunction;
import org.squiddev.cobalt.function.TwoArgFunction;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

import static org.squiddev.cobalt.ValueFactory.valueOf;

public class MCLuaFactory {
    public static LuaValue get(ServerCommandSource source) {
        LuaTable table = new LuaTable();

        table.rawset("_obj", new LuaUserdata(source));
        table.rawset("position", LuaUtils.readonly(get(source.getPosition())));
        table.rawset("name", valueOf(source.getName()));
        
        if (source.getEntity() == null) {
            table.rawset("entity", Constants.NIL);
        } else {
            table.rawset("entity", get(source.getEntity()));
        }

        return table;
    }

    public static LuaValue get(Vec3d vec) {
        LuaTable table = new LuaTable();

        table.rawset("x", valueOf(vec.x));
        table.rawset("y", valueOf(vec.y));
        table.rawset("z", valueOf(vec.z));

        return table;
    }

    public static Vec3d toVec(LuaValue val) {
        try {
            return new Vec3d(((LuaTable) val).rawget("x").checkDouble(), ((LuaTable) val).rawget("y").checkDouble(), ((LuaTable) val).rawget("z").checkDouble());
        } catch (LuaError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LuaValue get(Entity entity) {
        LuaTable table = new LuaTable();

        table.rawset("get_pos", new OneArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1) throws LuaError, UnwindThrowable {
                Entity self = toEntity(arg1);
                return get(self.getPos());
            }
        });

        table.rawset("set_pos", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1, LuaValue arg2) throws LuaError, UnwindThrowable {
                Entity self = toEntity(arg1);
                LuaDatapack.SERVER.getCommandManager().execute(LuaDatapack.SERVER.getCommandSource().withEntity(self), "tp " + ((LuaTable) arg2).rawget("x").checkDouble() + " " + ((LuaTable) arg2).rawget("y").checkDouble() + " " + ((LuaTable) arg2).rawget("z").checkDouble());
                return null;
            }
        });

        table.rawset("_obj", new LuaUserdata(entity));
        return table;
    }

    public static Entity toEntity(LuaValue val) throws LuaError {
        return ((LuaTable) val).rawget("_obj").checkUserdata(Entity.class);
    }
}
