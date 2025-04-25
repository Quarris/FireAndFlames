package dev.quarris.fireandflames.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.quarris.fireandflames.client.screen.CrucibleScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(
        method = "renderSlot",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;getCarried()Lnet/minecraft/world/item/ItemStack;")
    )
    private void fireandflames_modifySlotPosition(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci, @Local(ordinal = 1) LocalIntRef localYPos) {
        if ((Object) this instanceof CrucibleScreen screen) {
            localYPos.set(localYPos.get() - screen.getMenu().getScroll() * 18);
        }
    }

}
