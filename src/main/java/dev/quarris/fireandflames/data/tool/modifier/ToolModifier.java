package dev.quarris.fireandflames.data.tool.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.tool.modifier.effect.IToolEffect;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record ToolModifier(List<IToolEffect> effects) {

    public static final Codec<ToolModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistrySetup.TOOL_EFFECTS.byNameCodec().dispatch(IToolEffect::codec, Function.identity()).listOf().fieldOf("effects").forGetter(ToolModifier::effects)
    ).apply(instance, ToolModifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolModifier> STREAM_CODEC = StreamCodec.composite(
        IToolEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), ToolModifier::effects,
        ToolModifier::new
    );

    public void modifyItem(ItemStack stack) {
        this.effects().forEach(effect -> effect.modifyItem(stack));
    }
}
