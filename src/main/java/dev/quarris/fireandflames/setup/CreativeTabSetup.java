package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabSetup {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModRef.ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = REGISTRY.register("creative_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("creative_tabs.fireandflames.creative_tab"))
        .icon(() -> new ItemStack(BlockSetup.CRUCIBLE_CONTROLLER))
        .displayItems((pParams, pOutput) -> {
            ItemSetup.REGISTRY.getEntries().forEach(entry -> pOutput.accept(entry.get()));
            FluidSetup.REGISTRY.getBucketEntries().forEach(entry -> pOutput.accept(entry.get()));
        })
        .build());


    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}
