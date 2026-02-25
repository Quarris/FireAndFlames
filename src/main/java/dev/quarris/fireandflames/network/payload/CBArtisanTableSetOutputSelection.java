package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.ArtisanTableMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CBArtisanTableSetOutputSelection(int outputSelection) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("artisan_table_set_output_selection");
    public static final Type<CBArtisanTableSetOutputSelection> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CBArtisanTableSetOutputSelection> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, CBArtisanTableSetOutputSelection::outputSelection,
        CBArtisanTableSetOutputSelection::new
    );

    @Override
    public Type<CBArtisanTableSetOutputSelection> type() {
        return TYPE;
    }

    public static void handle(CBArtisanTableSetOutputSelection payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof ArtisanTableMenu menu) {
            menu.selectOutput(payload.outputSelection);
        }
    }
}
