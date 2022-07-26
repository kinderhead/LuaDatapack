package mod.kinderhead.luadatapack.lua.api;

import java.util.ArrayList;
import java.util.List;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.ErrorFactory;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.ValueFactory;
import org.squiddev.cobalt.lib.Bit32Lib;
import org.squiddev.cobalt.lib.LuaLibrary;
import org.squiddev.cobalt.lib.MathLib;
import org.squiddev.cobalt.lib.StringLib;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import mod.kinderhead.luadatapack.LuaCommand;
import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.datapack.Scripts;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import mod.kinderhead.util.Out;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class LuastdLib implements LuaLibrary {
    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        env.rawset("require", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            String name = arg1.checkString();

            switch (name) {
                case "std:math":
                    state.getMainThread().getfenv().load(state, new MathLib());
                    break;
                
                case "std:string":
                    state.getMainThread().getfenv().load(state, new StringLib());
                    break;

                case "std:bit32":
                    state.getMainThread().getfenv().load(state, new Bit32Lib());
                    break;
            
                default:
                    break;
            }

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

        env.rawset("get_blockentity", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);
            BlockEntity block = source.getWorld().getBlockEntity(new BlockPos(MCLuaFactory.toVec(arg1)));
            LuaTable table = new LuaTable();

            if (block == null) {
                return Constants.NIL;
            }

            table.rawset("id", ValueFactory.valueOf(BlockEntityType.getId(block.getType()).toString()));
            
            if (block instanceof Inventory) {
                table.rawset("inventory", MCLuaFactory.get((Inventory) block));
            } else {
                table.rawset("inventory", Constants.NIL);
            }

            return table;
        }));

        env.rawset("set_block", LuaUtils.twoArgFunctionFactory((s, arg1, arg2) -> {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            LuaDatapack.SERVER.getCommandManager().execute(source, "setblock " + String.valueOf(arg1.checkTable().rawget("x").checkInteger()) + " " + String.valueOf(arg1.checkTable().rawget("y").checkInteger()) + " " + String.valueOf(arg1.checkTable().rawget("z").checkInteger()) + " " + arg2.checkString() + " replace");
            return Constants.NIL;
        }));

        env.rawset("run", LuaUtils.varArgFunctionFactory((s, args) -> {
            LuaTable _G = state.getCurrentThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);

            String code = Scripts.get(new Identifier(args.first().checkString()));

            if (code == null) {
                throw new LuaError(args.first().checkString() + " is not a valid script");
            }

            ArrayList<String> list = new ArrayList<>();

            for (int i = 1; i < args.count() + 1; i++) {
                if (i == 1) {
                    continue;
                }

                list.add(args.arg(i).checkString());
            }

            var ret = new Out<LuaValue>();
            LuaCommand.exec(code, args.first().checkString(), source, list.toArray(new String[list.size()]), ret);

            return ret.Get();
        }));

        return env;
    }
}
