package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.TableCastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class CastingTableBlockEntity extends CastingBlockEntity<TableCastingRecipe> {

    public CastingTableBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CASTING_TABLE.get(), pPos, pState);
    }

    @Override
    public RecipeType<TableCastingRecipe> getRecipeType() {
        return RecipeSetup.TABLE_CASTING_TYPE.get();
    }
}
