package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.inventory.crafting.CastingRecipe;
import dev.quarris.fireandflames.world.inventory.crafting.TableMaterialCastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;

public class CastingTableBlockEntity extends CastingBlockEntity {

    public CastingTableBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CASTING_TABLE.get(), pPos, pState);
    }

    @Override
    public RecipeHolder<? extends CastingRecipe> getRecipeFor(CastingRecipe.Input input) {
        RecipeManager recipeManager = this.getLevel().getRecipeManager();
        RecipeHolder<TableMaterialCastingRecipe> materialRecipe = recipeManager.getRecipeFor(RecipeSetup.TABLE_MATERIAL_CASTING_TYPE.get(), input, this.getLevel()).orElse(null);
        if (materialRecipe != null) {
            return materialRecipe;
        }

        return recipeManager.getRecipeFor(RecipeSetup.TABLE_CASTING_TYPE.get(), input, this.getLevel()).orElse(null);
    }
}
