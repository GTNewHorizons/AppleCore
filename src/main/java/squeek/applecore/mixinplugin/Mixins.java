package squeek.applecore.mixinplugin;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // spotless:off
    MINECRAFT(new MixinBuilder()
            .addCommonMixins(
                    "minecraft.BlockCactusMixin",
                    "minecraft.BlockCakeMixin",
                    "minecraft.BlockCocoaMixin",
                    "minecraft.BlockCropsMixin",
                    "minecraft.BlockMushroomMixin",
                    "minecraft.BlockNetherWartMixin",
                    "minecraft.BlockReedMixin",
                    "minecraft.BlockSaplingMixin",
                    "minecraft.BlockStemMixin",
                    "minecraft.EntityPlayerMixin",
                    "minecraft.FoodStatsMixin",
                    "minecraft.accessors.FoodStatsAccessor")
            .addClientMixins(
                    "minecraft.GuiScreenMixin",
                    "minecraft.ItemRendererMixin")
            .setPhase(Phase.EARLY)),
    GuiDrawMixinLegacy(new MixinBuilder()
            .addClientMixins("codechickenlib.GuiDrawMixinLegacy")
            .addRequiredMod(TargetedMod.CODECHICKEN_LIB)
            .addExcludedMod(TargetedMod.CODECHICKEN_LIB_NEW_DRAWTOOLTIPBOX)
            .setPhase(Phase.EARLY)),
    GuiDrawMixin(new MixinBuilder()
            .addClientMixins("codechickenlib.GuiDrawMixin")
            .addRequiredMod(TargetedMod.CODECHICKEN_LIB)
            .addRequiredMod(TargetedMod.CODECHICKEN_LIB_NEW_DRAWTOOLTIPBOX)
            .setPhase(Phase.EARLY)),
    BlockPamFruitMixin(new MixinBuilder()
            .addCommonMixins("harvestcraft.BlockPamFruitMixin")
            .addRequiredMod(TargetedMod.HARVESTCRAFT)
            .setPhase(Phase.LATE)),
    BlockPamSaplingMixin(new MixinBuilder()
            .addCommonMixins("harvestcraft.BlockPamSaplingMixin")
            .addRequiredMod(TargetedMod.HARVESTCRAFT)
            .setPhase(Phase.LATE)),
    BerryBushMixin(new MixinBuilder()
            .addCommonMixins("natura.BerryBushMixin")
            .addRequiredMod(TargetedMod.NATURA)
            .setPhase(Phase.LATE)),
    NetherBerryBushMixin(new MixinBuilder()
            .addCommonMixins("natura.NetherBerryBushMixin")
            .addRequiredMod(TargetedMod.NATURA)
            .setPhase(Phase.LATE));
    // spotless:on

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
