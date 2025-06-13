package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MaterialCastingRecipeSerializer<T extends MaterialCastingRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public MaterialCastingRecipeSerializer(Factory<T> factory, boolean consumesInput) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IItemOutput.CODEC.fieldOf("result").forGetter(MaterialCastingRecipe::getOutput),
            INumberProvider.CODEC.fieldOf("units").forGetter(MaterialCastingRecipe::getFluidUnits),
            Ingredient.CODEC.optionalFieldOf("ingredient", Ingredient.EMPTY).forGetter(MaterialCastingRecipe::getItemInput),
            Codec.INT.optionalFieldOf("cooling_time", 100).forGetter(MaterialCastingRecipe::getCoolingTime),
            Codec.BOOL.optionalFieldOf("consumes_input", consumesInput).forGetter(MaterialCastingRecipe::consumesInput),
            Codec.BOOL.optionalFieldOf("should_move_item", false).forGetter(MaterialCastingRecipe::shouldMoveItem)
        ).apply(instance, factory::create));

        this.streamCodec = StreamCodec.composite(
            IItemOutput.STREAM_CODEC, MaterialCastingRecipe::getOutput,
            INumberProvider.STREAM_CODEC, MaterialCastingRecipe::getFluidUnits,
            Ingredient.CONTENTS_STREAM_CODEC, MaterialCastingRecipe::getItemInput,
            ByteBufCodecs.INT, MaterialCastingRecipe::getCoolingTime,
            ByteBufCodecs.BOOL, MaterialCastingRecipe::consumesInput,
            ByteBufCodecs.BOOL, MaterialCastingRecipe::shouldMoveItem,
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
    public interface Factory<T extends MaterialCastingRecipe> {
        T create(IItemOutput result, INumberProvider fluidUnits, Ingredient itemInput, int coolingTime, boolean consumeInput, boolean moveItem);
    }
}
