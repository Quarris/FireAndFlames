package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.ToolMaterial;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MaterialSetup {

    public static final ResourceKey<ToolMaterial> WOOD = create(ModRef.res("wood"));
    public static final ResourceKey<ToolMaterial> STONE = create(ModRef.res("stone"));
    public static final ResourceKey<ToolMaterial> COPPER = create(ModRef.res("copper"));
    public static final ResourceKey<ToolMaterial> IRON = create(ModRef.res("iron"));
    public static final ResourceKey<ToolMaterial> GOLD = create(ModRef.res("gold"));

    public static ResourceKey<ToolMaterial> create(ResourceLocation id) {
        return ResourceKey.create(RegistrySetup.Keys.MATERIALS, id);
    }

}
