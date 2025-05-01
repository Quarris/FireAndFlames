package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class CrucibleRecipeSerializer implements RecipeSerializer<CrucibleRecipe> {

    public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(recipe -> recipe.category),
        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
        ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("byproduct", ItemStack.EMPTY).forGetter(recipe -> recipe.byproduct),
        IFluidRecipeOutput.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
        Codec.INT.fieldOf("smelting_time").orElse(200).forGetter(recipe -> recipe.smeltingTime)
    ).apply(instance, CrucibleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.of(CrucibleRecipeSerializer::toNetwork, CrucibleRecipeSerializer::fromNetwork);

    private static void toNetwork(RegistryFriendlyByteBuf buffer, CrucibleRecipe recipe) {
        buffer.writeUtf(recipe.group);
        buffer.writeEnum(recipe.category);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.byproduct);
        ByteBufCodecs.fromCodec(IFluidRecipeOutput.CODEC).encode(buffer, recipe.result);
        buffer.writeVarInt(recipe.smeltingTime);
    }

    private static CrucibleRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        CookingBookCategory category = buffer.readEnum(CookingBookCategory.class);
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ItemStack byproduct = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        IFluidRecipeOutput result = ByteBufCodecs.fromCodec(IFluidRecipeOutput.CODEC).decode(buffer);
        int smeltingTime = buffer.readVarInt();
        return new CrucibleRecipe(group, category, ingredient, byproduct, result, smeltingTime);
    }

    @Override
    public MapCodec<CrucibleRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
