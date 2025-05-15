package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.FluidSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FluidTagGen extends FluidTagsProvider {

    public FluidTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModRef.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pLookup) {
        this.tag(TagSetup.FluidTags.MOLTEN_IRON).add(FluidSetup.MOLTEN_IRON.getSource().get(), FluidSetup.MOLTEN_IRON.getFlowing().get());
        this.tag(TagSetup.FluidTags.MOLTEN_GOLD).add(FluidSetup.MOLTEN_GOLD.getSource().get(), FluidSetup.MOLTEN_GOLD.getFlowing().get());
        this.tag(TagSetup.FluidTags.MOLTEN_COPPER).add(FluidSetup.MOLTEN_COPPER.getSource().get(), FluidSetup.MOLTEN_COPPER.getFlowing().get());
        this.tag(TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS).add(FluidSetup.MOLTEN_ANCIENT_DEBRIS.getSource().get(), FluidSetup.MOLTEN_ANCIENT_DEBRIS.getFlowing().get());
        this.tag(TagSetup.FluidTags.MOLTEN_NETHERITE).add(FluidSetup.MOLTEN_NETHERITE.getSource().get(), FluidSetup.MOLTEN_NETHERITE.getFlowing().get());
    }

    private static ResourceKey<Fluid> key(Fluid block) {
        return ResourceKey.create(Registries.FLUID, BuiltInRegistries.FLUID.getKey(block));
    }
}
