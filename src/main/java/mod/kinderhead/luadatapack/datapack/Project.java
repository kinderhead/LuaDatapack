package mod.kinderhead.luadatapack.datapack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.ValueFactory;

import mod.kinderhead.luadatapack.LuaDatapack;
import mod.kinderhead.luadatapack.lua.LuaRunner;
import mod.kinderhead.util.Out;
import net.minecraft.util.Identifier;

public class Project {
    private Map<Identifier, String> files = new HashMap<Identifier, String>();
    private List<Identifier> scripts = new ArrayList<>();
    private List<Identifier> exports = new ArrayList<>();

    public final String name;

    public Project(String name) {
        this.name = name;
    }

    public void put(Identifier id, String file) {
        files.put(id, file);
    }

    public String get(Identifier id) {
        return files.get(id);
    }

    public boolean has(Identifier id) {
        return files.containsKey(id);
    }

    public Collection<Identifier> getCallableIds() {
        return scripts;
    }

    public Collection<Identifier> getImportableIds() {
        return exports;
    }

    public boolean init() {
        scripts.clear();
        exports.clear();

        Identifier file;
        if (has(new Identifier(name, "_project"))) {
            file = new Identifier(name, "_project");
        } else {
            file = new Identifier("std:_default_project");
        }

        LuaTable env = new LuaTable();
        
        LuaTable files = new LuaTable();

        for (Identifier i : this.files.keySet()) {
            files.insert(0, ValueFactory.valueOf(i.toString()));
        }

        env.rawset("files", files);

        try {
            Out<LuaValue> out = new Out<>();

            boolean ret = LuaRunner.run(Scripts.get(file), file.toString(), env, out);

            try {
                LuaTable data = out.get().checkTable();

                if (!data.rawget("scripts").isNil()) {
                    LuaTable scripts = data.rawget("scripts").checkTable();
                    for (int i = 1; i < scripts.length() + 1; i++) {
                        this.scripts.add(new Identifier(scripts.rawget(i).checkString()));
                    }
                }

                if (!data.rawget("exports").isNil()) {
                    LuaTable exports = data.rawget("exports").checkTable();
                    for (int i = 1; i < exports.length() + 1; i++) {
                        this.exports.add(new Identifier(exports.rawget(i).checkString()));
                    }
                }

                if (!data.rawget("dependencies").isNil()) {
                    
                }
            } catch (LuaError e) {
                LuaDatapack.LOGGER.error("Error initializing project " + name + ". Invalid _project.lua format. Check documentation for proper format");
                return false;
            }

            return ret;
        } catch (LuaError e) {
            LuaDatapack.LOGGER.error("Error initializing project " + name, e);
            return false;
        }
    }
}
