package com.acikek.purpeille.world.reload;

import com.acikek.purpeille.Purpeille;
import com.acikek.purpeille.warpath.component.Component;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;
import java.util.function.BiFunction;

public class ComponentReloader<T extends Component> extends JsonDataLoader implements IdentifiableResourceReloadListener {

    public Identifier id;
    public Map<Identifier, T> registry;
    public BiFunction<JsonObject, Identifier, T> fromJson;

    public ComponentReloader(String type, Map<Identifier, T> registry, BiFunction<JsonObject, Identifier, T> fromJson) {
        super(new Gson(), type);
        id = Purpeille.id(type);
        this.registry = registry;
        this.fromJson = fromJson;
    }

    @Override
    public Identifier getFabricId() {
        return id;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        registry.clear();
        for (Map.Entry<Identifier, JsonElement> file : prepared.entrySet()) {
            JsonObject obj = file.getValue().getAsJsonObject();
            try {
                T component = fromJson.apply(obj, file.getKey());
                registry.put(file.getKey(), component);
            }
            catch (Exception e) {
                Purpeille.LOGGER.error(e);
            }
        }
    }
}
