package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.crafting.ArtisanRecipeOutput;
import dev.quarris.fireandflames.world.inventory.menu.ArtisanTableMenu;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ArtisanTableSetOutputsS2CPayload(List<ArtisanRecipeOutput> outputs) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("artisan_table_set_outputs");
    public static final Type<ArtisanTableSetOutputsS2CPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtisanTableSetOutputsS2CPayload> CODEC = StreamCodec.composite(
        ArtisanRecipeOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), ArtisanTableSetOutputsS2CPayload::outputs,
        ArtisanTableSetOutputsS2CPayload::new
    );

    @Override
    public Type<ArtisanTableSetOutputsS2CPayload> type() {
        return TYPE;
    }

    public static void handle(ArtisanTableSetOutputsS2CPayload payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof ArtisanTableMenu menu) {
            menu.setPossibleOutputs(payload.outputs());
        }
    }
}
