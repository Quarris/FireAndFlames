package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class CastingBasinBlockEntity extends CastingBlockEntity<BasinCastingRecipe> {

    public CastingBasinBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CASTING_BASIN.get(), pPos, pState);
    }

    @Override
    public RecipeType<BasinCastingRecipe> getRecipeType() {
        return RecipeSetup.BASIN_CASTING_TYPE.get();
    }
}
