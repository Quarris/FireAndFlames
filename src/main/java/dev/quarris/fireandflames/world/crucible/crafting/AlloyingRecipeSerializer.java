package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.FluidInput;
import dev.quarris.fireandflames.util.IFluidOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AlloyingRecipeSerializer implements RecipeSerializer<AlloyingRecipe> {

    public static final MapCodec<AlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidInput.CODEC.listOf().fieldOf("ingredients").forGetter(AlloyingRecipe::ingredients),
        IFluidOutput.CODEC.listOf().fieldOf("results").forGetter(AlloyingRecipe::results)
    ).apply(instance, AlloyingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> STREAM_CODEC = StreamCodec.composite(
        FluidInput.LIST_STREAM_CODEC, AlloyingRecipe::ingredients,
        IFluidOutput.LIST_STREAM_CODEC, AlloyingRecipe::results,
        AlloyingRecipe::new
    );

    @Override
    public MapCodec<AlloyingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
