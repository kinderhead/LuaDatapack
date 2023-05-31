package mod.kinderhead.luadatapack.datapack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mod.kinderhead.luadatapack.LuaDatapack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Scripts {
    private static Map<String, Project> projects = new HashMap<String, Project>();

    public static String get(Identifier id) {
        if (projects.containsKey(id.getNamespace()) && projects.get(id.getNamespace()).has(id)) {
            return projects.get(id.getNamespace()).get(id);
        }

        LuaDatapack.LOGGER.error("Could not find file with id " + id.toString());
        return null;
    }

    public static String getImportable(Identifier id) {
        if (projects.containsKey(id.getNamespace()) && projects.get(id.getNamespace()).getImportableIds().contains(id)) {
            return projects.get(id.getNamespace()).get(id);
        }

        LuaDatapack.LOGGER.error("Could not find importable file with id " + id.toString() + ". Check project configuration if this is a mistake");
        return null;
    }

    public static void set(Identifier id, String code) {
        if (!projects.containsKey(id.getNamespace())) projects.put(id.getNamespace(), new Project(id.getNamespace()));
        projects.get(id.getNamespace()).put(id, code);
    }

    public static void clear() {
        projects.clear();
    }

    public static Iterable<Identifier> idSet() {
        ArrayList<Identifier> ids = new ArrayList<>();

        for (Project i : projects.values()) {
            ids.addAll(i.getCallableIds());
        }

        return ids;
    }

    public static void initAll() {
        for (Project i : projects.values()) {
            if (!i.init()) {
                LuaDatapack.SERVER.getPlayerManager().broadcast(Text.translatable("text.luadatapack.project_error", i.name), false);
            }
        }
    }
}
