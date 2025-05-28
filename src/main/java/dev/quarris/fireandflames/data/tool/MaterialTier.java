package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class MaterialTier implements Tier {

    @Override
    public int getUses() {
        return 1;
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public float getAttackDamageBonus() {
        return 0;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return TagKey.create(Registries.BLOCK, ModRef.res("empty"));
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of();
    }

    @Override
    public Tool createToolProperties(TagKey<Block> block) {
        return new Tool(List.of(), 0, 0);
    }
}
