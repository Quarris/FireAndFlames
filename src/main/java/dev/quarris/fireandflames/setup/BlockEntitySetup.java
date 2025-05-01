package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.CrucibleDrainBlock;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import dev.quarris.fireandflames.world.block.entity.CrucibleDrainBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntitySetup {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModRef.ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleControllerBlockEntity>> CRUCIBLE_CONTROLLER =
        REGISTRY.register("crucible_controller",
            () -> BlockEntityType.Builder.of(
                CrucibleControllerBlockEntity::new,
                BlockSetup.CRUCIBLE_CONTROLLER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleDrainBlockEntity>> CRUCIBLE_DRAIN =
        REGISTRY.register("crucible_drain",
            () -> BlockEntityType.Builder.of(
                CrucibleDrainBlockEntity::new,
                BlockSetup.CRUCIBLE_DRAIN.get()
            ).build(null));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}
