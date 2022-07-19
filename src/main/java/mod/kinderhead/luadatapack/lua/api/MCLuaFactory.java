package mod.kinderhead.luadatapack.lua.api;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaUserdata;
import org.squiddev.cobalt.LuaValue;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import static org.squiddev.cobalt.ValueFactory.valueOf;

import java.util.UUID;

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

        table.rawset("get_nbt", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Entity self = toEntity(arg1);
            EntityDataObject data = new EntityDataObject(self);
            return LuaUtils.getFromNbt(data.getNbt().get(arg2.checkString()));
        }));

        table.rawset("merge_nbt", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Entity self = toEntity(arg1);
            EntityDataObject data = new EntityDataObject(self);
            
            // EntityDataObject does it this way
            UUID id = self.getUuid();
            self.readNbt(data.getNbt().copyFrom((NbtCompound) LuaUtils.getFromLua(arg2)));
            self.setUuid(id);
            
            return Constants.NIL;
        }));

        table.rawset("add_effect", LuaUtils.varArgFunctionFactory((state, args) -> {
            Entity self = toEntity(args.first());
            LuaTable _G = state.getMainThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);
            
            LuaDatapack.SERVER.getCommandManager().execute(source, "effect give " + self.getUuidAsString() + " " + args.arg(2).checkString() + " " + String.valueOf(args.arg(3).checkInteger()) + " " + String.valueOf(args.arg(4).checkInteger()) + " " + String.valueOf(args.arg(5).checkBoolean()));

            return Constants.NIL;
        }));

        table.rawset("clear_effects", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            Entity self = toEntity(arg1);
            LuaTable _G = state.getMainThread().getfenv();
            ServerCommandSource source = _G.rawget("src").checkTable().rawget("_obj").checkUserdata(ServerCommandSource.class);
            
            LuaDatapack.SERVER.getCommandManager().execute(source, "effect clear " + self.getUuidAsString());

            return Constants.NIL;
        }));

        LuaValue inv = Constants.NIL;
        LuaValue echest = Constants.NIL;
        if (entity instanceof Inventory) {
            inv = get((Inventory) entity);
            echest = get(((PlayerEntity) entity).getEnderChestInventory());
        }
        else if (entity instanceof PlayerEntity) {
            inv = get(((PlayerEntity) entity).getInventory());
        }
        table.rawset("inventory", inv);
        table.rawset("echest", echest);

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
        return val.checkTable().rawget("_obj").checkUserdata(Entity.class);
    }

    public static LivingEntity toLivingEntity(LuaValue val) throws LuaError {
        return val.checkTable().rawget("_obj").checkUserdata(LivingEntity.class);
    }

    public static LuaValue get(Inventory inventory) {
        LuaTable table = new LuaTable();

        table.rawset("size", valueOf(inventory.size()));

        table.rawset("get", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Inventory self = toInventory(arg1);
            return get(self.getStack(arg2.checkInteger()));
        }));

        table.rawset("set", LuaUtils.threeArgFunctionFactory((state, arg1, arg2, arg3) -> {
            Inventory self = toInventory(arg1);
            self.setStack(arg2.checkInteger(), toItemStack(arg3.checkTable()));
            return Constants.NIL;
        }));

        table.rawset("_obj", new LuaUserdata(inventory));
        return table;
    }

    public static Inventory toInventory(LuaValue val) throws LuaError {
        return val.checkTable().rawget("_obj").checkUserdata(Inventory.class);
    }

    public static LuaValue get(ItemStack stack) {
        LuaTable table = new LuaTable();

        table.rawset("count", valueOf(stack.getCount()));
        table.rawset("id", valueOf(Registry.ITEM.getId(stack.getItem()).toString()));
        table.rawset("nbt", LuaUtils.getFromNbt(stack.getNbt()));

        return table;
    }

    public static ItemStack toItemStack(LuaTable val) throws LuaError {
        ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier(val.rawget("id").checkString())), val.rawget("count").checkInteger());
        stack.setNbt((NbtCompound) LuaUtils.getFromLua(val.rawget("nbt")));
        return stack;
    }
}
