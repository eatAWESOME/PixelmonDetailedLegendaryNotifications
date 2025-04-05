package eatAWESOME.pixelmondetailedlegendarynotifications;

import eatAWESOME.pixelmondetailedlegendarynotifications.events.LegendarySpawnListener;

import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PixelmonDetailedLegendaryNotifications.MOD_ID)
@Mod.EventBusSubscriber(modid = PixelmonDetailedLegendaryNotifications.MOD_ID)
public class PixelmonDetailedLegendaryNotifications {

	public static final String MOD_ID = "pixelmondetailedlegendarynotifications";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	private static PixelmonDetailedLegendaryNotifications instance;
	
    public PixelmonDetailedLegendaryNotifications() {
        instance = this;
    	reloadConfig();
        MinecraftForge.EVENT_BUS.register(this);
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(PixelmonDetailedLegendaryNotifications::onModLoad);
    }

    public static void onModLoad(FMLCommonSetupEvent event) {
    	Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());
    }
    
    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
    }

    public void reloadConfig() {
    }
    
    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event) {
    }
    
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
    }
    
    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
    }
    
    public static PixelmonDetailedLegendaryNotifications getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
