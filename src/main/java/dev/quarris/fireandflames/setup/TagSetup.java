package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TagSetup {

    public static class BlockTags {

        public static TagKey<Block> VALID_CRUCIBLE_BLOCKS = create(ModRef.res("valid_crucible_blocks"));

        private static TagKey<Block> create(ResourceLocation id) {
            return TagKey.create(Registries.BLOCK, id);
        }

    }

    public static class ItemTags {

        public static TagKey<Item> CASTS = create(ModRef.res("casts"));

        private static TagKey<Item> create(ResourceLocation id) {
            return TagKey.create(Registries.ITEM, id);
        }
    }

    public static class FluidTags {

        public static final TagKey<Fluid> MOLTEN_IRON = common("molten_iron");
        public static final TagKey<Fluid> MOLTEN_GOLD = common("molten_gold");
        public static final TagKey<Fluid> MOLTEN_COPPER = common("molten_copper");
        public static final TagKey<Fluid> MOLTEN_ANCIENT_DEBRIS = common("molten_ancient_debris");
        public static final TagKey<Fluid> MOLTEN_NETHERITE = common("molten_netherite");

        private static TagKey<Fluid> common(String name) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
