package squeek.applecore.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import java.util.Arrays;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import squeek.applecore.ModConfig;
import squeek.applecore.ModInfo;

public class GuiAppleCoreConfig extends GuiConfig {

    public GuiAppleCoreConfig(GuiScreen parentScreen) {
        super(
                parentScreen,
                Arrays.asList(new IConfigElement[] {
                    new ConfigElement<>(ModConfig.config.getCategory(ModConfig.CATEGORY_CLIENT)),
                    new ConfigElement<>(ModConfig.config.getCategory(ModConfig.CATEGORY_SERVER)),
                    new ConfigElement<>(ModConfig.config.getCategory(ModConfig.CATEGORY_GENERAL))
                }),
                ModInfo.MODID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ModConfig.config.toString()));
    }
}
