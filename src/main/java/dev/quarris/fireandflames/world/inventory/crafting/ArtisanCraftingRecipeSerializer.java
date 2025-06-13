package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ArtisanCraftingRecipeSerializer implements RecipeSerializer<ArtisanCraftingRecipe> {

    public static final MapCodec<ArtisanCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(ArtisanCraftingRecipe::group),
        ItemInput.CODEC.fieldOf("ingredient").forGetter(ArtisanCraftingRecipe::ingredient),
        IItemOutput.CODEC.fieldOf("result").forGetter(ArtisanCraftingRecipe::result),
        IItemOutput.CODEC.optionalFieldOf("byproduct", new IItemOutput.Stack(ItemStack.EMPTY)).forGetter(ArtisanCraftingRecipe::byproduct)
    ).apply(instance, ArtisanCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArtisanCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ArtisanCraftingRecipe::group,
        ItemInput.STREAM_CODEC, ArtisanCraftingRecipe::ingredient,
        IItemOutput.STREAM_CODEC, ArtisanCraftingRecipe::result,
        IItemOutput.STREAM_CODEC, ArtisanCraftingRecipe::byproduct,
        ArtisanCraftingRecipe::new
    );

    @Override
    public MapCodec<ArtisanCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ArtisanCraftingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
