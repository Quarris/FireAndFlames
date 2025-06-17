package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantValue")
public record FamilyArtisanCraftingRecipe(
    String group,
    Ingredient ingredient,
    Map<BlockFamily.Variant, FamilyVariantOutput> variants
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item()) && BlockFamilies.getAllFamilies().anyMatch(family -> input.item().is(family.getBaseBlock().asItem()));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return BlockFamilies.getAllFamilies().map(family -> new ItemStack(family.getBaseBlock())).filter(stack -> input.item().is(stack.getItem())).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return BlockFamilies.getAllFamilies().map(family -> new ItemStack(family.getBaseBlock())).filter(this.ingredient::test).findFirst().orElse(ItemStack.EMPTY);
    }

    public List<ArtisanRecipeOutput> createResults(SingleRecipeInput input) {
        List<ArtisanRecipeOutput> outputs = new ArrayList<>();
        BlockFamilies.getAllFamilies().filter(family -> input.item().is(family.getBaseBlock().asItem())).findFirst().ifPresent(family -> {
            this.variants.forEach((variant, output) -> {
                Block block = family.get(variant);
                if (block == null) {
                    ModRef.LOGGER.warn("Invalid variant {} for family based off {}", variant, family.getBaseBlock());
                    return;
                }

                outputs.add(new ArtisanRecipeOutput(output.requires.evaluateInt(), new ItemStack(block, output.outputs.evaluateInt()), output.byproduct.createItemStack()));
            });
        });
        return outputs;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.FAMILY_ARTISAN_CRAFTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.FAMILY_ARTISAN_CRAFTING_TYPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public record FamilyVariantOutput(INumberProvider requires, INumberProvider outputs, IItemOutput byproduct) {

        public static final Codec<FamilyVariantOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            INumberProvider.CODEC.optionalFieldOf("requires", new ConstantNumber(1)).forGetter(FamilyVariantOutput::requires),
            INumberProvider.CODEC.fieldOf("outputs").forGetter(FamilyVariantOutput::outputs),
            IItemOutput.CODEC.optionalFieldOf("byproducts", new IItemOutput.Stack(ItemStack.EMPTY)).forGetter(FamilyVariantOutput::byproduct)
        ).apply(instance, FamilyVariantOutput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FamilyVariantOutput> STREAM_CODEC = StreamCodec.composite(
            INumberProvider.STREAM_CODEC, FamilyVariantOutput::requires,
            INumberProvider.STREAM_CODEC, FamilyVariantOutput::outputs,
            IItemOutput.STREAM_CODEC, FamilyVariantOutput::byproduct,
            FamilyVariantOutput::new
        );

    }
}
