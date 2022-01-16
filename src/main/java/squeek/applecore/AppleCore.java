package squeek.applecore;

import java.io.InputStream;
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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ModInfo.MODID, name = ModInfo.MODID, version = ModInfo.VERSION, acceptableRemoteVersions="*", guiFactory = ModInfo.GUI_FACTORY_CLASS)
public class AppleCore
{
	public static Logger Log = LogManager.getLogger(ModInfo.MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// too lazy to figure out a real solution for this (@Mod enforces mcmod.info filename)
		// this will at least allow the metadata to populate the mod listing, though
		InputStream is = MetadataCollection.class.getResourceAsStream("/mcmod.info");
		MetadataCollection metadataCollection = MetadataCollection.from(is, ModInfo.MODID);
		Loader.instance().activeModContainer().bindMetadata(metadataCollection);

		// force initialization of the singletons
		AppleCoreAccessorMutatorImpl.values();
		AppleCoreDispatcherImpl.values();
		AppleCoreRegistryImpl.values();

		ModConfig.init(event.getSuggestedConfigurationFile());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		SyncHandler.init();

		if (event.getSide() == Side.CLIENT)
		{
			DebugInfoHandler.init();
			HUDOverlayHandler.init();
			TooltipOverlayHandler.init();
		}
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		Commands.init(event.getServer());
	}
}
