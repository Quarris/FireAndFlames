package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CrucibleRecipeSerializer implements RecipeSerializer<CrucibleRecipe> {

    public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(CrucibleRecipe::group),
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CrucibleRecipe::ingredient),
        ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("byproduct", ItemStack.EMPTY).forGetter(CrucibleRecipe::byproduct),
        IFluidOutput.CODEC.fieldOf("result").forGetter(CrucibleRecipe::result),
        Codec.INT.fieldOf("smelting_time").orElse(200).forGetter(CrucibleRecipe::smeltingTime),
        Codec.INT.fieldOf("heat").orElse(800).forGetter(CrucibleRecipe::heat)
    ).apply(instance, CrucibleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, CrucibleRecipe::group,
        Ingredient.CONTENTS_STREAM_CODEC, CrucibleRecipe::ingredient,
        ItemStack.OPTIONAL_STREAM_CODEC, CrucibleRecipe::byproduct,
        IFluidOutput.STREAM_CODEC, CrucibleRecipe::result,
        ByteBufCodecs.INT, CrucibleRecipe::smeltingTime,
        ByteBufCodecs.INT, CrucibleRecipe::heat,
        CrucibleRecipe::new
    );

    @Override
    public MapCodec<CrucibleRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
