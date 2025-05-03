package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleFawsitBlockEntity>> CRUCIBLE_FAWSIT =
        REGISTRY.register("crucible_fawsit",
            () -> BlockEntityType.Builder.of(
                CrucibleFawsitBlockEntity::new,
                BlockSetup.CRUCIBLE_FAWSIT.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingBasinBlockEntity>> CASTING_BASIN =
        REGISTRY.register("casting_basin",
            () -> BlockEntityType.Builder.of(
                CastingBasinBlockEntity::new,
                BlockSetup.CASTING_BASIN.get()
            ).build(null));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}
