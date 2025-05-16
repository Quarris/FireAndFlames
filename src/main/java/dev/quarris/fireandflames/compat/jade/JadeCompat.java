package dev.quarris.fireandflames.compat.jade;

import dev.quarris.fireandflames.compat.CompatManager;
import dev.quarris.fireandflames.compat.IModCompat;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompat implements IModCompat, IWailaPlugin {

    public JadeCompat() {
        CompatManager.jade = this;
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerProgress(CrucibleProgressProvider.INSTANCE, CrucibleControllerBlockEntity.class);
        registration.registerBlockDataProvider(CrucibleHeatComponentProvider.INSTANCE, CrucibleControllerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerProgressClient(CrucibleProgressProvider.INSTANCE);
        registration.registerBlockComponent(CrucibleHeatComponentProvider.INSTANCE, CrucibleControllerBlock.class);
    }

    @Override
    public String modId() {
        return "jade";
    }
}
