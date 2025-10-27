package mod.kinderhead.luadatapack.lua.api;

import java.util.HashMap;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaValue;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.LuaUtils;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class StorageState extends PersistentState {
    public HashMap<Identifier, LuaValue> data = new HashMap<>();

    static PersistentState.Type<StorageState> type = new PersistentState.Type<StorageState>(
        StorageState::new,
        StorageState::create,
        DataFixTypes.LEVEL
    );

    public static StorageState create(NbtCompound nbt) {
        LuaDatapack.LOGGER.info("Loading storage");

        StorageState state = new StorageState();

        for (var i : nbt.getKeys()) {
            state.data.put(new Identifier(i), LuaUtils.getFromNbt(nbt.get(i)));
        }

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        LuaDatapack.LOGGER.info("Saving storage");
        for (var i : data.keySet()) {
            try {
                nbt.put(i.toString(), LuaUtils.getNbtFromLua(data.get(i)));
            } catch (LuaError e) {
                LuaDatapack.LOGGER.error("Could not convert lua object to nbt. Will not save object", e);
            }
        }
        return nbt;
    }

    public static StorageState get() {
        PersistentStateManager manager = LuaDatapack.SERVER.getWorld(World.OVERWORLD).getPersistentStateManager();

        return manager.getOrCreate(StorageState.type, "luadatapack");
    }
}
