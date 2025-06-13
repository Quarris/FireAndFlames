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

public record ArtisanTableSetOutputSelectionS2CPayload(int outputSelection) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("artisan_table_set_output_selection");
    public static final Type<ArtisanTableSetOutputSelectionS2CPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtisanTableSetOutputSelectionS2CPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ArtisanTableSetOutputSelectionS2CPayload::outputSelection,
        ArtisanTableSetOutputSelectionS2CPayload::new
    );

    @Override
    public Type<ArtisanTableSetOutputSelectionS2CPayload> type() {
        return TYPE;
    }

    public static void handle(ArtisanTableSetOutputSelectionS2CPayload payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof ArtisanTableMenu menu) {
            menu.selectOutput(payload.outputSelection);
        }
    }
}
