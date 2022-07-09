package mod.kinderhead.luadatapack.lua.api;

import java.util.List;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.ErrorFactory;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.ValueFactory;
import org.squiddev.cobalt.lib.LuaLibrary;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.datapack.Scripts;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class LuastdLib implements LuaLibrary {
    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        env.rawset("require", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            String name = arg1.checkString();

            String data = Scripts.get(new Identifier(name));
            if (data == null) {
                throw new LuaError("Could not find module " + name);
            } else {
                return env.rawget("loadstring").checkFunction().call(state, ValueFactory.valueOf(data)).checkFunction().call(state);
            }
        }));

        env.rawset("selector", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            List<? extends Entity> entities;
            try {
                EntitySelector sel = new EntitySelectorReader(new StringReader(arg1.checkString())).read();
                entities = sel.getEntities(source);
            } catch (CommandSyntaxException e) {
                throw ErrorFactory.argError(arg1, "Target selector");
            }
            LuaTable table = new LuaTable();
            for (Entity entity : entities) {
                table.insert(0, MCLuaFactory.get(entity));
            }
            return table;
        }));

        env.rawset("command", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);
            LuaDatapack.SERVER.getCommandManager().execute(source, arg1.checkString().strip());
            return Constants.NIL;
        }));

        env.rawset("get_block", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            String id = Registry.BLOCK.getId(source.getWorld().getBlockState(new BlockPos(MCLuaFactory.toVec(arg1))).getBlock()).toString();
            return ValueFactory.valueOf(id);
        }));

        return env;
    }
}
