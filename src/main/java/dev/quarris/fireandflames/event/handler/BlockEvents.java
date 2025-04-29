package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;

@EventBusSubscriber(modid = ModRef.ID)
public class BlockEvents {

    @SubscribeEvent
    public static void blockChange(BlockEvent.NeighborNotifyEvent event) {
        for (Map.Entry<BlockPos, CrucibleStructure.CrucibleShape> entry : CrucibleStructure.ALL_CRUCIBLES.entrySet()) {
            if (entry.getValue().containsAbove(event.getPos())) {
                event.getLevel().getBlockEntity(entry.getKey(), BlockEntitySetup.CRUCIBLE_CONTROLLER.get()).ifPresent(crucible -> {
                    crucible.getStructure().notifyChange((Level) event.getLevel(), event.getPos(), event.getState());
                });
            }
        }
    }

    @SubscribeEvent
    public static void worldStop(LevelEvent.Unload event) {
        CrucibleStructure.ALL_CRUCIBLES.clear();
    }
}
