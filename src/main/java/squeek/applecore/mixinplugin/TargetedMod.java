package squeek.applecore.mixinplugin;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

public enum TargetedMod implements ITargetMod {

    // spotless:off
    CODECHICKEN_LIB_NEW_DRAWTOOLTIPBOX(new TargetModBuilder().setTargetClass("codechicken.lib.gui.GuiDraw").setClassNodeTest(
            // newest version of codechicken adds a second "drawTooltipBox" method with the descriptor "(IIIIIIII)V"
            // so we apply our mixin only if the new method is present
            cn -> cn.methods.stream().anyMatch(mn -> "drawTooltipBox".equals(mn.name) && "(IIIIIIII)V".equals(mn.desc)))
    ),
    CODECHICKEN_LIB("codechicken.lib", null),
    HARVESTCRAFT(null, "harvestcraft"),
    NATURA(null, "Natura");
    // spotless:on

    private final TargetModBuilder builder;

    TargetedMod(TargetModBuilder builder) {
        this.builder = builder;
    }

    TargetedMod(String coreModClass, String modId) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass).setModId(modId);
    }

    @Nonnull
    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
