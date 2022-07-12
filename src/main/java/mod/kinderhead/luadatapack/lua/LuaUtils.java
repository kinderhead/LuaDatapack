package mod.kinderhead.luadatapack.lua;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.function.OneArgFunction;
import org.squiddev.cobalt.function.ThreeArgFunction;
import org.squiddev.cobalt.function.TwoArgFunction;

import mod.kinderhead.luadatapack.LuaDatapack;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import static org.squiddev.cobalt.ValueFactory.valueOf;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.squiddev.cobalt.Constants;

public class LuaUtils {
    public static LuaTable readonly(LuaValue t) {
        LuaTable proxy = new LuaTable();

        LuaTable metatable = new LuaTable();
        metatable.rawset("__index", t);
        metatable.rawset("__newindex", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1, LuaValue arg2, LuaValue arg3) throws LuaError, UnwindThrowable {
                throw new LuaError("Table is readonly");
            }
        });

        proxy.setMetatable(metatable);
        return proxy;
    }

    public interface ThreeArgFunctionType {
        public LuaValue call(LuaState state, LuaValue arg, LuaValue arg2, LuaValue arg3) throws LuaError, UnwindThrowable;
    }

    public static ThreeArgFunction threeArgFunctionFactory(ThreeArgFunctionType func) {
        return new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1, LuaValue arg2, LuaValue arg3) throws LuaError, UnwindThrowable {
                return func.call(state, arg1, arg2, arg3);
            }
        };
    }

    public interface TwoArgFunctionType {
        public LuaValue call(LuaState state, LuaValue arg, LuaValue arg2) throws LuaError, UnwindThrowable;
    }

    public static TwoArgFunction twoArgFunctionFactory(TwoArgFunctionType func) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1, LuaValue arg2) throws LuaError, UnwindThrowable {
                return func.call(state, arg1, arg2);
            }
        };
    }

    public interface OneArgFunctionType {
        public LuaValue call(LuaState state, LuaValue arg) throws LuaError, UnwindThrowable;
    }

    public static OneArgFunction oneArgFunctionFactory(OneArgFunctionType func) {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaState state, LuaValue arg1) throws LuaError, UnwindThrowable {
                return func.call(state, arg1);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static LuaValue getFromNbt(NbtElement thing) {
        if (thing instanceof AbstractNbtNumber) {
            return valueOf(((AbstractNbtNumber) thing).doubleValue());
        } else if (thing instanceof NbtString) {
            return valueOf(thing.asString());
        } else if (thing instanceof AbstractNbtList) {
            AbstractNbtList<?> list = (AbstractNbtList<?>)thing;
            LuaTable table = new LuaTable();

            for (int i = 0; i < list.size(); i++) {
                table.insert(0, getFromNbt(list.get(i)));
            }

            return table;
        } else if (thing instanceof NbtElement) {
            NbtCompound compound = (NbtCompound)thing;
            LuaTable table = new LuaTable();

            try {
                for (var i : ((Map<String, NbtElement>)NbtCompound.class.getDeclaredMethod("toMap").invoke(compound)).entrySet()) {
                    table.rawset(i.getKey(), getFromNbt(i.getValue()));
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                LuaDatapack.LOGGER.error("Error converting Nbt to lua", e);
                return Constants.NIL;
            }

            return table;
        }

        return Constants.NIL;
    }
}
