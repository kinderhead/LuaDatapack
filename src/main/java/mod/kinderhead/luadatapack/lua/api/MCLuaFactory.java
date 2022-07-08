package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;

import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

import static org.squiddev.cobalt.ValueFactory.valueOf;

public class MCLuaFactory {
    public static LuaValue get(ServerCommandSource source) {
        LuaTable table = new LuaTable();

        table.rawset("position", LuaUtils.readonly(get(source.getPosition())));
        table.rawset("name", valueOf(source.getName()));
        
        if (source.getEntity() == null) {
            table.rawset("entity", Constants.NIL);
        }

        return LuaUtils.readonly(table);
    }

    public static LuaValue get(Vec3d vec) {
        LuaTable table = new LuaTable();

        table.rawset("x", valueOf(vec.x));
        table.rawset("y", valueOf(vec.y));
        table.rawset("z", valueOf(vec.z));

        return table;
    }
}
