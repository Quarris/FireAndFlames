package dev.quarris.fireandflames.world.inventory.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MaterialArtisanCraftingRecipeSerializer implements RecipeSerializer<MaterialArtisanCraftingRecipe> {

    public static final MapCodec<MaterialArtisanCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        INumberProvider.CODEC.fieldOf("units").forGetter(MaterialArtisanCraftingRecipe::units),
        IItemOutput.CODEC.fieldOf("result").forGetter(MaterialArtisanCraftingRecipe::result)
    ).apply(instance, MaterialArtisanCraftingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialArtisanCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
        INumberProvider.STREAM_CODEC, MaterialArtisanCraftingRecipe::units,
        IItemOutput.STREAM_CODEC, MaterialArtisanCraftingRecipe::result,
        MaterialArtisanCraftingRecipe::new
    );

    @Override
    public MapCodec<MaterialArtisanCraftingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MaterialArtisanCraftingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
