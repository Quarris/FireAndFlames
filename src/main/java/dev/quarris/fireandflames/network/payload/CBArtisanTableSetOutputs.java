package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.crafting.ArtisanRecipeOutput;
import dev.quarris.fireandflames.world.inventory.menu.ArtisanTableMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record CBArtisanTableSetOutputs(List<ArtisanRecipeOutput> outputs) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("artisan_table_set_outputs");
    public static final Type<CBArtisanTableSetOutputs> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CBArtisanTableSetOutputs> CODEC = StreamCodec.composite(
        ArtisanRecipeOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), CBArtisanTableSetOutputs::outputs,
        CBArtisanTableSetOutputs::new
    );

    @Override
    public Type<CBArtisanTableSetOutputs> type() {
        return TYPE;
    }

    public static void handle(CBArtisanTableSetOutputs payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof ArtisanTableMenu menu) {
            menu.setPossibleOutputs(payload.outputs());
        }
    }
}
