package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.modifier.ToolModifier;
import dev.quarris.fireandflames.data.tool.modifier.effect.MiningSpeedEffect;
import dev.quarris.fireandflames.datagen.server.provider.ToolModifierProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ToolModifierGen extends ToolModifierProvider {

    public ToolModifierGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildModifiers(DataOutput dataOutput, HolderLookup.Provider registries) {
        dataOutput.accept(ModRef.res("haste"), new ToolModifier(List.of(new MiningSpeedEffect(2f, 0.5f))));
    }
}
