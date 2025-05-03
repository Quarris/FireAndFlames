package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class CastingRecipeSerializer<T extends CastingRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CastingRecipeSerializer(Factory<T> factory, boolean consumesInput) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("result").forGetter(CastingRecipe::getResult),
            Codec.mapPair(
                FluidIngredient.MAP_CODEC_NONEMPTY,
                Codec.INT.fieldOf("amount")
            ).fieldOf("fluid").forGetter(recipe -> Pair.of(recipe.fluidInput, recipe.fluidInputAmount)),
            Ingredient.CODEC.optionalFieldOf("ingredient", Ingredient.EMPTY).forGetter(CastingRecipe::getItemInput),
            Codec.INT.optionalFieldOf("cooling_time", 100).forGetter(CastingRecipe::getCoolingTime),
            Codec.BOOL.optionalFieldOf("consumes_item", consumesInput).forGetter(CastingRecipe::consumesItem)
        ).apply(instance, (ItemStack result, Pair<FluidIngredient, Integer> fluidInput, Ingredient itemInput, Integer coolingTime, Boolean consumeItem) ->
            factory.create(result, fluidInput.getFirst(), fluidInput.getSecond(), itemInput, coolingTime, consumeItem)
        ));

        this.streamCodec = StreamCodec.composite(
            ItemStack.STREAM_CODEC, CastingRecipe::getResult,
            FluidIngredient.STREAM_CODEC, CastingRecipe::getFluidInput,
            ByteBufCodecs.INT, CastingRecipe::getFluidInputAmount,
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
        T create(ItemStack result, FluidIngredient fluidInput, int fluidInputAmount, Ingredient itemInput, int coolingTime, boolean consumeItem/*, boolean copyData*/);
    }
}
