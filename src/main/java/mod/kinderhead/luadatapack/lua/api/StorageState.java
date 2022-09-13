package mod.kinderhead.luadatapack.lua.api;

import java.util.HashMap;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaValue;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;

public class StorageState extends PersistentState {
    public HashMap<Identifier, LuaValue> data = new HashMap<>();

    public StorageState() {
        LuaDatapack.LOGGER.info("Loading storage");
    }

    public StorageState(NbtCompound nbt) {
        LuaDatapack.LOGGER.info("Loading storage");
        for (var i : nbt.getKeys()) {
            data.put(new Identifier(i), LuaUtils.getFromNbt(nbt.get(i)));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        LuaDatapack.LOGGER.info("Saving storage");
        for (var i : StorageLib.data.keySet()) {
            try {
                nbt.put(i.toString(), LuaUtils.getFromLua(data.get(i)));
            } catch (LuaError e) {
                LuaDatapack.LOGGER.error("Could not convert lua object to nbt. Will not save object", e);
            }
        }
        return nbt;
    }
}
