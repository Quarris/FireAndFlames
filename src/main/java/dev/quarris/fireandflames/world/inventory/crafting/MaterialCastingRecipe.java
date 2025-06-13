package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class MaterialCastingRecipe extends CastingRecipe {

    public final IItemOutput result;
    private final INumberProvider fluidInputUnits;

    protected MaterialCastingRecipe(IItemOutput result, INumberProvider fluidInputUnits, Ingredient itemInput, int coolingTime, boolean consumeInput, boolean moveItem) {
        super(itemInput, coolingTime, consumeInput, moveItem);
        this.result = result;
        this.fluidInputUnits = fluidInputUnits;
    }

    @Override
    public int requiredContents(FluidStack stack, Level level) {
        return DataMapUtil.getConverters(stack, level.registryAccess()).stream().
            map(data -> data.converter().getCountForUnits(this.fluidInputUnits.evaluateInt()))
            .findAny().orElse(-1);
    }

    @Override
    public boolean matches(Input input, Level level) {
        if (!this.itemInput.test(input.item())) {
            return false;
        }

        return !DataMapUtil.getConverters(input.fluid(), level.registryAccess()).isEmpty();
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        ItemStack resultStack = this.result.createItemStack();
        if (!(resultStack.getItem() instanceof IMaterialHolder materialHolder)) {
            ModRef.LOGGER.warn("Tried creating a material casting output for a non-material holding item: {}", resultStack);
            return resultStack;
        }

        DataMapUtil.getConverters(input.fluid(), registries).stream().findAny()
            .ifPresentOrElse(
                data -> materialHolder.setMaterial(resultStack, data.material()),
                () -> ModRef.LOGGER.warn("Tried creating a material casting with input that does not have a registered conversion: {}", resultStack)
            );

        return resultStack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    public IItemOutput getOutput() {
        return this.result;
    }

    public INumberProvider getFluidUnits() {
        return this.fluidInputUnits;
    }
}
