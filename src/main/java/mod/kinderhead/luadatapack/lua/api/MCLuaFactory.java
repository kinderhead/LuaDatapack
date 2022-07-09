package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaUserdata;
import org.squiddev.cobalt.LuaValue;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

import static org.squiddev.cobalt.ValueFactory.valueOf;

public class MCLuaFactory {
    public static LuaValue get(ServerCommandSource source) {
        LuaTable table = new LuaTable();

        table.rawset("_obj", new LuaUserdata(source));
        table.rawset("position", LuaUtils.readonly(get(source.getPosition())));
        table.rawset("name", valueOf(source.getName()));
        //table.rawset("dimension", valueOf(source.getWorld()))
        
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
            return new Vec3d(val.checkTable().rawget("x").checkDouble(), val.checkTable().rawget("y").checkDouble(), val.checkTable().rawget("z").checkDouble());
        } catch (LuaError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LuaValue get(Entity entity) {
        LuaTable table = new LuaTable();

        table.rawset("get_pos", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Entity self = toEntity(arg1);
            return get(self.getPos());
        }));

        table.rawset("set_pos", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Entity self = toEntity(arg1);
            LuaDatapack.SERVER.getCommandManager().execute(LuaDatapack.SERVER.getCommandSource().withEntity(self), "tp " + arg2.checkTable().rawget("x").checkDouble() + " " + arg2.checkTable().rawget("y").checkDouble() + " " + arg2.checkTable().rawget("z").checkDouble());
            return null;
        }));

        table.rawset("get_air", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            return valueOf(toEntity(arg1).getAir());
        }));

        table.rawset("set_air", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            toEntity(arg1).setAir(arg2.checkInteger());
            return null;
        }));

        table.rawset("get_name", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            return valueOf(toEntity(arg1).getName().getString());
        }));

        // LivingEntity methods

        table.rawset("is_living_entity", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            LivingEntity self = toLivingEntity(arg1);
            if (self == null) {
                return valueOf(false);
            }
            return valueOf(true);
        }));

        table.rawset("get_hp", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            LivingEntity self = toLivingEntity(arg1);
            if (self == null) {
                return Constants.NIL;
            }
            return valueOf(self.getHealth());
        }));

        table.rawset("set_hp", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            LivingEntity self = toLivingEntity(arg1);
            if (self != null) {
                self.setHealth((float) arg2.checkDouble());
            }
            return Constants.NIL;
        }));

        table.rawset("get_armor", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            LivingEntity self = toLivingEntity(arg1);
            if (self == null) {
                return Constants.NIL;
            }
            return valueOf(self.getArmor());
        }));

        table.rawset("get_age", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            LivingEntity self = toLivingEntity(arg1);
            if (self == null) {
                return Constants.NIL;
            }
            return valueOf(self.age);
        }));

        table.rawset("set_age", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            LivingEntity self = toLivingEntity(arg1);
            if (self != null) {
                self.age = arg2.checkInteger();
            }
            return Constants.NIL;
        }));

        table.rawset("_obj", new LuaUserdata(entity));
        return table;
    }

    public static Entity toEntity(LuaValue val) throws LuaError {
        return ((LuaTable) val).rawget("_obj").checkUserdata(Entity.class);
    }

    public static LivingEntity toLivingEntity(LuaValue val) throws LuaError {
        return ((LuaTable) val).rawget("_obj").checkUserdata(LivingEntity.class);
    }
}
