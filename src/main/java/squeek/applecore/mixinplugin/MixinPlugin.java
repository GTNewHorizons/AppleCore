package squeek.applecore.mixinplugin;

import cpw.mods.fml.common.FMLCommonHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.libraries.org.objectweb.asm.tree.ClassNode;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;
import squeek.applecore.ModConfig;
import squeek.applecore.ModInfo;

public class MixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOG = LogManager.getLogger(ModInfo.MODID + " mixins");
    private static final Path MODS_DIRECTORY_PATH = new File(Launch.minecraftHome, "mods/").toPath();

    @Override
    public void onLoad(String mixinPackage) {
        ModConfig.init(new File(Launch.minecraftHome, "config/" + ModInfo.MODID + ".cfg"));
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    // This method return a List<String> of mixins. Every mixins in this list will be loaded.
    @Override
    public List<String> getMixins() {
        final boolean isDevelopmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        List<TargetedMod> loadedMods = Arrays.stream(TargetedMod.values())
                .filter(mod -> mod == TargetedMod.VANILLA
                        || (mod.loadInDevelopment && isDevelopmentEnvironment)
                        || loadJarOf(mod))
                .collect(Collectors.toList());

        for (TargetedMod mod : TargetedMod.values()) {
            if (loadedMods.contains(mod)) {
                LOG.info("Found " + mod.modName + "! Integrating now...");
            } else if (ArrayUtils.contains(ModConfig.REQUIRED_MODS, mod.modName) && !isDevelopmentEnvironment) {
                LOG.error(
                        "CRITICAL ERROR: Could not find required jar {}.  If this mod is not required please remove it from the 'requiredMods' section of the config.",
                        mod.modName);
                FMLCommonHandler.instance().exitJava(-1, true);
            } else {
                LOG.info("Could not find " + mod.modName + "! Skipping mixins....");
            }
        }

        List<String> mixins = new ArrayList<>();
        for (Mixin mixin : Mixin.values()) {
            if (mixin.shouldLoad(loadedMods)) {
                mixins.add(mixin.mixinClass);
                LOG.debug("Loading mixin: " + mixin.mixinClass);
            }
        }
        return mixins;
    }

    private boolean loadJarOf(final TargetedMod mod) {
        try {
            File jar = findJarOf(mod);
            if (jar == null) {
                LOG.warn("Jar not found for " + mod);
                return false;
            }

            LOG.info("Attempting to add " + jar + " to the URL Class Path");
            if (!jar.exists()) {
                throw new FileNotFoundException(jar.toString());
            }
            MinecraftURLClassPath.addJar(jar);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File findJarOf(final TargetedMod mod) {
        try {
            return Files.walk(MODS_DIRECTORY_PATH)
                    .filter(mod::isMatchingJar)
                    .map(Path::toFile)
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
