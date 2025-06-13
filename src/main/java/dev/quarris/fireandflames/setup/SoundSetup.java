package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundSetup {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, ModRef.ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SMITHING_HIT = REGISTRY.register("smithing_hit", () -> SoundEvent.createVariableRangeEvent(ModRef.res("smithing_hit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SMITHING_HIT_FAIL = REGISTRY.register("smithing_fail", () -> SoundEvent.createVariableRangeEvent(ModRef.res("smithing_fail")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SMITHING_FINAL = REGISTRY.register("smithing_final", () -> SoundEvent.createVariableRangeEvent(ModRef.res("smithing_final")));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }

}
