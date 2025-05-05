package dev.quarris.fireandflames.world.crucible.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class EntityMeltingRecipeSerializer implements RecipeSerializer<EntityMeltingRecipe> {

    public static final MapCodec<EntityMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntityTypePredicate.CODEC.fieldOf("entity_predicate").forGetter(EntityMeltingRecipe::entityPredicate),
        Codec.BOOL.fieldOf("requires_fluid").forGetter(EntityMeltingRecipe::requiresFluid),
        IFluidOutput.CODEC.fieldOf("result").forGetter(EntityMeltingRecipe::result),
        Codec.FLOAT.fieldOf("chance").forGetter(EntityMeltingRecipe::chance)
    ).apply(instance, EntityMeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> STREAM_CODEC = StreamCodec.of(EntityMeltingRecipeSerializer::toNetwork, EntityMeltingRecipeSerializer::fromNetwork);

    private static void toNetwork(RegistryFriendlyByteBuf buffer, EntityMeltingRecipe recipe) {
        ByteBufCodecs.fromCodecWithRegistries(EntityTypePredicate.CODEC).encode(buffer, recipe.entityPredicate());
        buffer.writeBoolean(recipe.requiresFluid());
        ByteBufCodecs.fromCodec(IFluidOutput.CODEC).encode(buffer, recipe.result());
        buffer.writeFloat(recipe.chance());
    }

    private static EntityMeltingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        EntityTypePredicate entityPredicate = ByteBufCodecs.fromCodecWithRegistries(EntityTypePredicate.CODEC).decode(buffer);
        boolean requiresFluid = buffer.readBoolean();
        IFluidOutput result = ByteBufCodecs.fromCodec(IFluidOutput.CODEC).decode(buffer);
        float chance = buffer.readFloat();
        return new EntityMeltingRecipe(entityPredicate, requiresFluid, result, chance);
    }

    @Override
    public MapCodec<EntityMeltingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityMeltingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
