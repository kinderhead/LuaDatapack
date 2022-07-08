package mod.kinderhead.luadatapack.datapack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import mod.kinderhead.luadatapack.LuaDatapack;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

public class ReloadListener implements IdentifiableResourceReloadListener, SynchronousResourceReloader {
    @Override
    public void reload(ResourceManager manager) {
        Scripts.clear();
        int count = 0;

        for(var i : manager.findResources("lua", (path) -> true).entrySet()) {
            try {
                String code = IOUtils.toString(i.getValue().getInputStream(), StandardCharsets.UTF_8);
                Scripts.set(new Identifier(i.getKey().getNamespace(), FilenameUtils.removeExtension(i.getKey().getPath().replaceFirst("lua/", ""))), code);
                count++;
            } catch (IOException e) {
                LuaDatapack.LOGGER.error("Error reading file " + i.getKey().toString(), e);
            }
        }

        LuaDatapack.LOGGER.info("Loaded " + String.valueOf(count) + " lua scripts");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("luadatapack", "lua");
    }
}
