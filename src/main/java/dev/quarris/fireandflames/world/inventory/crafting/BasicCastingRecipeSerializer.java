package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BasicCastingRecipeSerializer<T extends BasicCastingRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public BasicCastingRecipeSerializer(Factory<T> factory, boolean consumesInput) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IItemOutput.CODEC.fieldOf("result").forGetter(BasicCastingRecipe::getOutput),
            FluidInput.CODEC.fieldOf("fluid").forGetter(BasicCastingRecipe::getFluidInput),
            Ingredient.CODEC.optionalFieldOf("ingredient", Ingredient.EMPTY).forGetter(BasicCastingRecipe::getItemInput),
            Codec.INT.optionalFieldOf("cooling_time", 100).forGetter(BasicCastingRecipe::getCoolingTime),
            Codec.BOOL.optionalFieldOf("consumes_input", consumesInput).forGetter(BasicCastingRecipe::consumesInput),
            Codec.BOOL.optionalFieldOf("should_move_item", false).forGetter(BasicCastingRecipe::shouldMoveItem)
        ).apply(instance, factory::create));

        this.streamCodec = StreamCodec.composite(
            IItemOutput.STREAM_CODEC, BasicCastingRecipe::getOutput,
            FluidInput.STREAM_CODEC, BasicCastingRecipe::getFluidInput,
            Ingredient.CONTENTS_STREAM_CODEC, BasicCastingRecipe::getItemInput,
            ByteBufCodecs.INT, BasicCastingRecipe::getCoolingTime,
            ByteBufCodecs.BOOL, BasicCastingRecipe::consumesInput,
            ByteBufCodecs.BOOL, BasicCastingRecipe::shouldMoveItem,
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
        T create(IItemOutput result, FluidInput fluidInput, Ingredient itemInput, int coolingTime, boolean consumeInput, boolean moveItem);
    }
}
