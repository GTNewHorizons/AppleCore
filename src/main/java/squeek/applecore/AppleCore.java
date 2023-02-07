package squeek.applecore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import squeek.applecore.api_impl.AppleCoreAccessorMutatorImpl;
import squeek.applecore.api_impl.AppleCoreDispatcherImpl;
import squeek.applecore.api_impl.AppleCoreRegistryImpl;
import squeek.applecore.client.DebugInfoHandler;
import squeek.applecore.client.HUDOverlayHandler;
import squeek.applecore.client.TooltipOverlayHandler;
import squeek.applecore.commands.Commands;
import squeek.applecore.mixinplugin.Mixins;
import squeek.applecore.network.SyncHandler;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.Side;

@IFMLLoadingPlugin.SortingIndex(1100)
@IFMLLoadingPlugin.MCVersion("1.7.10")
@Mod(
        modid = ModInfo.MODID,
        name = ModInfo.MODID,
        version = ModInfo.VERSION,
        acceptableRemoteVersions = "*",
        guiFactory = ModInfo.GUI_FACTORY_CLASS,
        acceptedMinecraftVersions = "[1.7.10]")
public class AppleCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static Logger Log = LogManager.getLogger(ModInfo.MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // force initialization of the singletons
        AppleCoreAccessorMutatorImpl.values();
        AppleCoreDispatcherImpl.values();
        AppleCoreRegistryImpl.values();

        FMLCommonHandler.instance().bus().register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        SyncHandler.init();
        if (event.getSide() == Side.CLIENT) {
            DebugInfoHandler.init();
            HUDOverlayHandler.init();
            FMLCommonHandler.instance().bus().register(new TooltipOverlayHandler());
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

    @Override
    public String getMixinConfig() {
        return "mixins.AppleCore.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        ModConfig.init(new File(Launch.minecraftHome, "config/" + ModInfo.MODID + ".cfg"));
        // Manual coremod identification
        try {
            Class.forName("codechicken.lib.asm.ModularASMTransformer");
            loadedCoreMods.add("codechicken.lib");
        } catch (ClassNotFoundException ignored) {}

        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.EARLY) {
                if (mixin.shouldLoad(loadedCoreMods, Collections.emptySet())) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        Log.info("Not loading the following EARLY mixins: {}", notLoading.toString());
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
