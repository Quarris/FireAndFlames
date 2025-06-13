package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.inventory.crafting.CastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

public class CastingBasinBlockEntity extends CastingBlockEntity {

    public CastingBasinBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CASTING_BASIN.get(), pPos, pState);
    }

    @Override
    public RecipeHolder<? extends CastingRecipe> getRecipeFor(CastingRecipe.Input input) {
        return this.getLevel().getRecipeManager().getRecipeFor(RecipeSetup.BASIN_CASTING_TYPE.get(), new CastingRecipe.Input(input.fluid(), input.item()), this.getLevel()).orElse(null);
    }
}
