package eatAWESOME.pixelmonspawnalerts.capabilities;

import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityHandler {
    public static void register() {
        CapabilityManager.INSTANCE.register(ISpawnAlertData.class, new SpawnAlertDataStorage(), SpawnAlertData::new);
    }
}
