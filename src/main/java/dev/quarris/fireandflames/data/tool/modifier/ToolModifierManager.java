package dev.quarris.fireandflames.data.tool.modifier;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Stream;

public class ToolModifierManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<ResourceLocation, ToolModifier> modifiers = ImmutableMap.of();

    public ToolModifierManager() {
        super(GSON, Registries.elementsDirPath(RegistrySetup.Keys.TOOL_MODIFIERS));
    }

    protected void apply(Map<ResourceLocation, JsonElement> modifierJsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, ToolModifier> mapBuilder = ImmutableMap.builder();
        RegistryOps<JsonElement> regOps = this.makeConditionalOps();

        for (Map.Entry<ResourceLocation, JsonElement> entry : modifierJsonMap.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (id.getPath().startsWith("_")) {
                continue;
            }

            try {
                var decoded = ToolModifier.CODEC.parse(regOps, entry.getValue()).getOrThrow(JsonParseException::new);
                mapBuilder.put(id, decoded);
            } catch (IllegalArgumentException | JsonParseException ex) {
                LOGGER.error("Parsing error loading modifier {}", id, ex);
            }
        }

        this.modifiers = mapBuilder.build();
        LOGGER.info("Loaded {} tool modifiers", this.modifiers.size());
    }

    public ToolModifier byKey(ResourceLocation recipeId) {
        return this.modifiers.get(recipeId);
    }

    public Map<ResourceLocation, ToolModifier> getAllModifiers() {
        return ImmutableMap.copyOf(this.modifiers);
    }

    public Collection<ToolModifier> getModifiers() {
        return this.modifiers.values();
    }

    public Stream<ResourceLocation> getModifierIds() {
        return this.modifiers.keySet().stream();
    }

    @VisibleForTesting
    protected static ToolModifier fromJson(ResourceLocation recipeId, JsonObject json, HolderLookup.Provider registries) {
        return ToolModifier.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
    }

    public void replaceModifiers(Map<ResourceLocation, ToolModifier> modifiers) {
        this.modifiers = modifiers;
    }
}
