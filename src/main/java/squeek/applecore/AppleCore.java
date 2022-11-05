package squeek.applecore;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.applecore.api_impl.AppleCoreAccessorMutatorImpl;
import squeek.applecore.api_impl.AppleCoreDispatcherImpl;
import squeek.applecore.api_impl.AppleCoreRegistryImpl;
import squeek.applecore.client.DebugInfoHandler;
import squeek.applecore.client.HUDOverlayHandler;
import squeek.applecore.client.TooltipOverlayHandler;
import squeek.applecore.commands.Commands;
import squeek.applecore.network.SyncHandler;

@Mod(
        modid = ModInfo.MODID,
        name = ModInfo.MODID,
        version = ModInfo.VERSION,
        acceptableRemoteVersions = "*",
        guiFactory = ModInfo.GUI_FACTORY_CLASS,
        acceptedMinecraftVersions = "[1.7.10]")
public class AppleCore {
    public static Logger Log = LogManager.getLogger(ModInfo.MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // force initialization of the singletons
        AppleCoreAccessorMutatorImpl.values();
        AppleCoreDispatcherImpl.values();
        AppleCoreRegistryImpl.values();

        FMLCommonHandler.instance().bus().register(new AppleCore());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        SyncHandler.init();

        if (event.getSide() == Side.CLIENT) {
            DebugInfoHandler.init();
            HUDOverlayHandler.init();
            TooltipOverlayHandler.init();
        }
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        Commands.init(event.getServer());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModInfo.MODID)) ModConfig.sync();
    }
}
