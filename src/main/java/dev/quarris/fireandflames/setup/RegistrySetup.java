package dev.quarris.fireandflames.setup;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.data.tool.part.PartType;
import dev.quarris.fireandflames.data.tool.ToolMaterial;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.datagen.server.DamageTypeGen;
import dev.quarris.fireandflames.datagen.server.MaterialGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;

public class RegistrySetup {

    public static final Registry<MapCodec<? extends INumberProvider>> NUMBER_PROVIDERS = NumberProviderSetup.REGISTRY.makeRegistry(builder -> builder.defaultKey(NumberProviderSetup.CONSTANT.getKey()).sync(true));
    public static final Registry<PartType> PART_TYPES = PartTypeSetup.REGISTRY.makeRegistry(builder -> builder.defaultKey(PartTypeSetup.PICKAXE_HEAD.getKey()).sync(true));
    public static final Registry<ToolType<?>> TOOL_TYPES = ToolTypeSetup.REGISTRY.makeRegistry(builder -> builder.defaultKey(ToolTypeSetup.PICKAXE.getKey()).sync(true));

    public static final RegistrySetBuilder DATAPACK_REGISTRIES = new RegistrySetBuilder()
        .add(Registries.DAMAGE_TYPE, DamageTypeGen::bootstrap)
        .add(RegistrySetup.Keys.MATERIALS, MaterialGen::bootstrap);

    public static HolderLookup.Provider createLookup() {
        RegistryAccess.Frozen registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return DATAPACK_REGISTRIES.build(registryAccess);
    }

    public static void init(IEventBus modBus) {

    }

    public static class Keys {
        public static final ResourceKey<Registry<MapCodec<? extends INumberProvider>>> NUMBER_PROVIDERS = ResourceKey.createRegistryKey(ModRef.res("number_providers"));
        public static final ResourceKey<Registry<PartType>> PART_TYPES = ResourceKey.createRegistryKey(ModRef.res("part_types"));
        public static final ResourceKey<Registry<ToolType<?>>> TOOL_TYPES = ResourceKey.createRegistryKey(ModRef.res("tool_types"));

        public static final ResourceKey<Registry<ToolMaterial>> MATERIALS = ResourceKey.createRegistryKey(ModRef.res("materials"));
    }

}
