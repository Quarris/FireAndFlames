package dev.quarris.fireandflames.world.crucible.fuel;

import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.data.maps.FuelData;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

public class ItemHandlerFuelWrapper extends AbstractFuelProvider {

    private BlockCapabilityCache<IItemHandler, Direction> itemCache;

    public ItemHandlerFuelWrapper(ServerLevel level, BlockPos pos, Direction direction) {
        this.itemCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level, pos, direction);
    }

    public IItemHandler handler() {
        return this.itemCache.getCapability();
    }

    public boolean invalid() {
        return this.itemCache == null || this.handler() == null;
    }

    @Override
    public ActiveFuel burn(int baseTemp, IFuelConflictChecker checker) {
        if (this.invalid()) return ActiveFuel.EMPTY;

        int extractSlot = -1;
        int maxHeat = 0;
        for (int slot = 0; slot < this.handler().getSlots(); slot++) {
            ItemStack extracted = this.handler().extractItem(slot, 1, true);
            if (extracted.isEmpty() || checker.conflicts(new ItemActiveFuel(extracted))) continue;

            Optional<Integer> itemHeat = getItemHeat(extracted);
            if (itemHeat.isEmpty()) continue;

            int temperature = itemHeat.get();
            if (ServerConfigs.isHeatEnabled() && temperature < baseTemp) continue;

            if (temperature > maxHeat) {
                maxHeat = temperature;
                extractSlot = slot;
            }
        }

        if (extractSlot < 0) return ActiveFuel.EMPTY;

        ItemStack extracted = this.handler().extractItem(extractSlot, 1, false);
        if (extracted.isEmpty()) return ActiveFuel.EMPTY;

        return new ItemActiveFuel(extracted);
    }

    private static Optional<Integer> getItemHeat(ItemStack stack) {
        Optional<Integer> fuelHeatData = DataMapUtil.getFuelData(stack.getItem()).map(FuelData::heat);
        if (fuelHeatData.isEmpty()) {
            if (!ServerConfigs.useItemBurnValue()) return Optional.empty();
            return Optional.of(ServerConfigs.getItemFuelHeat());
        }

        return fuelHeatData;
    }

    public static final class ItemActiveFuel extends ActiveFuel {

        private final ItemStack stack;

        public ItemActiveFuel(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int burnValue() {
            return DataMapUtil.getFuelData(this.stack().getItem())
                .map(FuelData::burnTicks)
                .orElse(this.stack().getBurnTime(null));
        }

        @Override
        public int heat() {
            return DataMapUtil.getFuelData(this.stack().getItem())
                .map(FuelData::heat)
                .orElse(800);
        }

        @Override
        public boolean matches(ActiveFuel other) {
            if (!(other instanceof ItemActiveFuel otherFuel)) {
                return false;
            }

            return ItemStack.isSameItemSameComponents(this.stack(), otherFuel.stack());
        }

        @Override
        public CompoundTag saveTo(CompoundTag tag, HolderLookup.Provider provider) {
            super.saveTo(tag, provider);

            if (!this.stack.isEmpty()) {
                tag.put("Item", this.stack.save(provider));
            }

            return tag;
        }

        @Override
        public String type() {
            return "Item";
        }

        public static ItemActiveFuel load(CompoundTag tag, HolderLookup.Provider provider) {
            ItemStack item = ItemStack.EMPTY;
            if (tag.contains("Item")) {
                item = ItemStack.parse(provider, tag.getCompound("Item")).orElse(ItemStack.EMPTY);
            }

            return new ItemActiveFuel(item);
        }

        public ItemStack stack() {
            return stack;
        }

        @Override
        public String toString() {
            return "ItemActiveFuel[" +
                "stack=" + stack + ']';
        }
    }
}
