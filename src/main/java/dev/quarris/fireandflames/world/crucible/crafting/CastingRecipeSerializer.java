package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CastingRecipeSerializer<T extends CastingRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CastingRecipeSerializer(Factory<T> factory, boolean consumesInput) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IItemOutput.CODEC.fieldOf("result").forGetter(CastingRecipe::getOutput),
            FluidInput.CODEC.fieldOf("fluid").forGetter(CastingRecipe::getFluidInput),
            Ingredient.CODEC.optionalFieldOf("ingredient", Ingredient.EMPTY).forGetter(CastingRecipe::getItemInput),
            Codec.INT.optionalFieldOf("cooling_time", 100).forGetter(CastingRecipe::getCoolingTime),
            Codec.BOOL.optionalFieldOf("consumes_item", consumesInput).forGetter(CastingRecipe::consumesItem)
        ).apply(instance, factory::create));

        this.streamCodec = StreamCodec.composite(
            IItemOutput.STREAM_CODEC, CastingRecipe::getOutput,
            FluidInput.STREAM_CODEC, CastingRecipe::getFluidInput,
            Ingredient.CONTENTS_STREAM_CODEC, CastingRecipe::getItemInput,
            ByteBufCodecs.INT, CastingRecipe::getCoolingTime,
            ByteBufCodecs.BOOL, CastingRecipe::consumesItem,
            factory::create);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    @FunctionalInterface
    public interface Factory<T extends CastingRecipe> {
        T create(IItemOutput result, FluidInput fluidInput, Ingredient itemInput, int coolingTime, boolean consumeItem/*, boolean copyData*/);
    }
}
