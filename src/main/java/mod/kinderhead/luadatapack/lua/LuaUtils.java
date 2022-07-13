package mod.kinderhead.luadatapack.lua;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.UnwindThrowable;
import org.squiddev.cobalt.Varargs;
import org.squiddev.cobalt.function.OneArgFunction;
import org.squiddev.cobalt.function.ThreeArgFunction;
import org.squiddev.cobalt.function.TwoArgFunction;

import mod.kinderhead.luadatapack.LuaDatapack;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtString;

import static org.squiddev.cobalt.ValueFactory.valueOf;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.squiddev.cobalt.Constants;
import org.squiddev.cobalt.ErrorFactory;

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

    public static NbtElement getFromLua(LuaValue value) throws LuaError {
        if (value.isBoolean()) {
            return NbtByte.of((byte) (value.checkBoolean() ? 1 : 0));
        } else if (value.isInteger()) {
            return NbtInt.of(value.checkInteger());
        } else if (value.isLong()) {
            return NbtLong.of(value.checkLong());
        } else if (value.isNumber()) {
            return NbtDouble.of(value.checkDouble());
        } else if (value.isString()) {
            return NbtString.of(value.checkString());
        } else if (value.isTable()) {
            LuaTable table = value.checkTable();
            if (table.getArrayLength() == 0) {
                // Table
                NbtCompound dict = new NbtCompound();
                
                LuaValue k = Constants.NIL;
                while (true) {
                    Varargs n = table.next(k);
                    if ((k = n.first()).isNil())
                        break;
                    LuaValue v = n.arg(2);
                    dict.put(k.checkString(), getFromLua(v));
                }

                return dict;
            } else {
                // Array
                NbtList list = new NbtList();

                for (int i = 0; i < table.getArrayLength(); i++) {
                    list.add(getFromLua(table.rawget(i)));
                }

                return list;
            }
        }

        return NbtInt.of(0);
    }
}
