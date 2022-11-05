package squeek.applecore.mixinplugin;

import static squeek.applecore.mixinplugin.TargetedMod.*;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.util.Arrays;
import java.util.List;

public enum Mixin {

    //
    // IMPORTANT: Do not make any references to any mod from this file. This file is loaded quite early on and if
    // you refer to other mods you load them as well. The consequence is: You can't inject any previously loaded
    // classes!
    // Exception: Tags.java, as long as it is used for Strings only!
    //
    BlockCactusMixin("minecraft.BlockCactusMixin", VANILLA),
    BlockCakeMixin("minecraft.BlockCakeMixin", VANILLA),
    BlockCocoaMixin("minecraft.BlockCocoaMixin", VANILLA),
    BlockCropsMixin("minecraft.BlockCropsMixin", VANILLA),
    BlockMushroomMixin("minecraft.BlockMushroomMixin", VANILLA),
    BlockNetherWartMixin("minecraft.BlockNetherWartMixin", VANILLA),
    BlockReedMixin("minecraft.BlockReedMixin", VANILLA),
    BlockSaplingMixin("minecraft.BlockSaplingMixin", VANILLA),
    BlockStemMixin("minecraft.BlockStemMixin", VANILLA),
    EntityPlayerMixin("minecraft.EntityPlayerMixin", VANILLA),
    FoodStatsMixin("minecraft.FoodStatsMixin", VANILLA),
    GuiScreenMixin("minecraft.GuiScreenMixin", Side.CLIENT, VANILLA),
    ItemRendererMixin("minecraft.ItemRendererMixin", Side.CLIENT, VANILLA),

    GuiDrawMixin("codechickenlib.GuiDrawMixin", Side.CLIENT, CODECHICKEN_LIB),

    BlockPamFruitMixin("harvestcraft.BlockPamFruitMixin", HARVESTCRAFT),
    BlockPamSaplingMixin("harvestcraft.BlockPamSaplingMixin", HARVESTCRAFT),

    BerryBushMixin("natura.BerryBushMixin", NATURA),
    NetherBerryBushMixin("natura.NetherBerryBushMixin", NATURA);

    public final String mixinClass;
    public final List<TargetedMod> targetedMods;
    private final Side side;

    Mixin(String mixinClass, Side side, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = side;
    }

    Mixin(String mixinClass, TargetedMod... targetedMods) {
        this.mixinClass = mixinClass;
        this.targetedMods = Arrays.asList(targetedMods);
        this.side = Side.BOTH;
    }

    public boolean shouldLoad(List<TargetedMod> loadedMods) {
        return (side == Side.BOTH
                        || side == Side.SERVER && FMLLaunchHandler.side().isServer()
                        || side == Side.CLIENT && FMLLaunchHandler.side().isClient())
                && loadedMods.containsAll(targetedMods);
    }
}

enum Side {
    BOTH,
    CLIENT,
    SERVER;
}
