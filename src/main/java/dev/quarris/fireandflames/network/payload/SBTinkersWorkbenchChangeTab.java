package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.TinkersWorkbenchMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SBTinkersWorkbenchChangeTab(ResourceLocation tab) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("tinkers_workbench_change_tab");
    public static final Type<SBTinkersWorkbenchChangeTab> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SBTinkersWorkbenchChangeTab> CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, SBTinkersWorkbenchChangeTab::tab,
        SBTinkersWorkbenchChangeTab::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SBTinkersWorkbenchChangeTab payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof TinkersWorkbenchMenu menu) {
            menu.setTabByName(payload.tab);
        }
    }
}
