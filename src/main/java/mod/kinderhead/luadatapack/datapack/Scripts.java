package mod.kinderhead.luadatapack.datapack;

import java.util.HashMap;
import java.util.Map;

import mod.kinderhead.luadatapack.LuaDatapack;
import net.minecraft.util.Identifier;

public class Scripts {
    private static Map<Identifier, String> files = new HashMap<Identifier, String>();

    public static String get(Identifier id) {
        for (var i : files.entrySet()) {
            if (i.getKey().getNamespace().equals(id.getNamespace()) && i.getKey().getPath().equals(id.getPath())) {
                return i.getValue();
            }
        }

        LuaDatapack.LOGGER.error("Could not find file with id " + id.toString());
        return null;
    }

    public static void set(Identifier id, String code) {
        files.put(id, code);
    }

    public static void clear() {
        files.clear();
    }
}
