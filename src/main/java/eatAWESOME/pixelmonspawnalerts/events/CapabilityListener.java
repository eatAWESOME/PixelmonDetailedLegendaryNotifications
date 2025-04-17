package eatAWESOME.pixelmonspawnalerts.events;

import eatAWESOME.pixelmonspawnalerts.PixelmonSpawnAlerts;
import eatAWESOME.pixelmonspawnalerts.capabilities.SpawnAlertDataProvider;
import eatAWESOME.pixelmonspawnalerts.utils.SpawnAlertTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PixelmonSpawnAlerts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityListener {

	public static final ResourceLocation spawnAlertLocation = new ResourceLocation(PixelmonSpawnAlerts.MOD_ID, "spawnAlert");

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(spawnAlertLocation, new SpawnAlertDataProvider());
        }
    }
    
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        player.getCapability(SpawnAlertDataProvider.spawnAlertLocation).ifPresent(data -> {
            SpawnAlertTracker.updateSpawnAlert(player.getUUID(), data.getSpawnAlert());
        });
    }
    
    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SpawnAlertTracker.removeSpawnAlert(event.getPlayer().getUUID());
    }
}