package eatAWESOME.pixelmonspawnalerts;

import eatAWESOME.pixelmonspawnalerts.capabilities.CapabilityHandler;
import eatAWESOME.pixelmonspawnalerts.commands.SpawnAlert;
import eatAWESOME.pixelmonspawnalerts.events.RaidListener;
import eatAWESOME.pixelmonspawnalerts.events.SpawnListener;

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

@Mod(PixelmonSpawnAlerts.MOD_ID)
@Mod.EventBusSubscriber(modid = PixelmonSpawnAlerts.MOD_ID)
public class PixelmonSpawnAlerts {

	public static final String MOD_ID = "pixelmonspawnalerts";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	private static PixelmonSpawnAlerts instance;
	
    public PixelmonSpawnAlerts() {
        instance = this;
    	reloadConfig();
        MinecraftForge.EVENT_BUS.register(this);
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(PixelmonSpawnAlerts::onModLoad);
    }

    public static void onModLoad(FMLCommonSetupEvent event) {
    	CapabilityHandler.register();
    	Pixelmon.EVENT_BUS.register(new SpawnListener());
    	Pixelmon.EVENT_BUS.register(new RaidListener());
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
    	SpawnAlert.register(event.getDispatcher());
    }
    
    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
    }
    
    public static PixelmonSpawnAlerts getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
