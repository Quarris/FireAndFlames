package dev.quarris.fireandflames.datagen.server.provider;

import com.google.common.collect.Sets;
import dev.quarris.fireandflames.data.tool.modifier.ToolModifier;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class ToolModifierProvider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public ToolModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(RegistrySetup.Keys.TOOL_MODIFIERS);
        this.registries = registries;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(registries -> this.run(output, registries));
    }

    protected CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider registries) {
        final Set<ResourceLocation> set = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildModifiers(
            (id, modifier) -> {
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate modifier " + id);
                } else {
                    list.add(DataProvider.saveStable(output, registries, ToolModifier.CODEC, modifier, ToolModifierProvider.this.pathProvider.json(id)));
                }
            }, registries
        );
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    protected abstract void buildModifiers(DataOutput dataOutput, HolderLookup.Provider registries);

    @Override
    public final String getName() {
        return "Tool Modifiers";
    }

    public interface DataOutput {
        void accept(ResourceLocation id, ToolModifier modifier);
    }
}
