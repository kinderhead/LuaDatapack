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
import org.squiddev.cobalt.lib.Utf8Lib;

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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LuastdLib implements LuaLibrary {
    @Override
    public LuaValue add(LuaState state, LuaTable env) {
        LuaTable _G = state.getCurrentThread().getfenv();

        ServerCommandSource source;
        try {
            if (_G.rawget("src").isNil()) {
                source = null;
            } else {
                source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);
            }

            env.rawset("require", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
                String name = arg1.checkString();

                switch (name) {
                    case "std:math":
                        return LuaDatapack.DUMMY.getMainThread().getfenv().load(LuaDatapack.DUMMY, new MathLib());
                    
                    case "std:string":
                        return LuaDatapack.DUMMY.getMainThread().getfenv().load(LuaDatapack.DUMMY, new StringLib());

                    case "std:bit32":
                        return LuaDatapack.DUMMY.getMainThread().getfenv().load(LuaDatapack.DUMMY, new Bit32Lib());

                    case "std:utf8":
                        return LuaDatapack.DUMMY.getMainThread().getfenv().load(LuaDatapack.DUMMY, new Utf8Lib());
                    
                    case "std:commands":
                        return LuaDatapack.DUMMY.getMainThread().getfenv().load(LuaDatapack.DUMMY, new CommandsLib());
                    
                    case "std:storage":
                        return LuaDatapack.DUMMY.getMainThread().getfenv().load(LuaDatapack.DUMMY, new StorageLib());
                
                    default:
                        break;
                }
                
                String data;

                if (new Identifier(name).getNamespace().equals("std") || new Identifier(name).getNamespace().equals(new Identifier(env.rawget("filename").checkString()).getNamespace())) {
                    data = Scripts.get(new Identifier(name));
                } else {
                    data = Scripts.getImportable(new Identifier(name));
                }

                if (data == null) {
                    throw new LuaError("Could not find module " + name);
                } else {
                    return env.rawget("loadstring").checkFunction().call(state, ValueFactory.valueOf(data)).checkFunction().call(state);
                }
            }));

            env.rawset("selector", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
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
                LuaDatapack.executeCommand(source, arg1.checkString().strip());
                return Constants.NIL;
            }));

            env.rawset("get_block", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
                String id = Registries.BLOCK.getId(source.getWorld().getBlockState(new BlockPos(MCLuaFactory.toVeci(arg1))).getBlock()).toString();
                return ValueFactory.valueOf(id);
            }));

            env.rawset("get_blockentity", LuaUtils.oneArgFunctionFactory((s, arg1) -> {
                BlockEntity block = source.getWorld().getBlockEntity(new BlockPos(MCLuaFactory.toVeci(arg1)));
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
                LuaDatapack.executeCommand(source, "setblock " + String.valueOf(arg1.checkTable().rawget("x").checkInteger()) + " " + String.valueOf(arg1.checkTable().rawget("y").checkInteger()) + " " + String.valueOf(arg1.checkTable().rawget("z").checkInteger()) + " " + arg2.checkString() + " replace");
                return Constants.NIL;
            }));

            env.rawset("run", LuaUtils.varArgFunctionFactory((s, args) -> {
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

                return ret.get();
            }));

            env.rawset("print", LuaUtils.varArgFunctionFactory((s, args) -> {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.count() + 1; i++) {
                    if (args.arg(i).isTable()) {
                        builder.append(LuaUtils.getNbtFromLua(args.arg(i)).toString());
                    } else {
                        builder.append(args.arg(i).toString());
                    }
                    builder.append(" ");
                }
                builder.deleteCharAt(builder.length() - 1);
                LuaDatapack.SERVER.getPlayerManager().broadcast(Text.of(builder.toString()), false);
                return Constants.NIL;
            }));

            env.rawset("print_db", LuaUtils.varArgFunctionFactory((s, args) -> {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < args.count() + 1; i++) {
                    if (args.arg(i).isTable()) {
                        builder.append(LuaUtils.getNbtFromLua(args.arg(i)).toString());
                    } else {
                        builder.append(args.arg(i).toString());
                    }
                    builder.append(" ");
                }
                builder.deleteCharAt(builder.length() - 1);
                LuaDatapack.LOGGER.info(builder.toString());
                return Constants.NIL;
            }));

            LuaTable entity = new LuaTable();
            entity.rawset("new", LuaUtils.varArgFunctionFactory((s, args) -> {     
                NbtCompound nbt;
                if (args.count() == 3) {
                    nbt = (NbtCompound)LuaUtils.getNbtFromLua(args.arg(3)).copy();
                }
                else {
                    nbt = new NbtCompound();
                }
                nbt.putString("id", args.arg(1).checkString());

                Vec3d pos = MCLuaFactory.toVecd(args.arg(2));

                Entity ret = EntityType.loadEntityWithPassengers(nbt, source.getWorld(), e -> {
                    e.refreshPositionAndAngles(pos.x, pos.y, pos.z, e.getYaw(), e.getPitch());
                    return e;
                });

                if (ret instanceof MobEntity) {
                    ((MobEntity)ret).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(ret.getBlockPos()), SpawnReason.COMMAND, null, null);
                }

                source.getWorld().spawnEntityAndPassengers(ret);

                return MCLuaFactory.get(ret);
            }));
            env.rawset("Entity", entity);
        } catch (LuaError e) {
            e.printStackTrace();
        }

        return env;
    }
}
