package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagSetup {

    public static class BlockTags {

        public static TagKey<Block> VALID_CRUCIBLE_BLOCKS = create(ModRef.res("valid_crucible_blocks"));

        private static TagKey<Block> create(ResourceLocation id) {
            return TagKey.create(Registries.BLOCK, id);
        }

    }

    public static class ItemTags {

        private static TagKey<Item> create(ResourceLocation id) {
            return TagKey.create(Registries.ITEM, id);
        }
    }
}
