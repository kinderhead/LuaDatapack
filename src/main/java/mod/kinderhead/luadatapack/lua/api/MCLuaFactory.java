package mod.kinderhead.luadatapack.lua.api;

import static org.squiddev.cobalt.ValueFactory.valueOf;

import java.util.UUID;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaUserdata;
import org.squiddev.cobalt.LuaValue;

import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

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

    public static LuaValue get(Vec2f vec) {
        LuaTable table = new LuaTable();

        table.rawset("x", valueOf(vec.x));
        table.rawset("y", valueOf(vec.y));

        return table;
    }

    public static Vec3i toVec3i(LuaValue val) {
        try {
            return new Vec3i(val.checkTable().rawget("x").checkInteger(), val.checkTable().rawget("y").checkInteger(), val.checkTable().rawget("z").checkInteger());
        } catch (LuaError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Vec3d toVec3d(LuaValue val) {
        try {
            return new Vec3d(val.checkTable().rawget("x").checkDouble(), val.checkTable().rawget("y").checkDouble(), val.checkTable().rawget("z").checkDouble());
        } catch (LuaError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Vec2f toVec2f(LuaValue val) {
        try {
            return new Vec2f((float)val.checkTable().rawget("x").checkDouble(), (float)val.checkTable().rawget("y").checkDouble());
        } catch (LuaError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LuaValue get(Entity entity) {
        LuaTable table = new LuaTable();

        table.rawset("get_pos", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            Entity self = toEntity(arg1);
            return get(self.getPos());
        }));

        table.rawset("set_pos", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Entity self = toEntity(arg1);
            Vec3d pos = toVec3d(arg2);
            self.refreshPositionAndAngles(pos.x, pos.y, pos.z, self.getYaw(), self.getPitch());
            self.setHeadYaw(self.getYaw());
            return null;
        }));

        table.rawset("get_rot", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            Entity self = toEntity(arg1);
            return get(new Vec2f(self.getYaw(), self.getPitch()));
        }));

        table.rawset("set_rot", LuaUtils.twoArgFunctionFactory((state, arg1, arg2) -> {
            Entity self = toEntity(arg1);
            Vec2f rot = toVec2f(arg2);
            self.refreshPositionAndAngles(self.getX(), self.getY(), self.getZ(), rot.x, rot.y);
            self.setHeadYaw(rot.x);
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
            self.readNbt(data.getNbt().copyFrom((NbtCompound) LuaUtils.getNbtFromLua(arg2)));
            self.setUuid(id);
            
            return Constants.NIL;
        }));

        table.rawset("add_effect", LuaUtils.varArgFunctionFactory((state, args) -> {
            Entity self = toEntity(args.first());

            if (self instanceof LivingEntity) ((LivingEntity)self).addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.get(new Identifier(args.arg(2).checkString())), args.arg(3).checkInteger()*20, args.arg(4).checkInteger()-1, false, args.arg(5).checkBoolean()));
            
            return Constants.NIL;
        }));

        table.rawset("clear_effects", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            Entity self = toEntity(arg1);
            
            if (self instanceof LivingEntity) ((LivingEntity)self).clearStatusEffects();

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

        table.rawset("clear", LuaUtils.oneArgFunctionFactory((state, arg1) -> {
            Inventory self = toInventory(arg1);
            self.clear();
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
        table.rawset("id", valueOf(Registries.ITEM.getId(stack.getItem()).toString()));
        table.rawset("nbt", LuaUtils.getFromNbt(stack.getNbt()));

        return table;
    }

    public static ItemStack toItemStack(LuaTable val) throws LuaError {
        ItemStack stack = new ItemStack(Registries.ITEM.get(new Identifier(val.rawget("id").checkString())), val.rawget("count").checkInteger());
        stack.setNbt((NbtCompound) LuaUtils.getNbtFromLua(val.rawget("nbt")));
        return stack;
    }

    public static Vec3d toVec3d(LuaTable val) throws LuaError {
        return new Vec3d(val.checkTable().rawget("x").checkDouble(), val.checkTable().rawget("y").checkDouble(), val.checkTable().rawget("z").checkDouble());
    }
}
