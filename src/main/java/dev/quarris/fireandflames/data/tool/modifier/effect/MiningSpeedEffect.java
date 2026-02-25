package dev.quarris.fireandflames.data.tool.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public record MiningSpeedEffect(float modifier, float bonus) implements IToolEffect {

    public static final MapCodec<MiningSpeedEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("modifier", 1f).forGetter(MiningSpeedEffect::modifier),
        Codec.FLOAT.optionalFieldOf("bonus", 0f).forGetter(MiningSpeedEffect::bonus)
    ).apply(instance, MiningSpeedEffect::new));


    @Override
    public MapCodec<? extends IToolEffect> codec() {
        return CODEC;
    }

    @Override
    public void modifyItem(ItemStack stack) {

    }

    @EventBusSubscriber(modid = ModRef.ID)
    public static class EventHandler {
        @SubscribeEvent(priority = EventPriority.HIGH)
        private static void miningSpeed$breakSpeed(PlayerEvent.BreakSpeed event) {
            ItemStack heldItem = event.getEntity().getMainHandItem();
            if (!heldItem.has(DataComponentSetup.TOOL_DATA)) {
                return;
            }

            ToolData toolData = heldItem.get(DataComponentSetup.TOOL_DATA);

            toolData.modifiers().forEach(mod -> mod.effects().forEach(effect -> {
                if (effect instanceof MiningSpeedEffect(float modifier, float bonus)) {
                    event.setNewSpeed(event.getNewSpeed() * modifier + bonus);
                }
            }));
        }
    }
}
