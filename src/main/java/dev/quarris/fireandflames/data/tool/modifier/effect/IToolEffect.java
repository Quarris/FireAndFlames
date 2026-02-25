package dev.quarris.fireandflames.data.tool.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface IToolEffect {

    Codec<? extends IToolEffect> CODEC = RegistrySetup.TOOL_EFFECTS.byNameCodec().dispatch(IToolEffect::codec, Function.identity());

    StreamCodec<RegistryFriendlyByteBuf, IToolEffect> STREAM_CODEC = ByteBufCodecs.registry(RegistrySetup.Keys.TOOL_EFFECTS)
        .dispatch(IToolEffect::codec, c -> ByteBufCodecs.fromCodecWithRegistries(c.codec()));

    MapCodec<? extends IToolEffect> codec();

    void modifyItem(ItemStack stack);

}
